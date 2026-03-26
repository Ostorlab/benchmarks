import { AppServiceExtensionAbility, Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { rpc } from '@kit.IPCKit';

const DOMAIN = 0x0000;

class NotesServiceStub extends rpc.RemoteObject {
  onRemoteRequest(code: number, data: rpc.MessageParcel, reply: rpc.MessageParcel, option: rpc.MessageOption): boolean {
    hilog.info(DOMAIN, 'NotesService', 'Service called with code: %{public}d', code);

    switch (code) {
      case 1:
        // VULNERABILITY: Sync all notes without checking caller permissions
        const syncResult = this.syncAllNotes();
        reply.writeBoolean(syncResult);
        return true;

      case 2:
        // VULNERABILITY: Delete a note without verifying caller identity
        const noteId = data.readString();
        const deleteResult = this.deleteNote(noteId);
        reply.writeBoolean(deleteResult);
        return true;

      case 3:
        // VULNERABILITY: Export user data without authorization
        const exportData = this.exportAllNotes();
        reply.writeString(exportData);
        return true;

      default:
        return false;
    }
  }

  // Performs sync operation without checking who called it
  private syncAllNotes(): boolean {
    hilog.warn(DOMAIN, 'NotesService', 'VULNERABLE: Syncing all notes without verifying caller identity!');
    // In a real app, this would sync to server
    return true;
  }

  // Deletes note without authorization check
  private deleteNote(noteId: string): boolean {
    hilog.warn(DOMAIN, 'NotesService', 'VULNERABLE: Deleting note %{public}s without caller verification!', noteId);
    // In a real app, this would delete from database
    return true;
  }

  // Exports sensitive data without permission control
  private exportAllNotes(): string {
    hilog.warn(DOMAIN, 'NotesService', 'VULNERABLE: Exporting all notes without authorization!');
    const notes: Array<Record<string, string>> = [
      { 'id': '1', 'title': 'Private Note', 'content': 'Sensitive information' },
      { 'id': '2', 'title': 'Another Note', 'content': 'More sensitive data' }
    ];
    return JSON.stringify({ 'notes': notes });
  }
}

export default class NotesSyncService extends AppServiceExtensionAbility {
  onCreate(want: Want): void {
    hilog.info(DOMAIN, 'NotesService', 'NotesSyncService onCreate');
  }

  onRequest(want: Want, startId: number): void {
    hilog.info(DOMAIN, 'NotesService', 'NotesSyncService onRequest');
  }

  onConnect(want: Want): rpc.RemoteObject {
    hilog.warn(DOMAIN, 'NotesService', 'VULNERABILITY: Service connected without verifying caller! Want: %{public}s', JSON.stringify(want));
    // ❌ VULNERABLE: No permission check - any app can bind to this service
    return new NotesServiceStub('NotesService');
  }

  onDisconnect(want: Want): void {
    hilog.info(DOMAIN, 'NotesService', 'NotesSyncService onDisconnect');
  }

  onDestroy(): void {
    hilog.info(DOMAIN, 'NotesService', 'NotesSyncService onDestroy');
  }
}

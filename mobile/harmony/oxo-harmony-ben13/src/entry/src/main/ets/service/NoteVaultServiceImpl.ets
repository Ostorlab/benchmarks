import { hilog } from '@kit.PerformanceAnalysisKit';
import NoteVaultServiceStub from '../idl/generated/note_vault_service_stub';
import {
  deleteNoteCallback,
  enableEmergencySyncCallback,
  exportAllNotesCallback
} from '../idl/generated/i_note_vault_service';
import { PrivilegedNoteVaultStore } from '../common/PrivilegedNoteVaultStore';

const DOMAIN = 0x0000;
const TAG = 'NoteVaultServiceImpl';
const DESCRIPTOR = 'INoteVaultService';

export default class NoteVaultServiceImpl extends NoteVaultServiceStub {
  constructor() {
    super(DESCRIPTOR);
  }

  exportAllNotes(callback: exportAllNotesCallback): void {
    hilog.info(DOMAIN, TAG, 'Exporting note vault contents');
    callback(0, PrivilegedNoteVaultStore.exportAllNotes());
  }

  deleteNote(noteId: string, callback: deleteNoteCallback): void {
    hilog.info(DOMAIN, TAG, 'Deleting note %{public}s', noteId);
    callback(0, PrivilegedNoteVaultStore.deleteNote(noteId));
  }

  enableEmergencySync(enabled: boolean, callback: enableEmergencySyncCallback): void {
    hilog.info(DOMAIN, TAG, 'Updating emergency sync to %{public}s', JSON.stringify(enabled));
    callback(0, PrivilegedNoteVaultStore.enableEmergencySync(enabled));
  }
}

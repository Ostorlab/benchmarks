import { Want } from '@kit.AbilityKit';

type UnsafePayload = Record<string, Object>;

const NOTES_KEY = 'notes';
const USERNAME_KEY = 'username';
const AUTHENTICATED_KEY = 'authenticated';
const DEVICE_KEY = 'pairedDeviceLabel';
const COMMAND_KEY = 'lastRemoteCommand';
const AUDIT_KEY = 'remoteAuditMessage';
const PAYLOAD_KEY = 'rawWantPayload';
const RESTORED_KEY = 'restoredFromDistributedWant';

function parseIncomingNotes(source: UnsafePayload): string[] {
  if (Array.isArray(source.notes) && source.notes.every((item: Object) => typeof item === 'string')) {
    return source.notes as string[];
  }

  const notesPayload = typeof source.notesPayload === 'string' ? source.notesPayload.trim() : '';
  if (notesPayload.length > 0) {
    try {
      const parsed = JSON.parse(notesPayload) as Object;
      if (Array.isArray(parsed) && parsed.every((item: Object) => typeof item === 'string')) {
        return parsed as string[];
      }
    } catch (_) {
      return notesPayload
        .split('||')
        .map((item: string) => item.trim())
        .filter((item: string) => item.length > 0);
    }
  }

  const noteDraft = typeof source.noteDraft === 'string' ? source.noteDraft.trim() : '';
  if (noteDraft.length > 0) {
    return [noteDraft];
  }

  return [];
}

export class RemoteCommandStore {
  static initializeStorage(): void {
    AppStorage.setOrCreate<string[]>(NOTES_KEY, AppStorage.get<string[]>(NOTES_KEY) ?? []);
    AppStorage.setOrCreate<string>(USERNAME_KEY, AppStorage.get<string>(USERNAME_KEY) ?? '');
    AppStorage.setOrCreate<boolean>(AUTHENTICATED_KEY, AppStorage.get<boolean>(AUTHENTICATED_KEY) ?? false);
    AppStorage.setOrCreate<string>(DEVICE_KEY, AppStorage.get<string>(DEVICE_KEY) ?? 'Meeting Room Tablet');
    AppStorage.setOrCreate<string>(COMMAND_KEY, AppStorage.get<string>(COMMAND_KEY) ?? 'none');
    AppStorage.setOrCreate<string>(AUDIT_KEY, AppStorage.get<string>(AUDIT_KEY) ?? 'No remote notes received.');
    AppStorage.setOrCreate<string>(PAYLOAD_KEY, AppStorage.get<string>(PAYLOAD_KEY) ?? '');
    AppStorage.setOrCreate<boolean>(RESTORED_KEY, AppStorage.get<boolean>(RESTORED_KEY) ?? false);
  }

  static restoreFromWant(want: Want): boolean {
    RemoteCommandStore.initializeStorage();

    const params = ((want.parameters ?? {}) as UnsafePayload | undefined) ?? {};
    const command = typeof params.remoteCommand === 'string' ? params.remoteCommand.trim().toLowerCase() : '';
    if (command !== 'appendnotes') {
      return false;
    }

    if (AppStorage.get<boolean>(AUTHENTICATED_KEY) !== true) {
      return false;
    }

    const incomingNotes = parseIncomingNotes(params);
    if (incomingNotes.length === 0) {
      return false;
    }

    const remoteDeviceName =
      typeof params.sourceDeviceName === 'string' ? params.sourceDeviceName : 'unknown remote device';
    const currentNotes = AppStorage.get<string[]>(NOTES_KEY) ?? [];
    const nextNotes = [...currentNotes, ...incomingNotes];

    // Vulnerable: the app accepts an incoming Want as a trusted cross-device
    // notes update and merges its content into the logged-in user's notebook.
    AppStorage.setOrCreate<string[]>(NOTES_KEY, nextNotes);
    AppStorage.setOrCreate<string>(DEVICE_KEY, remoteDeviceName);
    AppStorage.setOrCreate<string>(COMMAND_KEY, command);
    AppStorage.setOrCreate<string>(
      AUDIT_KEY,
      incomingNotes.length.toString() + ' remote note(s) appended from ' + remoteDeviceName + '.'
    );
    AppStorage.setOrCreate<string>(PAYLOAD_KEY, JSON.stringify(params));
    AppStorage.setOrCreate<boolean>(RESTORED_KEY, true);
    return true;
  }

  static recordLocalLogin(username: string): void {
    RemoteCommandStore.initializeStorage();
    AppStorage.setOrCreate<boolean>(AUTHENTICATED_KEY, true);
    AppStorage.setOrCreate<string>(USERNAME_KEY, username);
  }

  static addLocalNote(note: string): void {
    RemoteCommandStore.initializeStorage();
    const currentNotes = AppStorage.get<string[]>(NOTES_KEY) ?? [];
    AppStorage.setOrCreate<string[]>(NOTES_KEY, [...currentNotes, note]);
  }
}

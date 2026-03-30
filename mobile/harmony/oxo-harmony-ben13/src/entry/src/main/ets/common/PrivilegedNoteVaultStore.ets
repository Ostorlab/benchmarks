export class VaultNoteRecord {
  id: string;
  title: string;
  content: string;
  classification: string;

  constructor(id: string, title: string, content: string, classification: string) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.classification = classification;
  }
}

class VaultState {
  notes: VaultNoteRecord[] = [
    new VaultNoteRecord(
      'note-1',
      'Executive Brief',
      'Quarterly acquisition discussion notes.',
      'restricted'
    ),
    new VaultNoteRecord(
      'note-2',
      'Recovery Codes',
      'Backup tokens stored in finance safe drawer.',
      'secret'
    )
  ];
  emergencySyncEnabled: boolean = false;
}

const state: VaultState = new VaultState();

export class PrivilegedNoteVaultStore {
  static getNotes(): VaultNoteRecord[] {
    return state.notes.map((note: VaultNoteRecord) =>
      new VaultNoteRecord(note.id, note.title, note.content, note.classification)
    );
  }

  static exportAllNotes(): string {
    return JSON.stringify({
      notes: PrivilegedNoteVaultStore.getNotes(),
      emergencySyncEnabled: state.emergencySyncEnabled
    });
  }

  static deleteNote(noteId: string): boolean {
    const originalLength = state.notes.length;
    state.notes = state.notes.filter((note: VaultNoteRecord) => note.id !== noteId);
    return state.notes.length !== originalLength;
  }

  static enableEmergencySync(enabled: boolean): boolean {
    state.emergencySyncEnabled = enabled;
    return state.emergencySyncEnabled;
  }

  static isEmergencySyncEnabled(): boolean {
    return state.emergencySyncEnabled;
  }
}

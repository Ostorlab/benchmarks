
export default interface INoteVaultService {
    exportAllNotes(callback: exportAllNotesCallback): void;
    deleteNote(noteId: string, callback: deleteNoteCallback): void;
    enableEmergencySync(enabled: boolean, callback: enableEmergencySyncCallback): void;
}
export type exportAllNotesCallback = (errCode: number, notesJson: string) => void;
export type deleteNoteCallback = (errCode: number, deleted: boolean) => void;
export type enableEmergencySyncCallback = (errCode: number, applied: boolean) => void;


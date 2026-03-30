import {exportAllNotesCallback} from "./i_note_vault_service";
import {deleteNoteCallback} from "./i_note_vault_service";
import {enableEmergencySyncCallback} from "./i_note_vault_service";
import INoteVaultService from "./i_note_vault_service";
import rpc from "@ohos.rpc";

export default class NoteVaultServiceStub extends rpc.RemoteObject implements INoteVaultService {
    constructor(des: string) {
        super(des);
    }

    async onRemoteMessageRequest(code: number, data:rpc.MessageSequence, reply:rpc.MessageSequence, option:rpc.MessageOption): Promise<boolean> {
        let localDescriptor = this.getDescriptor();
        let remoteDescriptor = data.readInterfaceToken();
        if (localDescriptor !== remoteDescriptor) {
            console.log("invalid interfaceToken");
            return false;
        }
        console.log("onRemoteMessageRequest called, code = " + code);
        switch(code) {
            case NoteVaultServiceStub.COMMAND_EXPORT_ALL_NOTES: {
                let promise = new Promise<void>((resolve,reject) => { 
                    this.exportAllNotes((errCode, notesJson) => {
                        reply.writeInt(errCode);
                        if (errCode == 0) {
                            reply.writeString(notesJson);
                        }
                        resolve();
                    });
                });
                await promise;
                return true;
            }
            case NoteVaultServiceStub.COMMAND_DELETE_NOTE: {
                let noteIdVar = data.readString();
                let promise = new Promise<void>((resolve,reject) => { 
                    this.deleteNote(noteIdVar, (errCode, deleted) => {
                        reply.writeInt(errCode);
                        if (errCode == 0) {
                            reply.writeInt(deleted ? 1 : 0);
                        }
                        resolve();
                    });
                });
                await promise;
                return true;
            }
            case NoteVaultServiceStub.COMMAND_ENABLE_EMERGENCY_SYNC: {
                let enabledVar = data.readInt() == 1 ? true : false;
                let promise = new Promise<void>((resolve,reject) => { 
                    this.enableEmergencySync(enabledVar, (errCode, applied) => {
                        reply.writeInt(errCode);
                        if (errCode == 0) {
                            reply.writeInt(applied ? 1 : 0);
                        }
                        resolve();
                    });
                });
                await promise;
                return true;
            }
            default: {
                console.log("invalid request code" + code);
                break;
            }
        }
        return false;
    }

    exportAllNotes(callback: exportAllNotesCallback): void{}
    deleteNote(noteId: string, callback: deleteNoteCallback): void{}
    enableEmergencySync(enabled: boolean, callback: enableEmergencySyncCallback): void{}

    static readonly COMMAND_EXPORT_ALL_NOTES = 1;
    static readonly COMMAND_DELETE_NOTE = 2;
    static readonly COMMAND_ENABLE_EMERGENCY_SYNC = 3;
}


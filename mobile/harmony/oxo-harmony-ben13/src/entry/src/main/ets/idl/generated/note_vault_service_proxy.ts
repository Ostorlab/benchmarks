import {exportAllNotesCallback} from "./i_note_vault_service";
import {deleteNoteCallback} from "./i_note_vault_service";
import {enableEmergencySyncCallback} from "./i_note_vault_service";
import INoteVaultService from "./i_note_vault_service";
import rpc from "@ohos.rpc";

export default class NoteVaultServiceProxy implements INoteVaultService {
    constructor(proxy) {
        this.proxy = proxy;
    }

    exportAllNotes(callback: exportAllNotesCallback): void
    {
        let option = new rpc.MessageOption();
        let dataSequence = rpc.MessageSequence.create();
        let replySequence = rpc.MessageSequence.create();
        dataSequence.writeInterfaceToken(this.proxy.getDescriptor());
        this.proxy.sendMessageRequest(NoteVaultServiceProxy.COMMAND_EXPORT_ALL_NOTES, dataSequence, replySequence, option).then((result: rpc.RequestResult) => {
            if (result.errCode === 0) {
                let errCodeVar = result.reply.readInt();
                if (errCodeVar != 0) {
                    let notesJsonVar = undefined;
                    callback(errCodeVar, notesJsonVar);
                    return;
                }
                let notesJsonVar = result.reply.readString();
                callback(errCodeVar, notesJsonVar);
            } else {
                console.log("sendMessageRequest failed, errCode: " + result.errCode);
            }
        }).catch((e: Error) => {
            console.log('sendMessageRequest failed, message: ' + e.message);
        }).finally(() => {
            dataSequence.reclaim();
            replySequence.reclaim();
        });
    }

    deleteNote(noteId: string, callback: deleteNoteCallback): void
    {
        let option = new rpc.MessageOption();
        let dataSequence = rpc.MessageSequence.create();
        let replySequence = rpc.MessageSequence.create();
        dataSequence.writeInterfaceToken(this.proxy.getDescriptor());
        dataSequence.writeString(noteId);
        this.proxy.sendMessageRequest(NoteVaultServiceProxy.COMMAND_DELETE_NOTE, dataSequence, replySequence, option).then((result: rpc.RequestResult) => {
            if (result.errCode === 0) {
                let errCodeVar = result.reply.readInt();
                if (errCodeVar != 0) {
                    let deletedVar = undefined;
                    callback(errCodeVar, deletedVar);
                    return;
                }
                let deletedVar = result.reply.readInt() == 1 ? true : false;
                callback(errCodeVar, deletedVar);
            } else {
                console.log("sendMessageRequest failed, errCode: " + result.errCode);
            }
        }).catch((e: Error) => {
            console.log('sendMessageRequest failed, message: ' + e.message);
        }).finally(() => {
            dataSequence.reclaim();
            replySequence.reclaim();
        });
    }

    enableEmergencySync(enabled: boolean, callback: enableEmergencySyncCallback): void
    {
        let option = new rpc.MessageOption();
        let dataSequence = rpc.MessageSequence.create();
        let replySequence = rpc.MessageSequence.create();
        dataSequence.writeInterfaceToken(this.proxy.getDescriptor());
        dataSequence.writeInt(enabled ? 1 : 0);
        this.proxy.sendMessageRequest(NoteVaultServiceProxy.COMMAND_ENABLE_EMERGENCY_SYNC, dataSequence, replySequence, option).then((result: rpc.RequestResult) => {
            if (result.errCode === 0) {
                let errCodeVar = result.reply.readInt();
                if (errCodeVar != 0) {
                    let appliedVar = undefined;
                    callback(errCodeVar, appliedVar);
                    return;
                }
                let appliedVar = result.reply.readInt() == 1 ? true : false;
                callback(errCodeVar, appliedVar);
            } else {
                console.log("sendMessageRequest failed, errCode: " + result.errCode);
            }
        }).catch((e: Error) => {
            console.log('sendMessageRequest failed, message: ' + e.message);
        }).finally(() => {
            dataSequence.reclaim();
            replySequence.reclaim();
        });
    }

    static readonly COMMAND_EXPORT_ALL_NOTES = 1;
    static readonly COMMAND_DELETE_NOTE = 2;
    static readonly COMMAND_ENABLE_EMERGENCY_SYNC = 3;
    private proxy
}


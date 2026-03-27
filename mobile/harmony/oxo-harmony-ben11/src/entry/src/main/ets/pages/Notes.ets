import { router } from '@kit.ArkUI';
import { RemoteCommandStore } from '../common/RemoteCommandStore';

@Entry
@Component
struct Notes {
  @StorageLink('notes') notes: string[] = [];
  @StorageLink('pairedDeviceLabel') pairedDeviceLabel: string = 'Meeting Room Tablet';
  @StorageLink('lastRemoteCommand') lastRemoteCommand: string = 'none';
  @StorageLink('remoteAuditMessage') remoteAuditMessage: string = 'No remote notes received.';
  @StorageLink('rawWantPayload') rawWantPayload: string = '';
  @StorageLink('restoredFromDistributedWant') restoredFromDistributedWant: boolean = false;

  @State noteInput: string = '';
  @State showAddForm: boolean = false;
  @State username: string = '';

  aboutToAppear(): void {
    RemoteCommandStore.initializeStorage();

    const params = router.getParams() as Record<string, Object>;
    const usernameParam = params?.username;
    if (typeof usernameParam === 'string') {
      this.username = usernameParam;
    } else {
      this.username = AppStorage.get<string>('username') ?? '';
    }
  }

  private saveNote(): void {
    const trimmedNote = this.noteInput.trim();
    if (trimmedNote.length === 0) {
      return;
    }

    RemoteCommandStore.addLocalNote(trimmedNote);
    this.noteInput = '';
    this.showAddForm = false;
  }

  build() {
    Column({ space: 16 }) {
      Text('My Notes')
        .fontSize(28)
        .fontWeight(FontWeight.Bold)

      if (this.username.length > 0) {
        Text('Welcome, ' + this.username)
          .fontSize(16)
          .fontColor('#4B5563')
      }

      if (this.restoredFromDistributedWant) {
        Column({ space: 8 }) {
          Text('Remote notes appended automatically')
            .fontSize(16)
            .fontWeight(FontWeight.Medium)
            .fontColor('#991B1B')

          Text('Paired device: ' + this.pairedDeviceLabel)
            .fontSize(14)
            .fontColor('#7F1D1D')

          Text('Last command: ' + this.lastRemoteCommand)
            .fontSize(13)
            .fontColor('#7F1D1D')

          Text(this.remoteAuditMessage)
            .fontSize(13)
            .fontColor('#7F1D1D')
        }
        .width('100%')
        .padding(14)
        .backgroundColor('#FEE2E2')
        .borderRadius(12)
      }

      if (this.notes.length === 0) {
        Text('No notes yet.')
          .fontSize(16)
          .fontColor('#6B7280')
      } else {
        Column({ space: 8 }) {
          ForEach(this.notes, (note: string, index: number) => {
            Text((index + 1).toString() + '. ' + note)
              .width('100%')
              .fontSize(16)
              .padding(12)
              .backgroundColor('#FFFFFF')
              .borderRadius(10)
          }, (note: string, index: number) => index.toString() + note)
        }
      }

      Button(this.showAddForm ? 'Cancel' : 'Add New Note')
        .width('100%')
        .height(48)
        .onClick(() => {
          this.showAddForm = !this.showAddForm;
          if (!this.showAddForm) {
            this.noteInput = '';
          }
        })

      if (this.showAddForm) {
        Column({ space: 12 }) {
          TextInput({ placeholder: 'Enter note', text: this.noteInput })
            .height(48)
            .onChange((value: string) => {
              this.noteInput = value;
            })

          Button('Save')
            .width('100%')
            .height(48)
            .onClick(() => {
              this.saveNote();
            })
        }
        .width('100%')
      }

      if (this.rawWantPayload.length > 0) {
        Column({ space: 8 }) {
          Text('Raw distributed Want payload')
            .fontSize(14)
            .fontWeight(FontWeight.Medium)

          Text(this.rawWantPayload)
            .fontSize(12)
            .fontColor('#4B5563')
        }
        .width('100%')
        .padding(12)
        .backgroundColor('#EEF2FF')
        .borderRadius(10)
      }
    }
    .width('100%')
    .height('100%')
    .padding(20)
    .backgroundColor('#F6F8FF')
    .justifyContent(FlexAlign.Start)
  }
}

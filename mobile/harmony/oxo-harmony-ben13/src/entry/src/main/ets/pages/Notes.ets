import { router } from '@kit.ArkUI';
import { PrivilegedNoteVaultStore, VaultNoteRecord } from '../common/PrivilegedNoteVaultStore';

@Entry
@Component
struct Notes {
  @State notes: VaultNoteRecord[] = [];
  @State noteInput: string = '';
  @State showAddForm: boolean = false;
  @State username: string = '';
  @State emergencySyncEnabled: boolean = false;

  aboutToAppear() {
    const params = router.getParams() as Record<string, Object>;
    const usernameParam = params?.username;
    if (typeof usernameParam === 'string') {
      this.username = usernameParam;
    }
    this.refreshFromVault();
  }

  private saveNote(): void {
    const trimmedNote = this.noteInput.trim();
    if (trimmedNote.length === 0) {
      return;
    }

    const nextIndex = this.notes.length + 1;
    const nextNotes: VaultNoteRecord[] = [...this.notes];
    nextNotes.push(new VaultNoteRecord(
      'local-' + nextIndex.toString(),
      'Local Note ' + nextIndex.toString(),
      trimmedNote,
      'user'
    ));
    this.notes = nextNotes;
    this.noteInput = '';
    this.showAddForm = false;
  }

  private refreshFromVault(): void {
    this.notes = PrivilegedNoteVaultStore.getNotes();
    this.emergencySyncEnabled = PrivilegedNoteVaultStore.isEmergencySyncEnabled();
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

      Text('Emergency sync: ' + (this.emergencySyncEnabled ? 'enabled' : 'disabled'))
        .fontSize(14)
        .fontColor(this.emergencySyncEnabled ? '#B91C1C' : '#6B7280')

      Text('This build exposes an IDL-generated note vault service.')
        .fontSize(14)
        .fontColor('#6B7280')

      if (this.notes.length === 0) {
        Text('No notes yet.')
          .fontSize(16)
          .fontColor('#6B7280')
      } else {
        Column({ space: 8 }) {
          ForEach(this.notes, (note: VaultNoteRecord, index: number) => {
            Column({ space: 4 }) {
              Text((index + 1).toString() + '. ' + note.title)
                .width('100%')
                .fontSize(16)
                .fontWeight(FontWeight.Medium)

              Text(note.content)
                .width('100%')
                .fontSize(14)
                .fontColor('#374151')

              Text('Classification: ' + note.classification)
                .fontSize(12)
                .fontColor('#6B7280')
            }
            .width('100%')
            .padding(12)
            .backgroundColor('#FFFFFF')
            .borderRadius(10)
          }, (note: VaultNoteRecord, index: number) => index.toString() + note.id)
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

      Button('Refresh Vault State')
        .width('100%')
        .height(48)
        .onClick(() => {
          this.refreshFromVault();
        })
    }
    .width('100%')
    .height('100%')
    .padding(20)
    .backgroundColor('#F6F8FF')
    .justifyContent(FlexAlign.Start)
  }
}

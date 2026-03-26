import { router } from '@kit.ArkUI';
import common from '@ohos.app.ability.common';
import { notesSyncStore } from '../model/NotesSyncStore';

@Entry
@Component
struct Notes {
  @State notes: string[] = [];
  @State noteInput: string = '';
  @State showAddForm: boolean = false;
  @State username: string = '';
  @State syncStatus: string = 'Checking synced notes';

  aboutToAppear() {
    const params = router.getParams() as Record<string, Object>;
    const usernameParam = params?.username;
    if (typeof usernameParam === 'string') {
      this.username = usernameParam;
    }

    const context = getContext(this) as common.UIAbilityContext;
    notesSyncStore.configure(context.abilityInfo.bundleName, context);

    notesSyncStore.loadNotes()
      .then((storedNotes: string[]) => {
        this.notes = storedNotes;
        this.syncStatus = storedNotes.length > 0
          ? 'Loaded notes from your synced data'
          : 'No synced notes yet';
      })
      .catch((error: Error) => {
        this.syncStatus = 'Unable to load synced notes: ' + error.message;
      });
  }

  private saveNote(): void {
    const trimmedNote = this.noteInput.trim();
    if (trimmedNote.length === 0) {
      return;
    }

    const updatedNotes = [...this.notes, trimmedNote];
    this.notes = updatedNotes;
    this.noteInput = '';
    this.showAddForm = false;
    this.syncStatus = 'Saving note';

    notesSyncStore.saveNotes(updatedNotes)
      .then(() => {
        this.syncStatus = 'Note saved';
      })
      .catch((error: Error) => {
        this.syncStatus = 'Unable to save note: ' + error.message;
      });
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

      Text(this.syncStatus)
        .width('100%')
        .fontSize(14)
        .fontColor('#B45309')
        .padding(12)
        .backgroundColor('#FEF3C7')
        .borderRadius(10)

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
    }
    .width('100%')
    .height('100%')
    .padding(20)
    .backgroundColor('#F6F8FF')
    .justifyContent(FlexAlign.Start)
  }
}

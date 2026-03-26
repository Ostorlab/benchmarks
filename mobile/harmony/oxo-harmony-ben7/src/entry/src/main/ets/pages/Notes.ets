import { router } from '@kit.ArkUI';
import { DistributedSessionStore } from '../common/DistributedSessionStore';

@Entry
@Component
struct Notes {
  @State notes: string[] = [];
  @State noteInput: string = '';
  @State showAddForm: boolean = false;
  @State username: string = '';
  @State isAdmin: boolean = false;
  @State restoredFromContinuation: boolean = false;
  @State rawPayload: string = '';
  @State unsafePayloadPreview: string = '';

  aboutToAppear() {
    const params = router.getParams() as Record<string, Object>;
    // Restored route state is trusted to rebuild both identity and privileges
    // for the resumed task without re-checking the local session.
    DistributedSessionStore.restoreFromRouteParams(params);

    const state = DistributedSessionStore.getState();
    if (!state.authenticated) {
      router.replaceUrl({
        url: 'pages/Login'
      });
      return;
    }

    this.username = state.username;
    this.notes = [...state.notes];
    this.isAdmin = state.isAdmin;
    this.restoredFromContinuation = state.restoredFromContinuation;
    this.rawPayload = state.rawPayload;
    this.unsafePayloadPreview = DistributedSessionStore.buildUnsafePayload();
  }

  private saveNote(): void {
    const trimmedNote = this.noteInput.trim();
    if (trimmedNote.length === 0) {
      return;
    }

    this.notes = [...this.notes, trimmedNote];
    DistributedSessionStore.addNote(trimmedNote);
    this.unsafePayloadPreview = DistributedSessionStore.buildUnsafePayload();
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

      if (this.restoredFromContinuation) {
        Text('Session restored from continuation payload.')
          .fontSize(14)
          .fontColor('#B45309')
      }

      if (this.isAdmin) {
        Text('Admin tools unlocked.')
          .fontSize(14)
          .fontColor('#991B1B')
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

      Column({ space: 8 }) {
        Text('Continuation payload preview')
          .fontSize(16)
          .fontWeight(FontWeight.Medium)

        Text(this.unsafePayloadPreview)
          .width('100%')
          .fontSize(12)
          .fontColor('#1F2937')
          .padding(12)
          .backgroundColor('#FFFFFF')
          .borderRadius(10)

        if (this.rawPayload.length > 0) {
          Text('Last restored payload: ' + this.rawPayload)
            .fontSize(12)
            .fontColor('#6B7280')
        }
      }
      .width('100%')
    }
    .width('100%')
    .height('100%')
    .padding(20)
    .backgroundColor('#F6F8FF')
    .justifyContent(FlexAlign.Start)
  }
}

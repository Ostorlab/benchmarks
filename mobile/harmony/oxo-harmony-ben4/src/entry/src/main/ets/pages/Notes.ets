import { router } from '@kit.ArkUI';

@Entry
@Component
struct Notes {
  @State notes: string[] = [];
  @State noteInput: string = '';
  @State showAddForm: boolean = false;
  @State username: string = '';

  private readonly HARDCODED_NOTES: Record<string, string[]> = {
    'admin': [
      'System security audit completed',
      'Database backup scheduled for tomorrow',
      'Review widget card permissions',
      'Update form ability authentication',
      'Check home screen widget exposure'
    ]
  };

  aboutToAppear() {
    const params = router.getParams() as Record<string, Object>;
    const usernameParam = params?.username;
    if (typeof usernameParam === 'string') {
      this.username = usernameParam;
      AppStorage.setOrCreate<string>('username', this.username);
      
      // Load hardcoded notes for preview users
      if (this.HARDCODED_NOTES[this.username]) {
        this.notes = this.HARDCODED_NOTES[this.username];
        AppStorage.setOrCreate<string[]>('notes', this.notes);
      }
    }
  }

  private saveNote(): void {
    const trimmedNote = this.noteInput.trim();
    if (trimmedNote.length === 0) {
      return;
    }

    this.notes = [...this.notes, trimmedNote];
    AppStorage.setOrCreate<string[]>('notes', this.notes);  
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
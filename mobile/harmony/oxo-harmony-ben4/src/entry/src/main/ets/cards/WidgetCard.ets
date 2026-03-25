let storageWidgetCard = new LocalStorage();

@Entry(storageWidgetCard)
@Component
struct WidgetCard {
  @LocalStorageProp('username') username: string = '';
  @LocalStorageProp('notes') notesJson: string = '[]';

  private getParsedNotes(): string[] {
    try {
      return JSON.parse(this.notesJson) as string[];
    } catch {
      return [];
    }
  }

  build() {
    Column({ space: 8 }) {
      Text('My Notes')
        .fontSize(16)
        .fontWeight(FontWeight.Bold)

      Text('User: ' + this.username)
        .fontSize(12)
        .fontColor('#6B7280')

      ForEach(this.getParsedNotes(), (note: string) => {
        Text('• ' + note)
          .fontSize(12)
          .width('100%')
          .maxLines(1)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
      }, (note: string) => note)

      Button('Clear All')
        .fontSize(11)
        .height(28)
        .onClick(() => {
          postCardAction(this, {
            action: 'message',
            params: { action: 'clearNotes' }
          });
        })
    }
    .width('100%')
    .height('100%')
    .padding(12)
    .backgroundColor('#F6F8FF')
    .justifyContent(FlexAlign.Start)
  }
}
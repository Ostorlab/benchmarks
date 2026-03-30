import { router } from '@kit.ArkUI';
import common from '@ohos.app.ability.common';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { nearbySyncStore } from '../common/NearbySyncStore';

const DOMAIN = 0x0000;
const TAG = 'NotesPage';

@Entry
@Component
struct Notes {
  @State notes: string[] = [];
  @State noteInput: string = '';
  @State showAddForm: boolean = false;
  @State username: string = '';
  @State localDeviceName: string = 'This device';
  @State pairedDeviceStatus: string = 'No paired device connected';
  @State syncAuditMessage: string = 'No cross-device note sync has been received yet.';
  @State lastSyncStatus: string = 'Checking Distributed Soft Bus availability.';
  @State transportProtection: string = 'Distributed KV sync in compatibility transport mode';

  async aboutToAppear(): Promise<void> {
    const params = router.getParams() as Record<string, Object>;
    const usernameParam = params?.username;
    if (typeof usernameParam === 'string') {
      this.username = usernameParam;
    }

    const context = getContext(this) as common.UIAbilityContext;
    nearbySyncStore.configure(context.abilityInfo.bundleName, context);
    hilog.info(DOMAIN, TAG, 'Notes page appeared for user %{public}s', this.username);
    await this.reloadNotebook();
  }

  private async reloadNotebook(): Promise<void> {
    hilog.info(DOMAIN, TAG, 'Reloading notebook and paired-device status');
    this.localDeviceName = nearbySyncStore.getLocalDeviceName();

    const devices = await nearbySyncStore.getAvailableDevices();
    this.pairedDeviceStatus = devices.length > 0
      ? devices.map((device) => device.deviceName).join(', ')
      : 'No paired device connected';

    this.notes = await nearbySyncStore.loadNotes();
    this.syncAuditMessage = this.notes.length > 0
      ? 'Loaded ' + this.notes.length.toString() + ' synced note(s) from the shared notebook.'
      : 'No cross-device note sync has been received yet.';
    this.lastSyncStatus = devices.length > 0
      ? 'Nearby Distributed Soft Bus session is available.'
      : 'No nearby Distributed Soft Bus session is active.';
    hilog.info(
      DOMAIN,
      TAG,
      'Reload complete. Notes=%{public}d PairedDevices=%{public}d',
      this.notes.length,
      devices.length
    );
  }

  private saveNote(): void {
    const trimmedNote = this.noteInput.trim();
    if (trimmedNote.length === 0) {
      hilog.warn(DOMAIN, TAG, 'Ignoring empty note submission');
      return;
    }

    hilog.info(DOMAIN, TAG, 'Save button clicked. NoteLength=%{public}d', trimmedNote.length);
    this.lastSyncStatus = 'Saving note and pushing it to paired devices.';
    this.noteInput = '';
    this.showAddForm = false;

    nearbySyncStore.saveNoteAndSync(trimmedNote, this.username)
      .then(async (status: string) => {
        hilog.info(DOMAIN, TAG, 'Save-and-sync completed: %{public}s', status);
        this.lastSyncStatus = status;
        await this.reloadNotebook();
      })
      .catch((error: Error) => {
        hilog.error(DOMAIN, TAG, 'Save-and-sync failed: %{public}s', error.message);
        this.lastSyncStatus = 'Unable to sync note: ' + error.message;
      });
  }

  private refreshFromPairedDevices(): void {
    hilog.info(DOMAIN, TAG, 'Check For Paired Device Updates button clicked');
    this.lastSyncStatus = 'Requesting note updates from paired devices.';

    nearbySyncStore.refreshFromPairedDevices()
      .then(async (status: string) => {
        hilog.info(DOMAIN, TAG, 'Pull update completed: %{public}s', status);
        this.lastSyncStatus = status;
        await this.reloadNotebook();
      })
      .catch((error: Error) => {
        hilog.error(DOMAIN, TAG, 'Pull update failed: %{public}s', error.message);
        this.lastSyncStatus = 'Unable to refresh note sync: ' + error.message;
      });
  }

  build() {
    Scroll() {
      Column({ space: 16 }) {
        Text('My Notes')
          .fontSize(28)
          .fontWeight(FontWeight.Bold)

        if (this.username.length > 0) {
          Text('Welcome, ' + this.username)
            .fontSize(16)
            .fontColor('#4B5563')
        }

        Column({ space: 6 }) {
          Text('Nearby sync status')
            .fontSize(16)
            .fontWeight(FontWeight.Medium)

          Text('Local device: ' + this.localDeviceName)
            .fontSize(13)
            .fontColor('#9A3412')

          Text(this.lastSyncStatus)
            .fontSize(14)
            .fontColor('#7C2D12')

          Text('Paired devices: ' + this.pairedDeviceStatus)
            .fontSize(13)
            .fontColor('#9A3412')

          Text('Transport protection: ' + this.transportProtection)
            .fontSize(13)
            .fontColor('#9A3412')

          Text(this.syncAuditMessage)
            .fontSize(13)
            .fontColor('#9A3412')
        }
        .width('100%')
        .padding(14)
        .backgroundColor('#FFF7ED')
        .borderRadius(12)

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

        Column({ space: 10 }) {
          Button('Sync Latest Note To Paired Device')
            .width('100%')
            .height(48)
            .onClick(() => {
              hilog.info(DOMAIN, TAG, 'Sync Latest Note To Paired Device button clicked');
              const latestNote = this.notes.length > 0 ? this.notes[this.notes.length - 1] : '';
              if (latestNote.length === 0) {
                hilog.warn(DOMAIN, TAG, 'Manual sync requested but there is no local note to sync');
                this.lastSyncStatus = 'Create a note before starting paired-device sync.';
                return;
              }

              nearbySyncStore.syncCurrentNotebook(this.username)
                .then(async (status: string) => {
                  hilog.info(DOMAIN, TAG, 'Manual sync completed: %{public}s', status);
                  this.lastSyncStatus = status;
                  await this.reloadNotebook();
                })
                .catch((error: Error) => {
                  hilog.error(DOMAIN, TAG, 'Manual sync failed: %{public}s', error.message);
                  this.lastSyncStatus = 'Unable to sync note: ' + error.message;
                });
            })

          Button('Check For Paired Device Updates')
            .width('100%')
            .height(48)
            .backgroundColor('#0F766E')
            .fontColor('#FFFFFF')
            .onClick(() => {
              this.refreshFromPairedDevices();
            })
        }
        .width('100%')
      }
      .width('100%')
      .padding(20)
      .backgroundColor('#F6F8FF')
      .justifyContent(FlexAlign.Start)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F6F8FF')
  }
}

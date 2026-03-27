import { router } from '@kit.ArkUI';
import abilityAccessCtrl, { PermissionRequestResult, Permissions } from '@ohos.abilityAccessCtrl';
import common from '@ohos.app.ability.common';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { DiscoverySnapshot, nearbySyncDiscoveryBeacon } from '../sync/NearbySyncDiscoveryBeacon';

const DOMAIN = 0x0000;

class LocalExposureView {
  localDeviceName: string;
  localDeviceId: string;
  localDeviceType: number;

  constructor(localDeviceName: string, localDeviceId: string, localDeviceType: number) {
    this.localDeviceName = localDeviceName;
    this.localDeviceId = localDeviceId;
    this.localDeviceType = localDeviceType;
  }
}

@Entry
@Component
struct Notes {
  @State notes: string[] = [];
  @State noteInput: string = '';
  @State showAddForm: boolean = false;
  @State username: string = '';
  @State syncStatus: string = 'Nearby device discovery inactive';
  @State localExposure: string = '';
  @State discoveredDevices: string = 'No nearby devices discovered yet.';

  aboutToAppear() {
    const params = router.getParams() as Record<string, Object>;
    const usernameParam = params?.username;
    if (typeof usernameParam === 'string') {
      this.username = usernameParam;
    }

    this.initializeNearbySyncDiscovery();
  }

  aboutToDisappear() {
    nearbySyncDiscoveryBeacon.stopDiscovery();
  }

  private saveNote(): void {
    const trimmedNote = this.noteInput.trim();
    if (trimmedNote.length === 0) {
      return;
    }

    this.notes = [...this.notes, trimmedNote];
    this.noteInput = '';
    this.showAddForm = false;
  }

  private initializeNearbySyncDiscovery(): void {
    const context = getContext(this) as common.UIAbilityContext;
    hilog.info(DOMAIN, 'NotesPage', 'Initializing nearby sync discovery');
    const atManager = abilityAccessCtrl.createAtManager();
    hilog.info(DOMAIN, 'NotesPage', 'AtManager created successfully');
    const permissions: Permissions[] = ['ohos.permission.DISTRIBUTED_DATASYNC' as Permissions];
    hilog.info(DOMAIN, 'NotesPage', 'Requesting permissions: %{public}s', JSON.stringify(permissions));

    atManager.requestPermissionsFromUser(context, permissions)
      .then((result: PermissionRequestResult) => {
        hilog.info(DOMAIN, 'NotesPage', 'Permission request resolved: %{public}s', JSON.stringify(result.authResults));
        if (!result.authResults.every((authResult) => authResult === 0)) {
          hilog.error(DOMAIN, 'NotesPage', 'Permission denied for nearby discovery');
          this.syncStatus = 'Nearby discovery permission denied.';
          return;
        }

        hilog.info(DOMAIN, 'NotesPage', 'Permission granted, configuring discovery beacon');
        nearbySyncDiscoveryBeacon.configure(context.abilityInfo.bundleName);
        this.syncStatus = nearbySyncDiscoveryBeacon.startDiscovery((snapshot: DiscoverySnapshot) => {
          hilog.info(DOMAIN, 'NotesPage', 'Discovery snapshot received with %{public}d discovered devices', snapshot.discoveredDevices.length);
          const localExposureView = new LocalExposureView(
            snapshot.localDeviceName,
            snapshot.localDeviceId,
            snapshot.localDeviceType
          );
          this.localExposure = JSON.stringify(localExposureView);

          if (snapshot.discoveredDevices.length === 0) {
            this.discoveredDevices = 'No nearby devices discovered yet.';
            return;
          }

          this.discoveredDevices = snapshot.discoveredDevices.map((device) => {
            return device.deviceName + ' [' + device.deviceId + ']';
          }).join('\n');
        });
      })
      .catch((err: Error) => {
        hilog.error(DOMAIN, 'NotesPage', 'Permission/discovery setup failed: %{public}s', JSON.stringify(err));
        this.syncStatus = 'Nearby discovery unavailable during permission/discovery setup: ' + JSON.stringify(err);
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

      Column({ space: 6 }) {
        Text('Nearby Sync Discovery')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)

        Text(this.syncStatus)
          .fontSize(14)
          .fontColor('#9A3412')

        if (this.localExposure.length > 0) {
          Text(this.localExposure)
            .width('100%')
            .fontSize(12)
            .fontColor('#7C2D12')
        }

        Text(this.discoveredDevices)
          .width('100%')
          .fontSize(12)
          .fontColor('#7C2D12')
      }
      .width('100%')
      .padding(12)
      .backgroundColor('#FFEDD5')
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
    }
    .width('100%')
    .height('100%')
    .padding(20)
    .backgroundColor('#F6F8FF')
    .justifyContent(FlexAlign.Start)
  }
}

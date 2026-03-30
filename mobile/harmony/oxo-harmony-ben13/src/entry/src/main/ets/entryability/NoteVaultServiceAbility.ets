import { AppServiceExtensionAbility, Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import rpc from '@ohos.rpc';
import NoteVaultServiceImpl from '../service/NoteVaultServiceImpl';

const DOMAIN = 0x0000;
const TAG = 'NoteVaultServiceAbility';

export default class NoteVaultServiceAbility extends AppServiceExtensionAbility {
  onCreate(want: Want): void {
    hilog.info(DOMAIN, TAG, 'NoteVaultServiceAbility created with want %{public}s', JSON.stringify(want));
  }

  onConnect(want: Want): rpc.RemoteObject {
    hilog.info(
      DOMAIN,
      TAG,
      'NoteVaultServiceAbility connected with want %{public}s',
      JSON.stringify(want)
    );

    return new NoteVaultServiceImpl();
  }

  onDisconnect(want: Want): void {
    hilog.info(DOMAIN, TAG, 'NoteVaultServiceAbility disconnected with want %{public}s', JSON.stringify(want));
  }
}

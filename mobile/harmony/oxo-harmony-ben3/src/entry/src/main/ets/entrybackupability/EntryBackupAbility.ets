import { hilog } from '@kit.PerformanceAnalysisKit';
import { BackupExtensionAbility, BundleVersion } from '@kit.CoreFileKit';
import { Want } from '@kit.AbilityKit';

const DOMAIN = 0x0000;

export default class EntryBackupAbility extends BackupExtensionAbility {
  private sensitiveData: string = 'user_credentials_and_app_state';

  async onBackup() {
    hilog.info(DOMAIN, 'testTag', 'onBackup ok');
    await Promise.resolve();
  }

  async onRestore(bundleVersion: BundleVersion) {
    hilog.info(DOMAIN, 'testTag', 'onRestore ok %{public}s', JSON.stringify(bundleVersion));
    await Promise.resolve();
  }
}
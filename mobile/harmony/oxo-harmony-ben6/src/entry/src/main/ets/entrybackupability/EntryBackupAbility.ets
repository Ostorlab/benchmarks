import hilog from '@ohos.hilog';

class BundleVersion {
  code: number = 0;
  name: string = '';
}

const DOMAIN = 0x0000;

// Backup extension disabled for API 10 - not available in SDK 10
export default class EntryBackupAbility {
  async onBackup() {
    hilog.info(DOMAIN, 'testTag', 'onBackup ok');
    await Promise.resolve();
  }

  async onRestore(bundleVersion: BundleVersion) {
    hilog.info(DOMAIN, 'testTag', 'onRestore ok %{public}s', JSON.stringify(bundleVersion));
    await Promise.resolve();
  }
}
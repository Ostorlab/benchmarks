import distributedKVStore from '@ohos.data.distributedKVStore';
import common from '@ohos.app.ability.common';

const STORE_ID = 'notes_sync_store';
const NOTES_KEY = 'private_notes';

class NotesSyncStore {
  private kvManager?: distributedKVStore.KVManager;
  private kvStore?: distributedKVStore.SingleKVStore;
  private bundleName: string = '';
  private context?: common.UIAbilityContext;

  configure(bundleName: string, context: common.UIAbilityContext): void {
    if (this.bundleName === bundleName && this.context === context) {
      return;
    }

    this.bundleName = bundleName;
    this.context = context;
    this.kvManager = undefined;
    this.kvStore = undefined;
  }

  private async openStore(): Promise<distributedKVStore.SingleKVStore> {
    if (this.kvStore) {
      return this.kvStore;
    }

    if (this.bundleName.length === 0 || !this.context) {
      throw new Error('Notes sync store is not configured');
    }

    const manager = distributedKVStore.createKVManager({
      bundleName: this.bundleName,
      context: this.context
    });

    this.kvManager = manager;
    this.kvStore = await manager.getKVStore(STORE_ID, {
      createIfMissing: true,
      encrypt: false,
      backup: false,
      autoSync: true,
      kvStoreType: distributedKVStore.KVStoreType.SINGLE_VERSION,
      securityLevel: distributedKVStore.SecurityLevel.S2
    });

    return this.kvStore;
  }

  async loadNotes(): Promise<string[]> {
    const store = await this.openStore();

    try {
      const rawValue = await store.get(NOTES_KEY);
      if (typeof rawValue !== 'string' || rawValue.length === 0) {
        return [];
      }

      return JSON.parse(rawValue) as string[];
    } catch (_) {
      return [];
    }
  }

  async saveNotes(notes: string[]): Promise<void> {
    const store = await this.openStore();
    await store.put(NOTES_KEY, JSON.stringify(notes));
  }
}

export const notesSyncStore = new NotesSyncStore();

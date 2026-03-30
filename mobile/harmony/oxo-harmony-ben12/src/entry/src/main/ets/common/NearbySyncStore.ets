import common from '@ohos.app.ability.common';
import distributedKVStore from '@ohos.data.distributedKVStore';
import distributedDeviceManager from '@ohos.distributedDeviceManager';
import { hilog } from '@kit.PerformanceAnalysisKit';

const STORE_ID = 'paired_notes_bus';
const NOTEBOOK_KEY = 'distributed_notebook';
const DOMAIN = 0x0000;
const TAG = 'NearbySyncStore';

class NoteNotebookPayload {
  username: string = '';
  notesJson: string = '[]';
  updatedAt: string = '';
}

class NoteTransportEnvelope {
  senderDeviceId: string = '';
  senderDeviceName: string = '';
  transport: string = 'DistributedSoftBus';
  protectionMode: string = 'compatibility-xor';
  payload: string = '';
  updatedAt: string = '';
}

export class NearbySyncStore {
  private kvManager?: distributedKVStore.KVManager;
  private kvStore?: distributedKVStore.SingleKVStore;
  private deviceManager?: distributedDeviceManager.DeviceManager;
  private bundleName: string = '';
  private context?: common.UIAbilityContext;

  configure(bundleName: string, context: common.UIAbilityContext): void {
    if (this.bundleName === bundleName && this.context === context) {
      return;
    }

    hilog.info(DOMAIN, TAG, 'Configuring nearby sync store for bundle %{public}s', bundleName);
    this.bundleName = bundleName;
    this.context = context;
    this.kvManager = undefined;
    this.kvStore = undefined;
    this.deviceManager = undefined;
  }

  private getTransportShift(secret: string): number {
    let checksum = 0;
    for (let index = 0; index < secret.length; index++) {
      checksum = (checksum + secret.charCodeAt(index)) % 17;
    }
    return checksum + 3;
  }

  private obfuscateTransportPayload(plainText: string, secret: string): string {
    const shift = this.getTransportShift(secret);
    let encoded = '';

    for (let index = 0; index < plainText.length; index++) {
      encoded += String.fromCharCode(plainText.charCodeAt(index) + shift);
    }

    return encoded;
  }

  private restoreTransportPayload(encodedText: string, secret: string): string {
    const shift = this.getTransportShift(secret);
    let decoded = '';

    for (let index = 0; index < encodedText.length; index++) {
      decoded += String.fromCharCode(encodedText.charCodeAt(index) - shift);
    }

    return decoded;
  }

  private parseNotesJson(rawNotes: string): string[] {
    try {
      const parsed = JSON.parse(rawNotes) as Object;
      if (Array.isArray(parsed) && parsed.every((item: Object) => typeof item === 'string')) {
        return parsed as string[];
      }
    } catch (_) {
      return [];
    }

    return [];
  }

  private parseNotebookEnvelope(rawValue: string): string[] {
    try {
      const envelope = JSON.parse(rawValue) as NoteTransportEnvelope;
      const decodedPayload = this.restoreTransportPayload(envelope.payload, envelope.senderDeviceId);
      const payload = JSON.parse(decodedPayload) as NoteNotebookPayload;
      return this.parseNotesJson(payload.notesJson);
    } catch (_) {
      return [];
    }
  }

  private async openStore(): Promise<distributedKVStore.SingleKVStore> {
    if (this.kvStore) {
      hilog.info(DOMAIN, TAG, 'Reusing existing distributed KV store %{public}s', STORE_ID);
      return this.kvStore;
    }

    if (this.bundleName.length === 0 || !this.context) {
      hilog.error(DOMAIN, TAG, 'Cannot open distributed KV store because configuration is missing');
      throw new Error('Nearby sync store is not configured');
    }

    const manager = distributedKVStore.createKVManager({
      bundleName: this.bundleName,
      context: this.context
    });

    hilog.info(DOMAIN, TAG, 'Opening distributed KV store %{public}s', STORE_ID);
    this.kvManager = manager;
    this.kvStore = await manager.getKVStore(STORE_ID, {
      createIfMissing: true,
      encrypt: false,
      backup: false,
      autoSync: false,
      kvStoreType: distributedKVStore.KVStoreType.SINGLE_VERSION,
      securityLevel: distributedKVStore.SecurityLevel.S2
    });

    hilog.info(DOMAIN, TAG, 'Distributed KV store %{public}s opened successfully', STORE_ID);
    return this.kvStore;
  }

  private openDeviceManager(): distributedDeviceManager.DeviceManager {
    if (this.deviceManager) {
      hilog.info(DOMAIN, TAG, 'Reusing existing distributed device manager');
      return this.deviceManager;
    }

    if (this.bundleName.length === 0) {
      hilog.error(DOMAIN, TAG, 'Cannot create distributed device manager because bundle name is missing');
      throw new Error('Device manager is not configured');
    }

    hilog.info(DOMAIN, TAG, 'Creating distributed device manager for bundle %{public}s', this.bundleName);
    this.deviceManager = distributedDeviceManager.createDeviceManager(this.bundleName);
    return this.deviceManager;
  }

  private async runSync(deviceIds: string[], mode: distributedKVStore.SyncMode): Promise<void> {
    const store = await this.openStore();
    if (deviceIds.length === 0) {
      hilog.warn(DOMAIN, TAG, 'Skipping distributed sync because no paired device IDs were provided');
      return;
    }

    hilog.info(
      DOMAIN,
      TAG,
      'Starting distributed sync. Mode=%{public}d DeviceCount=%{public}d',
      mode,
      deviceIds.length
    );
    await new Promise<void>((resolve, reject) => {
      const syncCallback = (_result: Array<[string, number]>): void => {
        hilog.info(DOMAIN, TAG, 'Distributed sync completed for %{public}d device(s)', deviceIds.length);
        store.off('syncComplete', syncCallback);
        resolve();
      };

      try {
        store.on('syncComplete', syncCallback);
        store.sync(deviceIds, mode);
      } catch (error) {
        hilog.error(DOMAIN, TAG, 'Distributed sync failed: %{public}s', JSON.stringify(error));
        store.off('syncComplete', syncCallback);
        reject(error);
      }
    });
  }

  getLocalDeviceName(): string {
    try {
      return this.openDeviceManager().getLocalDeviceName();
    } catch (_) {
      return 'This device';
    }
  }

  getLocalDeviceId(): string {
    try {
      return this.openDeviceManager().getLocalDeviceId();
    } catch (_) {
      return this.bundleName;
    }
  }

  async getAvailableDevices(): Promise<Array<distributedDeviceManager.DeviceBasicInfo>> {
    try {
      const devices = this.openDeviceManager().getAvailableDeviceListSync();
      hilog.info(DOMAIN, TAG, 'Device manager returned %{public}d available paired device(s)', devices.length);
      return devices;
    } catch (error) {
      hilog.error(DOMAIN, TAG, 'Unable to query available paired devices: %{public}s', JSON.stringify(error));
      return [];
    }
  }

  async loadNotes(): Promise<string[]> {
    const store = await this.openStore();

    try {
      const rawValue = await store.get(NOTEBOOK_KEY);
      if (typeof rawValue !== 'string' || rawValue.length === 0) {
        hilog.info(DOMAIN, TAG, 'No synchronized notebook payload found in KV store');
        return [];
      }

      const notes = this.parseNotebookEnvelope(rawValue);
      hilog.info(DOMAIN, TAG, 'Loaded %{public}d note(s) from distributed notebook payload', notes.length);
      return notes;
    } catch (error) {
      hilog.error(DOMAIN, TAG, 'Failed to load notes from distributed KV store: %{public}s', JSON.stringify(error));
      return [];
    }
  }

  private async syncNotebook(notes: string[], username: string): Promise<string> {
    hilog.info(
      DOMAIN,
      TAG,
      'Preparing notebook sync for %{public}d note(s) as user %{public}s',
      notes.length,
      username
    );
    const payload = new NoteNotebookPayload();
    payload.username = username;
    payload.notesJson = JSON.stringify(notes);
    payload.updatedAt = new Date().toISOString();

    const envelope = new NoteTransportEnvelope();
    envelope.senderDeviceId = this.getLocalDeviceId();
    envelope.senderDeviceName = this.getLocalDeviceName();
    envelope.payload = this.obfuscateTransportPayload(JSON.stringify(payload), envelope.senderDeviceId);
    envelope.updatedAt = payload.updatedAt;

    const store = await this.openStore();
    await store.put(NOTEBOOK_KEY, JSON.stringify(envelope));
    hilog.info(
      DOMAIN,
      TAG,
      'Wrote distributed notebook snapshot. Sender=%{public}s Protection=%{public}s',
      envelope.senderDeviceName,
      envelope.protectionMode
    );

    const devices = await this.getAvailableDevices();
    const reachableDeviceIds = devices
      .map((device: distributedDeviceManager.DeviceBasicInfo) => device.networkId ?? '')
      .filter((deviceId: string) => deviceId.length > 0);

    if (reachableDeviceIds.length === 0) {
      hilog.warn(DOMAIN, TAG, 'No paired devices available, notebook snapshot remains local only');
      return 'Note saved locally. Connect a paired device to sync it over Distributed Soft Bus.';
    }

    await this.runSync(reachableDeviceIds, distributedKVStore.SyncMode.PUSH_PULL);
    hilog.info(DOMAIN, TAG, 'Notebook sync pushed to %{public}d paired device(s)', reachableDeviceIds.length);
    return 'Note saved and synced with ' + devices.map((device) => device.deviceName).join(', ') + '.';
  }

  async saveNoteAndSync(note: string, username: string): Promise<string> {
    const currentNotes = await this.loadNotes();
    const updatedNotes = [...currentNotes, note];
    hilog.info(DOMAIN, TAG, 'Saving a new note before distributed sync. PreviousCount=%{public}d', currentNotes.length);
    return this.syncNotebook(updatedNotes, username);
  }

  async syncCurrentNotebook(username: string): Promise<string> {
    const currentNotes = await this.loadNotes();
    if (currentNotes.length === 0) {
      hilog.warn(DOMAIN, TAG, 'Manual sync requested but the notebook is empty');
      return 'Create a note before starting paired-device sync.';
    }

    hilog.info(DOMAIN, TAG, 'Manual sync requested for existing notebook with %{public}d note(s)', currentNotes.length);
    return this.syncNotebook(currentNotes, username);
  }

  async refreshFromPairedDevices(): Promise<string> {
    const devices = await this.getAvailableDevices();
    const reachableDeviceIds = devices
      .map((device: distributedDeviceManager.DeviceBasicInfo) => device.networkId ?? '')
      .filter((deviceId: string) => deviceId.length > 0);

    if (reachableDeviceIds.length === 0) {
      hilog.warn(DOMAIN, TAG, 'Pull request skipped because no paired devices are available');
      return 'No paired devices are currently available for note sync.';
    }

    hilog.info(DOMAIN, TAG, 'Pulling notebook updates from %{public}d paired device(s)', reachableDeviceIds.length);
    await this.runSync(reachableDeviceIds, distributedKVStore.SyncMode.PULL_ONLY);
    hilog.info(DOMAIN, TAG, 'Completed pull request from paired devices');
    return 'Fetched the latest note updates from ' + devices.map((device) => device.deviceName).join(', ') + '.';
  }
}

export const nearbySyncStore = new NearbySyncStore();

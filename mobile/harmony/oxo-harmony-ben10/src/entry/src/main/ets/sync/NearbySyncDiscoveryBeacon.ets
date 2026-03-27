import distributedDeviceManager from '@ohos.distributedDeviceManager';
import { hilog } from '@kit.PerformanceAnalysisKit';

const DOMAIN = 0x0000;

export class DiscoverySnapshot {
  localDeviceName: string;
  localDeviceId: string;
  localDeviceType: number;
  discovering: boolean;
  discoveredDevices: distributedDeviceManager.DeviceBasicInfo[];

  constructor(
    localDeviceName: string,
    localDeviceId: string,
    localDeviceType: number,
    discovering: boolean,
    discoveredDevices: distributedDeviceManager.DeviceBasicInfo[]
  ) {
    this.localDeviceName = localDeviceName;
    this.localDeviceId = localDeviceId;
    this.localDeviceType = localDeviceType;
    this.discovering = discovering;
    this.discoveredDevices = discoveredDevices;
  }
}

class DiscoverSuccessData {
  device: distributedDeviceManager.DeviceBasicInfo;

  constructor(device: distributedDeviceManager.DeviceBasicInfo) {
    this.device = device;
  }
}

type DiscoveryUpdateCallback = (snapshot: DiscoverySnapshot) => void;
type DiscoverSuccessCallback = (data: DiscoverSuccessData) => void;

class NearbySyncDiscoveryBeacon {
  private deviceManager: distributedDeviceManager.DeviceManager | null = null;
  private discoverSuccessCallback: DiscoverSuccessCallback | null = null;
  private snapshot: DiscoverySnapshot;

  constructor() {
    this.snapshot = this.createSnapshot('', '', 0, false, []);
  }

  configure(bundleName: string): void {
    if (this.deviceManager !== null) {
      hilog.info(DOMAIN, 'NearbySyncDiscovery', 'DeviceManager already configured for bundle %{public}s', bundleName);
      return;
    }

    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Creating DeviceManager for bundle %{public}s', bundleName);
    this.deviceManager = distributedDeviceManager.createDeviceManager(bundleName);
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'DeviceManager created successfully');
  }

  startDiscovery(onUpdate: DiscoveryUpdateCallback): string {
    if (this.deviceManager === null) {
      hilog.error(DOMAIN, 'NearbySyncDiscovery', 'startDiscovery called before DeviceManager configuration');
      throw new Error('DeviceManager is not configured.');
    }

    // VULNERABLE: Nearby discovery exposes the user-facing local device name.
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Reading local device name');
    this.snapshot.localDeviceName = this.deviceManager.getLocalDeviceName();
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Local device name read: %{public}s', this.snapshot.localDeviceName);
    // VULNERABLE: Nearby discovery exposes a stable app-scoped device identifier.
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Reading local device id');
    this.snapshot.localDeviceId = this.deviceManager.getLocalDeviceId();
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Local device id read: %{public}s', this.snapshot.localDeviceId);
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Reading local device type');
    this.snapshot.localDeviceType = this.deviceManager.getLocalDeviceType();
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Local device type read: %{public}d', this.snapshot.localDeviceType);

    if (this.discoverSuccessCallback === null) {
      hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Registering discoverSuccess callback');
      this.discoverSuccessCallback = (data: DiscoverSuccessData): void => {
        const device = data.device;
        hilog.info(
          DOMAIN,
          'NearbySyncDiscovery',
          'discoverSuccess received for device %{public}s [%{public}s]',
          device.deviceName,
          device.deviceId
        );
        const alreadyTracked = this.snapshot.discoveredDevices.some((trackedDevice: distributedDeviceManager.DeviceBasicInfo) => {
          return trackedDevice.deviceId === device.deviceId;
        });

        if (!alreadyTracked) {
          this.snapshot.discoveredDevices = [...this.snapshot.discoveredDevices, device];
        }

        onUpdate(this.getSnapshot());
      };

      this.deviceManager.on('discoverSuccess', this.discoverSuccessCallback);
      hilog.info(DOMAIN, 'NearbySyncDiscovery', 'discoverSuccess callback registered');
    }

    // VULNERABLE: Discovery starts automatically when the Notes page opens.
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Calling startDiscovering');
    this.deviceManager.startDiscovering(
      {
        discoverTargetType: 1 as Object
      },
      {
        authenticationStatus: 0 as Object,
        availableStatus: 0 as Object,
        discoverDistance: 100 as Object,
        authorizationType: 0 as Object
      }
    );
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'startDiscovering returned successfully');

    this.snapshot.discovering = true;
    hilog.error(
      DOMAIN,
      'NearbySyncDiscovery',
      'VULNERABLE: Nearby sync discovery started with local device name %{public}s and stable id %{public}s',
      this.snapshot.localDeviceName,
      this.snapshot.localDeviceId
    );

    onUpdate(this.getSnapshot());
    return 'Nearby device discovery active';
  }

  stopDiscovery(): void {
    if (this.deviceManager === null || !this.snapshot.discovering) {
      hilog.info(DOMAIN, 'NearbySyncDiscovery', 'stopDiscovery skipped because discovery is not active');
      return;
    }

    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Stopping discovery');
    this.deviceManager.stopDiscovering();
    if (this.discoverSuccessCallback !== null) {
      hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Unregistering discoverSuccess callback');
      this.deviceManager.off('discoverSuccess', this.discoverSuccessCallback);
    }

    this.snapshot.discovering = false;
    this.discoverSuccessCallback = null;
    hilog.info(DOMAIN, 'NearbySyncDiscovery', 'Discovery stopped');
  }

  getSnapshot(): DiscoverySnapshot {
    return this.createSnapshot(
      this.snapshot.localDeviceName,
      this.snapshot.localDeviceId,
      this.snapshot.localDeviceType,
      this.snapshot.discovering,
      [...this.snapshot.discoveredDevices]
    );
  }

  private createSnapshot(
    localDeviceName: string,
    localDeviceId: string,
    localDeviceType: number,
    discovering: boolean,
    discoveredDevices: distributedDeviceManager.DeviceBasicInfo[]
  ): DiscoverySnapshot {
    return new DiscoverySnapshot(
      localDeviceName,
      localDeviceId,
      localDeviceType,
      discovering,
      discoveredDevices
    );
  }
}

export const nearbySyncDiscoveryBeacon = new NearbySyncDiscoveryBeacon();

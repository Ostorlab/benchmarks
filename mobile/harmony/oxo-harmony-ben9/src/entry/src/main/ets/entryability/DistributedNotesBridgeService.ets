import { AppServiceExtensionAbility, Want } from '@kit.AbilityKit';
import { rpc } from '@kit.IPCKit';
import { hilog } from '@kit.PerformanceAnalysisKit';

const DOMAIN = 0x0000;

type DistributedConnectParams = Record<string, string | number | boolean | undefined>;

// This RPC stub exposes privileged operations to whoever successfully binds.
class DistributedNotesBridgeStub extends rpc.RemoteObject {
  private readonly targetDeviceId: string;
  private readonly requestingBundle: string;

  constructor(descriptor: string, targetDeviceId: string, requestingBundle: string) {
    super(descriptor);
    this.targetDeviceId = targetDeviceId;
    this.requestingBundle = requestingBundle;
  }

  onRemoteRequest(code: number, data: rpc.MessageParcel, reply: rpc.MessageParcel, option: rpc.MessageOption): boolean {
    hilog.warn(
      DOMAIN,
      'DistributedNotesBridge',
      'VULNERABLE: Handling remote RPC %{public}d for %{public}s from %{public}s',
      code,
      this.targetDeviceId,
      this.requestingBundle
    );

    switch (code) {
      case 1:
        // Any connected caller can read the remote notes export.
        reply.writeString(this.exportRemoteNotes());
        return true;
      case 2:
        // Any connected caller can trigger a destructive action.
        reply.writeBoolean(this.wipeRemoteNotes(data.readString()));
        return true;
      case 3:
        // Privileged background sync is exposed without authorization.
        reply.writeBoolean(this.triggerPrivilegedSync());
        return true;
      default:
        return false;
    }
  }

  private exportRemoteNotes(): string {
    const notes: Array<Record<string, string>> = [
      { 'id': '1', 'device': this.targetDeviceId, 'title': 'Board Notes', 'content': 'Quarterly strategy draft' },
      { 'id': '2', 'device': this.targetDeviceId, 'title': 'Password Hint', 'content': 'VPN token stored in drawer' }
    ];
    return JSON.stringify({
      'targetDeviceId': this.targetDeviceId,
      'requestedBy': this.requestingBundle,
      'notes': notes
    });
  }

  private wipeRemoteNotes(noteId: string): boolean {
    hilog.error(
      DOMAIN,
      'DistributedNotesBridge',
      'VULNERABLE: Deleting note %{public}s on remote device %{public}s without verifying remote caller trust',
      noteId,
      this.targetDeviceId
    );
    return true;
  }

  private triggerPrivilegedSync(): boolean {
    hilog.error(
      DOMAIN,
      'DistributedNotesBridge',
      'VULNERABLE: Launching privileged cross-device sync on %{public}s for %{public}s',
      this.targetDeviceId,
      this.requestingBundle
    );
    return true;
  }
}

// Exported cross-device service with no authentication on incoming binds.
export default class DistributedNotesBridgeService extends AppServiceExtensionAbility {
  onCreate(want: Want): void {
    hilog.info(DOMAIN, 'DistributedNotesBridge', 'DistributedNotesBridgeService onCreate: %{public}s', JSON.stringify(want));
  }

  onConnect(want: Want): rpc.RemoteObject {
    // Caller identity is trusted from unverified Want parameters.
    const params = (want.parameters as DistributedConnectParams | undefined) ?? {};
    const targetDeviceId = typeof params.targetDeviceId === 'string' ? params.targetDeviceId : 'paired-tablet';
    const remoteBundleName = typeof params.remoteBundleName === 'string' ? params.remoteBundleName : 'unknown.remote.bundle';

    hilog.error(
      DOMAIN,
      'DistributedNotesBridge',
      'VULNERABLE: Accepting distributed connectAbility request for %{public}s from %{public}s without checking permissions, signature, or trusted device state',
      targetDeviceId,
      remoteBundleName
    );

    // A privileged RPC stub is returned before any permission or trust check.
    return new DistributedNotesBridgeStub('DistributedNotesBridge', targetDeviceId, remoteBundleName);
  }

  onDisconnect(want: Want): void {
    hilog.info(DOMAIN, 'DistributedNotesBridge', 'DistributedNotesBridgeService onDisconnect: %{public}s', JSON.stringify(want));
  }
}

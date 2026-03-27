# oxo-harmony-ben9 Remote Ability Invocation Abuse

## Challenge Details

### Description

This HarmonyOS app sample demonstrates a distributed service abuse
vulnerability through the `connectAbility()` cross-device RPC mechanism.

In general, this weakness appears when an application exposes a service
extension ability that can be reached from a paired or trusted device and then
performs privileged operations over RPC without verifying the caller's real
identity, signature, permission set, or device trust context.

In this sample, the vulnerability is implemented by exporting a distributed
service extension and returning a privileged `rpc.RemoteObject` from
`onConnect()` without enforcing permissions or validating the remote caller.
The service also trusts attacker-controlled values from `Want.parameters` such
as `targetDeviceId` and `remoteBundleName`, reinforcing the distributed abuse
pattern.

### Vulnerability Type and Category
- **Type:** Remote Ability Invocation Abuse
- **Category:** Broken Access Control / Improper Component Exposure
- **CWE:** CWE-306 (Missing Authentication for Critical Function)
  > **Note:** CWE-306 is used here because the service exposes sensitive
  > cross-device operations through RPC without authenticating the remote
  > caller before executing privileged actions.

### Difficulty
Easy

### Severity
High

### Severity Rationale

This vulnerability is considered high severity because:

- The service extension is exported and reachable through HarmonyOS service
  binding flows
- The app accepts remote `connectAbility()` requests without verifying the real
  calling bundle, signature, or whether the requesting app should be trusted
- The RPC stub exposes privileged operations that simulate:
  - Exporting notes from a paired device
  - Deleting notes on a paired device
  - Triggering a privileged cross-device sync
- Attackers can exploit the weakness via:
    - Running a malicious app on another paired or trusted HarmonyOS device
    - Constructing a `Want` targeting `DistributedNotesBridgeService`
    - Supplying forged distributed context such as `targetDeviceId` or
      `remoteBundleName`
    - Binding through `connectAbility()` and invoking the returned RPC methods
      without authorization
- Users may interpret device pairing as a sufficient trust boundary even though
  app-level authorization is still required

### Root Cause

The absence of:
1. A restrictive permission on the distributed service declaration in
   `module.json5`
2. Programmatic verification of the real caller inside `onConnect()`
3. Authorization checks before handling privileged RPC codes in
   `onRemoteRequest()`
4. Validation that the remote device and remote bundle are genuinely trusted
   rather than attacker-supplied

### Example Vulnerable Declaration (`module.json5`)

```json5
{
  "extensionAbilities": [
    {
      "name": "DistributedNotesBridgeService",
      "type": "appService",
      "exported": true,
      "visible": true
      // Missing: "permissions": ["com.notes.app.permission.DISTRIBUTED_SYNC"]
    }
  ]
}
```

### Example Vulnerable Implementation (`DistributedNotesBridgeService.ets`)

```typescript
export default class DistributedNotesBridgeService extends AppServiceExtensionAbility {
  onConnect(want: Want): rpc.RemoteObject {
    const params = (want.parameters as Record<string, string>) ?? {};
    const targetDeviceId = params.targetDeviceId ?? 'paired-tablet';
    const remoteBundleName = params.remoteBundleName ?? 'unknown.remote.bundle';

    // VULNERABLE: Trusts distributed caller context from Want parameters.
    // VULNERABLE: No permission, signature, or trusted-device verification.
    return new DistributedNotesBridgeStub('DistributedNotesBridge', targetDeviceId, remoteBundleName);
  }
}
```

The returned remote object then allows privileged remote operations:

```typescript
switch (code) {
  case 1:
    reply.writeString(this.exportRemoteNotes());
    return true;
  case 2:
    reply.writeBoolean(this.wipeRemoteNotes(data.readString()));
    return true;
  case 3:
    reply.writeBoolean(this.triggerPrivilegedSync());
    return true;
}
```

### Mitigation

- Add a custom permission to the service declaration and restrict it to trusted
  apps
- Verify the actual caller identity and trust context inside `onConnect()`
- Reject cross-device requests unless the remote bundle, signature, account,
  and device relationship have been explicitly authorized
- Re-check authorization inside `onRemoteRequest()` before executing sensitive
  operations
- Set `exported: false` if external or distributed binding is not required

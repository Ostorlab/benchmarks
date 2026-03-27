# oxo-harmony-ben10 Device Discovery Eavesdropping

## Challenge Details

### Description

This HarmonyOS app sample demonstrates the victim side of a Device Discovery
Eavesdropping privacy vulnerability in a Notes application that supports
distributed note syncing.

In general, Device Discovery Eavesdropping occurs when a nearby attacker
passively listens to device discovery traffic and uses the observed metadata to
fingerprint users, correlate devices, or track presence over time. On the
victim side, the weakness appears when an application uses Harmony distributed
device discovery to find nearby sync targets but exposes more identity data
than is necessary for that purpose.

In this sample, the Notes page requests
`ohos.permission.DISTRIBUTED_DATASYNC`, creates a `DeviceManager` using
`distributedDeviceManager.createDeviceManager(...)`, reads the local device
name and stable application-scoped device identifier, registers a discovery
callback, and automatically attempts nearby discovery with
`startDiscovering()` when the page opens. By doing so, the app treats
identifying device metadata as normal sync information instead of minimizing
it for privacy. A nearby attacker that listens to Harmony device discovery
traffic can abuse this behavior to collect the victim device's identifying
metadata.

### Vulnerability Type and Category
- **Type:** Device Discovery Eavesdropping
- **Category:** Privacy Exposure / Information Exposure
- **CWE:** CWE-359 (Exposure of Private Personal Information to an Unauthorized Actor)
  > **Note:** CWE-359 is used here because the app exposes user-identifying
  > device metadata during discovery, allowing nearby unauthorized observers to
  > collect it for correlation and tracking.

### Difficulty
Easy

### Severity
Medium

### Severity Rationale

This vulnerability is considered medium severity because:

- Nearby listeners can passively observe identifying discovery metadata without
  directly compromising the Notes app
- The victim app exposes stable and user-linked values that can be correlated
  across sessions
- The nearby sync flow accesses and exposes more than low-risk pairing
  metadata, including:
  - Human-readable device names
  - Stable application-scoped device identifiers
  - Device type and nearby sync participation
- Attackers can exploit the weakness via:
    - Passive monitoring of nearby Harmony device discovery traffic
    - Repeated observation of the same device identifier over time
    - Correlation of device names to a specific user
    - Inferring sync activity from automatic page-load discovery behavior

### Root Cause

The absence of:
1. Metadata minimization for nearby sync discovery
2. Rotation or anonymization of device identifiers
3. Restriction of discovery to explicit user action instead of automatic page
   load
4. Restriction of discovery data to low-risk, non-identifying values only

### Example Vulnerable Implementation (`NearbySyncDiscoveryBeacon.ets`)

```typescript
const manager = distributedDeviceManager.createDeviceManager(bundleName);
const localDeviceName = manager.getLocalDeviceName();
const localDeviceId = manager.getLocalDeviceId();
manager.on('discoverSuccess', callback);

manager.startDiscovering(
  { discoverTargetType: 1 },
  {
    authenticationStatus: 0,
    availableStatus: 0,
    discoverDistance: 100,
    authorizationType: 0
  }
);
```

### Why It Is Vulnerable

- `getLocalDeviceName()` exposes a user-identifying device label to nearby observers
- `getLocalDeviceId()` exposes a stable identifier that can be correlated over time
- `on('discoverSuccess', ...)` integrates nearby device discovery directly into the Notes sync flow
- `startDiscovering()` is triggered automatically when the Notes page opens
- discovery is enabled for nearby sync without anonymization or identifier minimization
- the Notes app is the victim-side source of the metadata that an eavesdropping attacker can collect

### Example Notes Flow

On Notes page load, the app requests distributed sync permission and starts
real Harmony device discovery:

```typescript
aboutToAppear() {
  this.initializeNearbySyncDiscovery();
}
```

### Mitigation

- Discover nearby devices only after an explicit user action such as tapping a
  "Find devices" button
- Avoid using human-readable device names as sync identity where a lower-risk
  alias or ephemeral token would suffice
- Replace stable identifiers with rotating or scoped identifiers wherever
  possible
- Separate discovery from trust establishment and expose sensitive details only
  after pairing or authorization is completed

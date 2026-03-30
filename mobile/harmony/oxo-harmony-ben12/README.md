# oxo-harmony-ben12 Super Device Man-in-the-Middle

## Challenge Details

### Description

This HarmonyOS notes app sample demonstrates a Super Device
Man-in-the-Middle vulnerability during paired-device note synchronization.

- Tampering with note data in transit between paired devices on the
  Distributed Soft Bus when the app synchronizes note snapshots without
  authenticated encryption or payload integrity verification.

The app behaves like a normal notes client with a paired-device sync feature.
It uses Harmony distributed data APIs backed by the Distributed Soft Bus to
push and pull note updates between trusted nearby devices. When a note is
saved, the app updates the notebook snapshot in a distributed KV store and
synchronizes it with the currently available paired devices. The app also
allows the user to manually push the current notebook to paired devices and to
check for updates from those paired devices. When a device checks for updates,
it pulls the latest notebook snapshot from those paired devices.

The vulnerability is that note content is not protected with authenticated
encryption before it is synchronized. Instead, the notebook snapshot is wrapped
in a reversible compatibility transform and then trusted on receipt. Because
there is no integrity verification on the application payload, an attacker who
can tamper with the Distributed Soft Bus traffic can alter note content before
the receiver applies it.

### Vulnerability Type and Category
- **Type:** Super Device Man-in-the-Middle
- **Category:** Insecure Inter-Device Communication / Data Tampering in Transit
- **CWE:** CWE-300 (Channel Accessible by Non-Endpoint)

### Difficulty
Medium

### Severity
High

### Severity Rationale

This vulnerability is considered high severity because:

- Cross-device note synchronization occurs between trusted paired devices
- The receiver accepts synchronized notebook payloads without verifying an
  integrity tag or signature
- Attackers can exploit the weakness by:
  - Intercepting in-flight Distributed Soft Bus note synchronization
  - Rewriting the synchronized notebook snapshot
  - Delivering the modified payload so the victim device stores attacker-
    modified note content as legitimate

### Root Cause

The application assumes Harmony distributed synchronization over the
Distributed Soft Bus is sufficient on its own and does not perform app-layer
authenticity checks. It does not:

1. Use authenticated encryption for note-sync payloads
2. Validate a MAC or signature before loading synchronized notes
3. Bind the synchronized notebook payload to a trusted sender identity

### Example Vulnerable Implementation

The sender stores the notebook snapshot in a distributed KV store and protects
it only with a reversible compatibility transform:

```ts
const envelope = new NoteTransportEnvelope();
envelope.senderDeviceId = this.getLocalDeviceId();
envelope.senderDeviceName = this.getLocalDeviceName();
envelope.payload = this.obfuscateTransportPayload(JSON.stringify(payload), envelope.senderDeviceId);

await store.put(NOTEBOOK_KEY, JSON.stringify(envelope));
await this.runSync(reachableDeviceIds, distributedKVStore.SyncMode.PUSH_PULL);
```

The receiver then pulls the latest data and trusts the synchronized payload
without checking whether it was modified in transit:

```ts
await this.runSync(reachableDeviceIds, distributedKVStore.SyncMode.PULL_ONLY);
const rawValue = await store.get(NOTEBOOK_KEY);
return this.parseNotebookEnvelope(rawValue);
```

### Attack Flow

1. **Login:** The sender logs in on Device A and creates a note
2. **Sync:** Device A writes the notebook snapshot into the distributed KV
   store and synchronizes it to paired devices. The same notebook can also be
   pushed again later through the manual sync action.
3. **Interception:** An attacker in the communication path rewrites the
   encoded notebook payload while it is being synchronized over the Distributed
   Soft Bus
4. **Refresh:** The receiver on Device B checks for paired-device updates and
   pulls the modified notebook snapshot
5. **Impact:** Device B accepts and stores attacker-modified note content as a
   legitimate sync update

### Testing Notes

- Use two paired devices logged into the same distributed environment
- Emulators may work only if the image actually supports distributed device
  services and pairing
- On Device A, add a note and let the app push it to paired devices
- Optionally use the manual sync button on Device A to push the current
  notebook again without creating a new note
- On Device B, use the refresh action to pull the latest synchronized notes

### Mitigation

- Use authenticated encryption for note-sync payloads
- Verify note-packet integrity with a MAC or digital signature
- Bind synchronized notebook data to trusted paired-device identities
- Reject payloads whose protection mode does not meet the minimum security bar

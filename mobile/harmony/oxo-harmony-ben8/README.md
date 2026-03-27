# oxo-harmony-ben8 Distributed Data Service Leakage

## Challenge Details

### Description

This HarmonyOS app sample demonstrates a Distributed Data Service (DDS) leakage vulnerability.

A DDS leakage vulnerability occurs when an application stores sensitive data in HarmonyOS
distributed storage, such as a distributed KV store, even though that data should remain
local to the originating device. Because distributed storage is designed for synchronization,
this expands the trust boundary from one device to every device that can participate in the
same Harmony distributed ecosystem.

Typical examples include storing:
- authentication tokens
- private notes
- personal profile details
- other sensitive user content

without restricting synchronization, encrypting the payload, or keeping the data in
local-only secure storage.

In this benchmark, the application stores note content in a distributed KV store via
`@ohos.data.distributedKVStore` with `autoSync: true`, which models the unsafe pattern.

The result is that note data is no longer confined to the device where it was entered.

### Vulnerability Type and Category
- **Type:** Distributed Data Service Leakage
- **Category:** Sensitive Data Exposure / Insecure Data Storage
- **CWE:** CWE-922 (Insecure Storage of Sensitive Information), CWE-284 (Improper Access Control)

### Difficulty
Easy

### Severity
Medium

### Severity Rationale

This vulnerability is considered medium severity because:

- The notes app persists private user content inside a distributed KV store intended for
  cross-device synchronization.
- Any additional device attached to the same account becomes a new location where the data
  may be exposed.
- The data is stored without encryption and without limiting synchronization to non-sensitive
  fields only.

### Root Cause

The application:
1. Writes note content directly into a distributed KV store instead of local-only secure storage.
2. Enables automatic synchronization with `autoSync: true`.
3. Does not encrypt note values before storing them.
4. Does not distinguish between low-risk synchronized preferences and sensitive user content.

### Benchmark-Specific Example
```ts
import distributedKVStore from '@ohos.data.distributedKVStore';

const manager = distributedKVStore.createKVManager({
  bundleName: context.abilityInfo.bundleName,
  context
});

const kvStore = await manager.getKVStore('notes_sync_store', {
  createIfMissing: true,
  encrypt: false,
  backup: false,
  autoSync: true,
  kvStoreType: distributedKVStore.KVStoreType.SINGLE_VERSION,
  securityLevel: distributedKVStore.SecurityLevel.S2
});

await kvStore.put('private_notes', JSON.stringify(notes));
```

In the implemented sample:
- `NotesSyncStore` opens a distributed KV store named `notes_sync_store`
- the note list is serialized as JSON and stored under the key `private_notes`
- the notes page reloads those values when the user returns to the screen
- the app requests `ohos.permission.DISTRIBUTED_DATASYNC` in `module.json5`

### Exploitation Scenario

1. A user logs in to the notes app on Device A.
2. The app saves private notes into the Harmony distributed KV store.
3. HarmonyOS synchronizes the same note content to Device B on the same account.
4. Someone with access to Device B can read note content that the user only expected to remain
   on Device A.

### Mitigation

- Store sensitive notes in local-only secure storage instead of a distributed KV store.
- Synchronize only non-sensitive metadata if cross-device features are required.
- Encrypt any synchronized payload before writing it to the KV store.
- Document and enforce a clear policy for what data may enter Harmony distributed storage.

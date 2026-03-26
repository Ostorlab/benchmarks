# oxo-harmony-ben8 Distributed Data Service Leakage

## Challenge Details

### Description

This HarmonyOS app sample demonstrates a Distributed Data Service leakage vulnerability:

- Sensitive note content is stored in the distributed KV store via `@ohos.data.distributedData`
  with `autoSync: true`, causing note data to be replicated to other devices on the same account.
- The application treats private notes as ordinary sync data and does not apply data
  classification, encryption, or local-only storage boundaries.

This pattern leaks sensitive content beyond the originating device and expands the trust
boundary to every device participating in the user's Harmony distributed ecosystem.

### Vulnerability Type and Category
- **Type:** Distributed Data Service Leakage
- **Category:** Sensitive Data Exposure / Insecure Data Storage
- **CWE:** CWE-922 (Insecure Storage of Sensitive Information)

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
1. Writes note content directly into `distributedData` instead of local-only secure storage.
2. Enables automatic synchronization with `autoSync: true`.
3. Does not encrypt note values before storing them.
4. Does not distinguish between low-risk synchronized preferences and sensitive user content.

### Example Vulnerable Pattern
```ts
import distributedData from '@ohos.data.distributedData';

const manager = await distributedData.createKVManager({
  bundleName: 'co.ostorlab.insecure_harmony_app',
  userInfo: { userId: '0', userType: 0 }
});

const store = await manager.getKVStore('notes_sync_store', {
  createIfMissing: true,
  encrypt: false,
  backup: false,
  autoSync: true,
  kvStoreType: distributedData.KVStoreType.SINGLE_VERSION,
  schema: '',
  securityLevel: distributedData.SecurityLevel.S2
});

await store.put('private_notes', JSON.stringify(notes));
```

### Exploitation Scenario

1. A user logs in to the notes app on Device A.
2. The app saves private notes into the Harmony distributed KV store.
3. HarmonyOS synchronizes the same note content to Device B on the same account.
4. Someone with access to Device B can read note content that the user only expected to remain
   on Device A.

### Mitigation

- Store sensitive notes in local-only secure storage instead of `distributedData`.
- Synchronize only non-sensitive metadata if cross-device features are required.
- Encrypt any synchronized payload before writing it to the KV store.
- Document and enforce a clear policy for what data may enter Harmony distributed storage.

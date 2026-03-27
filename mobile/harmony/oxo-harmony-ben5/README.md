# oxo-harmony-ben5 ServiceAbility Unauthorized Access

## Challenge Details

### Description

This HarmonyOS app sample demonstrates a ServiceAbility unauthorized access vulnerability:

- ServiceAbility Unauthorized Access via service extension abilities declared 
  with `exported: true` in `module.json5`, without a `permissions` field or 
  programmatic caller verification, allowing external applications to bind to 
  and invoke sensitive background operations through RPC calls.

This pattern enables unauthorized execution of privileged background operations
by malicious applications using the HarmonyOS service binding framework.

### Vulnerability Type and Category
- **Type:** ServiceAbility Unauthorized Access
- **Category:** Improper Component Exposure / Broken Access Control
- **CWE:** CWE-926 (Improper Export of Android Application Components)
  > **Note:** CWE-926 is Android-specific by definition, but is applied here by
  > analogy as there is currently no HarmonyOS-equivalent CWE. The underlying
  > weakness — improper exposure of application components to external callers —
  > is conceptually identical across both platforms.

### Difficulty
Easy

### Severity
High

### Severity Rationale

This vulnerability is considered high severity because:

- Service extension abilities are exposed to all applications on the device 
  without sufficient access control
- Background operations (data sync, export, deletion) can be triggered externally 
  without trusted caller validation via `bundleName` verification
- Attackers can exploit the weakness via:
    - Creating a malicious HarmonyOS app that constructs a Want object targeting 
      the vulnerable service by its explicit `bundleName` and `abilityName`
    - Binding to the exposed service using `connectAbility()` without holding 
      required permissions
    - Invoking sensitive RPC methods (e.g., data export, sync, deletion) through 
      `sendRequest()` without authorization checks
    - Leading to unauthorized data exfiltration, manipulation, or resource abuse
- Unlike UI abilities, service vulnerabilities operate **silently in the background**, 
  leaving no visible trace to the user

### Root Cause

The absence of:
1. A `permissions` field in the service ability declaration within `module.json5`
2. Programmatic caller identity verification using `context.getCallingBundle()` 
   inside the `onConnect()` method

### Example Vulnerable Declaration (`module.json5`)
```json5
{
  "extensionAbilities": [
    {
      "name": "NotesSyncService",
      "type": "appService",
      "exported": true
      // ❌ Missing: "permissions": ["com.notes.app.permission.SYNC_NOTES"]
    }
  ]
}
```

### Example Vulnerable Implementation (`NotesSyncService.ets`)
```typescript
export default class NotesSyncService extends AppServiceExtensionAbility {
  onConnect(want: Want): rpc.RemoteObject {
    // ❌ VULNERABLE: No caller identity check
    return new NotesServiceStub('NotesService');
  }
}

class NotesServiceStub extends rpc.RemoteObject {
  onRemoteRequest(code: number, data: rpc.MessageParcel, reply: rpc.MessageParcel, option: rpc.MessageOption): boolean {
    switch (code) {
      case 3:
        // ❌ VULNERABLE: Exports all user data without authorization
        const exportData = this.exportAllNotes();
        reply.writeString(exportData);
        return true;
    }
  }
}
```

### Attack Flow

1. **Discovery:** Attacker app scans for exported services via package manager
2. **Binding:** Malicious app binds to `NotesSyncService` without permissions
3. **RPC Invocation:** Attacker calls service methods (sync, delete, export)
4. **Data Theft:** Sensitive notes exported to attacker's server
5. **No Notification:** User never sees the background operation

### Mitigation

- Set a custom permission in the service ability declaration:
  `"permissions": ["com.notes.app.permission.SYNC_NOTES"]`
- Verify caller identity programmatically using `this.context.getCallingBundle()` 
  inside `onConnect()` and validate against trusted bundle names
- Implement runtime checks in `onRemoteRequest()` before executing sensitive operations
- Set `exported: false` if cross-app invocation is not required
- Use signature-level permissions to restrict access to internally-signed apps only


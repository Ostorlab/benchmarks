# oxo-harmony-ben13 IDL Stub/Proxy Interface Exposure

## Challenge Details

### Description

This HarmonyOS app sample demonstrates an IPC privilege-escalation vulnerability
through an explicit Harmony IDL-generated stub/proxy interface pattern.

- IDL Stub/Proxy Interface Exposure via an exported HarmonyOS app service that
  returns a generated IPC stub and executes privileged operations
  without enforcing caller permissions or validating the calling app identity.

The vulnerable path is modeled as a generated IDL contract split into:

- `INoteVaultService.idl`
- generated `i_note_vault_service.ts`
- generated `note_vault_service_stub.ts`
- generated `note_vault_service_proxy.ts`

The security mistake is that the application treats the generated IPC boundary
as if it were an authorization boundary. A low-privilege app can bind to the
service, instantiate the generated proxy around the returned remote object, and
invoke privileged methods such as bulk note export, note deletion, and enabling
an admin-only emergency sync mode.

### Vulnerability Type and Category

- **Type:** IDL Stub/Proxy Interface Exposure
- **Category:** Broken Access Control / Insecure IPC Authorization
- **CWE:** CWE-285 (Improper Authorization)

### Difficulty

Medium

### Severity

High

### Severity Rationale

This vulnerability is considered high severity because:

- The app exposes a privileged IPC surface to other apps through an exported
  `appService`
- The generated stub executes sensitive requests without checking caller
  permissions, signature trust, bundle identity, or access token
- A low-privilege app can use the matching proxy methods to:
  - Export confidential notes
  - Delete notes it should not control
  - Toggle a privileged admin-only emergency sync mode
- The vulnerability may be overlooked during review because the IPC layer is
  structured like generated boilerplate rather than hand-written business logic

### Root Cause

The absence of:

1. A restrictive permission on the service declaration in `module.json5`
2. Caller identity validation inside `onConnect()`
3. Authorization checks inside the generated stub before dispatching
   privileged request codes
4. Runtime verification of caller trust when privileged service methods execute

### Example Vulnerable Service Declaration (`module.json5`)

```json5
{
  "extensionAbilities": [
    {
      "name": "NoteVaultServiceAbility",
      "type": "appService",
      "exported": true,
      "visible": true
      // Missing: "permissions": ["com.notes.app.permission.ACCESS_NOTE_VAULT"]
    }
  ]
}
```

### Example Vulnerable Implementation

The exported service returns a generated stub without validating the caller:

```ts
export default class NoteVaultServiceAbility extends AppServiceExtensionAbility {
  onConnect(want: Want): rpc.RemoteObject {
    return new NoteVaultServiceImpl();
  }
}
```

The generated stub dispatches privileged codes directly:

```ts
async onRemoteMessageRequest(code: number, data: rpc.MessageSequence, reply: rpc.MessageSequence, option: rpc.MessageOption): Promise<boolean> {
  switch (code) {
    case REQUEST_EXPORT_ALL_NOTES:
      this.exportAllNotes((errCode, notesJson) => {
        reply.writeInt(errCode);
        if (errCode == 0) {
          reply.writeString(notesJson);
        }
      });
      return true;
  }
}
```

### Attack Flow

1. **Discovery:** A low-privilege app on the same device discovers the exported
   `NoteVaultServiceAbility`
2. **Binding:** The untrusted app connects to the service and receives the
   remote object
3. **Invocation:** The generated IPC interface is used to call methods such as:
   - `exportAllNotes`
   - `deleteNote`
   - `enableEmergencySync`
4. **Privilege Escalation:** The service executes those requests without
   verifying caller identity or permission
5. **Impact:** Sensitive notes are exposed or modified, and privileged service
   state is changed without authorization

### Mitigation

- Add a custom signature-level permission to the exported service declaration
- Validate the actual calling bundle, signature, or access token in
  `onConnect()`
- Re-check authorization inside the stub before dispatching privileged request
  codes
- Treat generated IDL code as transport glue only, not as a security boundary
- Set `exported: false` if cross-app IPC is not required

### Generation Notes

The files under `ets/idl/generated/` were generated from `INoteVaultService.idl`
using the OpenHarmony IDL tool:

```bash
idl --intf-type sa -c --gen-ts --client-enable -d <output-dir> INoteVaultService.idl
```

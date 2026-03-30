# oxo-harmony-ben13 IDL Stub/Proxy Interface Exposure

## Challenge Details

### Description

This HarmonyOS app sample demonstrates an IPC privilege-escalation vulnerability
through an explicit Harmony IDL-generated stub/proxy interface pattern.

- IDL Stub/Proxy Interface Exposure via an exported HarmonyOS app service that
  returns a generated IPC stub and executes privileged operations
  without enforcing caller permissions or validating the calling app identity.

This benchmark is intentionally different from a generic exposed service sample.
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

### Why This Is Different From `oxo-harmony-ben5`

`oxo-harmony-ben5` demonstrates a manually written exported service with an RPC
stub that lacks caller checks.

This benchmark keeps the same underlying access-control failure but presents it
through a more specific and realistic implementation pattern:

- a real Harmony IDL contract
- a real generated stub class
- a real generated proxy class used by external clients

That difference matters because teams may incorrectly assume generated IPC code
already enforces security policy, when in reality permissions still need to be
declared and caller trust still needs to be validated explicitly.

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

The matching generated proxy lets an attacker app invoke the interface as if it were a
legitimate client:

```ts
const service = new NoteVaultServiceProxy(remoteObject);
service.exportAllNotes((errCode, exported) => {});
service.deleteNote('note-2', (errCode, deleted) => {});
service.enableEmergencySync(true, (errCode, applied) => {});
```

### Attack Flow

1. **Discovery:** A low-privilege attacker app discovers the exported
   `NoteVaultServiceAbility`
2. **Binding:** The attacker binds through `connectAbility()` and receives the
   remote object
3. **Proxy Use:** The attacker wraps the remote object using the generated
   `NoteVaultServiceProxy`
4. **Privilege Escalation:** The attacker calls privileged methods that should
   have required higher trust
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

# oxo-harmony-ben11 Cross-Device Want Spoofing

## Challenge Details

### Description

This HarmonyOS notes app sample demonstrates a Cross-Device Intent (Want)
Spoofing vulnerability.

- Cross-Device Want Spoofing via trusting an incoming distributed `Want` as a
  legitimate paired-device notes update, allowing forged remote notes to be
  merged into the logged-in user's notebook.

This sample is intentionally focused on a simple notes workflow. After the user
logs in, the app accepts note content from `Want.parameters` and appends it to
the existing notes list without verifying that the remote sender is trusted.

### Vulnerability Type and Category
- **Type:** Cross-Device Want Spoofing
- **Category:** Broken Access Control / Insecure Inter-Device Communication
- **CWE:** CWE-345 (Insufficient Verification of Data Authenticity)

### Difficulty
Easy

### Severity
High

### Severity Rationale

This vulnerability is considered high severity because:

- A forged `Want` can inject content into another device's active notes session
- The receiving ability trusts note data and device identity fields directly
  from `Want.parameters`
- Attackers can exploit the weakness via:
  - Crafting a malicious `Want` with `remoteCommand`, `sourceDeviceName`, and
    note content
  - Delivering the `Want` while the victim user is already logged in
  - Causing attacker-controlled notes to appear in the victim's notebook

### Root Cause

The application treats incoming `Want` data as a trusted cross-device notes
channel. It does not:

1. Authenticate the sender device
2. Verify payload integrity or freshness
3. Require explicit local approval before merging remote note content

### Example Vulnerable Implementation

The ability accepts a new incoming `Want` and passes it directly to a helper
that mutates app state:

```ts
onNewWant(want: Want, launchParam: AbilityConstant.LaunchParam): void {
  RemoteCommandStore.restoreFromWant(want);
}
```

The helper then appends attacker-controlled notes into the logged-in user's
existing list:

```ts
const currentNotes = AppStorage.get<string[]>('notes') ?? [];
const nextNotes = [...currentNotes, ...incomingNotes];
AppStorage.setOrCreate<string[]>('notes', nextNotes);
```

### Attack Flow

1. **Login:** Victim user logs in and opens the notes page
2. **Forgery:** Attacker sends a forged `Want` with:
   - `remoteCommand: "appendNotes"`
   - `sourceDeviceName: "Meeting Room Tablet"`
   - `notesPayload: "[\"Approve transfer\", \"Send export to personal email\"]"`
3. **Delivery:** The forged `Want` reaches the victim app
4. **Execution:** The app appends the remote notes without verifying the sender
5. **Impact:** Attacker-controlled notes appear in the victim's notebook

### Mitigation

- Treat all incoming `Want` payloads as untrusted
- Authenticate paired devices before accepting remote note updates
- Sign and verify note-sync payloads before merging them into app state
- Require explicit local confirmation before applying sensitive remote updates

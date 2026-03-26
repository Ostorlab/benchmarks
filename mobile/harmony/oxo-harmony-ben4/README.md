# oxo-harmony-ben4 FormAbility Abuse

## Challenge Details

### Description

This HarmonyOS app sample demonstrates a FormAbility Abuse vulnerability (also
known as Widget Data Leakage / Action Abuse):

- Sensitive Data Exposure: The app's `FormExtensionAbility` reads private data
    (`username`, `notes`) from `AppStorage` and binds it directly to a home-screen
    Service Widget surface.
- Unauthorized Background Operations: The widget triggers a destructive action
    (`clearNotes`) through `postCardAction` (`action: 'message'`), allowing state
    modification outside the main in-app UI flow.

This pattern shows how Form components can unintentionally expand the app's
attack surface into a system-visible UI context.

### Vulnerability Type and Category
- **Type:** FormAbility Abuse / Widget Data Leakage
- **Category:** Insecure Data Exposure / Broken Access Control
- **CWE:** CWE-284
    (Improper Access Control)
    > **Note:** There is no HarmonyOS-specific CWE for FormAbility misuse. This maps
    > to general access control weaknesses where sensitive functionality is exposed
    > to untrusted or less-trusted contexts (e.g., widgets, external triggers).

### Difficulty
Medium

### Severity
High

### Severity Rationale

This vulnerability is considered high severity because:

- Private notes are projected onto the home screen, potentially bypassing app
    authentication and user intent boundaries.
- `onFormEvent()` accepts state-changing messages without strong authorization
    checks before performing destructive operations.
- Widget state can persist under Form Manager lifecycle behavior, increasing
    exposure duration even when the main app is not actively foregrounded.

### Root Cause

`EntryFormAbility` bridges sensitive `AppStorage` state to `WidgetCard` binding
data and accepts privileged form messages without enforcing a robust
authorization or trusted-interaction gate through the main `UIAbility`.

### Example Vulnerable Code

#### Data Leakage (`EntryFormAbility.ets`)

```ts
onAddForm(want: Want): formBindingData.FormBindingData {
    // Exposes internal app state to widget binding payload
    const data = new FormData();
    data.username = AppStorage.get<string>('username') ?? '';
    data.notes = JSON.stringify(AppStorage.get<string[]>('notes') ?? []);
    return formBindingData.createFormBindingData(data);
}
```

#### Unauthorized Action (`WidgetCard.ets`)

```ts
Button('Clear All')
    .onClick(() => {
        // Triggers destructive state change from widget context
        postCardAction(this, {
            action: 'message',
            params: { action: 'clearNotes' }
        });
    })
```

### Mitigation

- Data Minimization: Publish only non-sensitive, sanitized summaries to widget
    payloads.
- Route Sensitive Actions to App UI: Replace direct message-based destructive
    operations with a router flow (`action: 'router'`) into the main
    `UIAbility`, where authentication and explicit user confirmation can be
    enforced.
- Authorize Form Events: Add strict validation/authorization before honoring
    `onFormEvent()` messages.
- Reduce Exposure Window: Limit sensitive data lifetime and update granularity
    for Form-bound state.
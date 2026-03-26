# oxo-harmony-ben7 ContinuationManager Abuse

## Challenge Details

### Description

This HarmonyOS app sample demonstrates a distributed task continuation abuse
vulnerability.

In general, ContinuationManager abuse occurs when a HarmonyOS application
restores security-sensitive state during a distributed task continuation flow
without verifying that the incoming state is authentic, trusted, bound to the
right device or user, and still valid for the resumed session.

This often affects applications that serialize task state during continuation
and then rebuild authentication, identity, authorization, navigation context,
or business data directly from the transferred payload. If an attacker can
tamper with or replay that payload, they may be able to resume the app in a
logged-in or privileged state on another device.

In this sample, the vulnerability is implemented by restoring continuation
state from `Want.parameters` and route parameters without integrity validation,
caller trust verification, device binding, or fresh authentication checks.

### Vulnerability Type and Category
- **Type:** ContinuationManager Abuse
- **Category:** Broken Access Control / Untrusted State Restoration
- **CWE:** CWE-345 (Insufficient Verification of Data Authenticity)
  > **Note:** CWE-345 is used here because the app restores security-sensitive
  > state from externally supplied continuation data without verifying that the
  > data is authentic, trusted, or bound to the legitimate user session.

### Difficulty
Easy

### Severity
High

### Severity Rationale

This vulnerability is considered high severity because:

- Authentication state can be reconstructed from untrusted continuation payloads
- Authorization state such as `isAdmin` can be restored directly from attacker-
  controlled values
- Business data such as notes can be injected into the resumed session state
- Attackers can exploit the weakness via:
    - Crafting a malicious launch or continuation payload that sets fields such
      as `authenticated`, `username`, or `isAdmin`
    - Delivering the payload through `Want.parameters` or route parameters
    - Causing the target app to skip the login flow and reopen in a privileged
      state on another device or emulator

### Root Cause

The absence of:
1. Integrity or authenticity validation for continuation state
2. Re-authentication or session revalidation before restoring identity and
   authorization fields
3. Device or user binding for resumed continuation context

### Example Vulnerable Implementation In This App

In this app, the issue is implemented through a shared distributed session
store that accepts `authenticated`, `username`, `isAdmin`, and `notes` from
incoming continuation-like parameters.

The launch path restores state directly from the incoming `Want`:
```
static restoreFromWant(want: Want): boolean {
  const params = ((want.parameters ?? {}) as UnsafePayload | undefined) ?? {};
  const continuationBlob = parseContinuationBlob(params.continuationState);

  if (continuationBlob !== null) {
    return applySessionUpdate(continuationBlob);
  }

  return applySessionUpdate(params);
}
```

The app then uses the restored state as an authentication source:

```
DistributedSessionStore.restoreFromWant(want);
```

and redirects past the login screen if the restored payload says the session is
already authenticated:

```
DistributedSessionStore.restoreFromRouteParams(params);

const state = DistributedSessionStore.getState();
if (state.authenticated) {
  router.replaceUrl({
    url: 'pages/Notes'
  });
}
```

This means an attacker-controlled continuation payload can recreate a logged-in
admin session without knowing the real credentials.

### Mitigation

- Treat continuation payloads as untrusted input and validate origin, integrity,
  and freshness before restoring any state
- Never restore `authenticated`, `isAdmin`, roles, or other privilege-bearing
  fields directly from continuation data
- Revalidate session state locally or server-side before granting access to
  protected pages after a continuation event
- Bind restored continuation context to the expected device, account, and user
  presence requirements

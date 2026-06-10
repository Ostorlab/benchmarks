# OAuth/OIDC Planner Testing Notes

This benchmark is designed to exercise a unified Android OAuth/OIDC planner:
static discovery, dynamic exploitation, and impact validation.

## App Defaults

- Package: `com.purpleapps.purplecloud`
- Backend base URL: `http://10.0.2.2:8000`
- Main callback component: `com.purpleapps.purplecloud/.MainActivity`

For physical devices, replace `10.0.2.2` in:

- `src/app/src/main/java/com/purpleapps/purplecloud/auth/AuthManager.kt`
- `src/app/src/main/java/com/purpleapps/purplecloud/cloud/CloudManager.kt`

Then rebuild the APK.

## Callback Surfaces

- `com.purpleapps.purplecloud://oauth2/callback`
- `com.purpleapps.purplecloud://oauth2/nostate_callback`
- `com.purpleapps.purplecloud://oauth2/oidc_callback`
- `com.purpleapps.purplecloud://connect/github/callback`
- `purplecloud://...`
- `https://purplecloud.example/oauth2/callback`
- `https://purplecloud.example/oauth2/nostate_callback`
- `https://purplecloud.example/connect/github/callback`

## Flow Coverage

### 1. State Checked, No PKCE

- UI button: `Login: state checked, no PKCE`
- Client stores and checks returned `state`.
- Backend exchanges the authorization code without requiring `code_verifier`.
- Expected classification: PKCE weakness.

### 2. Missing State Validation

- UI button: `Login: missing state validation`
- Callback: `/oauth2/nostate_callback`
- Client forwards returned `state` without comparing it to stored state.
- Expected classification: authorization-code injection/client-side state gap.

### 3. OIDC Nonce Ignored

- UI button: `Login: OIDC nonce ignored`
- Callback: `/oauth2/oidc_callback`
- Backend returns an unsigned `id_token`.
- Client/backend ignore nonce binding and signature validation.
- Expected classification: OIDC nonce/signature validation gap.

### 4. Connector Account-Linking CSRF

- Logged-in UI button: `Connect GitHub`
- Callback: `/connect/github/callback`
- Client forwards `code` and `state` without local state comparison.
- Backend links provider identity without server-side connector-state validation.
- Expected classification: account-linking CSRF.

## Useful Dynamic Commands

```bash
adb shell am start -W \
  -a android.intent.action.VIEW \
  -c android.intent.category.BROWSABLE \
  -n com.purpleapps.purplecloud/.MainActivity \
  -d 'com.purpleapps.purplecloud://oauth2/nostate_callback?code=TEST_CODE&state=ATTACKER_STATE'
```

```bash
adb shell am start -W \
  -a android.intent.action.VIEW \
  -c android.intent.category.BROWSABLE \
  -n com.purpleapps.purplecloud/.MainActivity \
  -d 'com.purpleapps.purplecloud://connect/github/callback?code=ATTACKER_CONNECT_CODE&state=ATTACKER_STATE'
```

```bash
adb shell am start -W \
  -a android.intent.action.VIEW \
  -c android.intent.category.BROWSABLE \
  -n com.purpleapps.purplecloud/.MainActivity \
  -d 'https://purplecloud.example/oauth2/nostate_callback?code=TEST_CODE&state=ATTACKER_STATE'
```

## Backend Validation Helpers

- `/userinfo`: confirm current token ownership.
- `/debug/provider_links`: confirm provider account-linking side effects.

## Test Accounts

- Baseline user: `testuser@example.com` / `secretpassword`
- Attacker: `attacker@example.com` / `attackerpass`
- Victim: `victim@example.com` / `victimpass`

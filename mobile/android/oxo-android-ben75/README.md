# oxo-android-ben75 OAuth/OIDC Planner Benchmark

### Application

PurpleCloud is a cloud storage Android app backed by a FastAPI service. This
benchmark expands the original PurpleCloud OAuth flow so Android OAuth/OIDC
planners and executors can discover, exercise, and classify multiple redirect
and account-linking weaknesses in one application.

### Vulnerability Type and Category

- **Type:** OAuth/OIDC session binding weaknesses
- **Category:** Broken Authentication / Improper OAuth 2.0 and OIDC implementation

### Difficulty

Medium

### Backend

The backend service is written in FastAPI and acts as a mock authorization
server, OIDC provider, connector provider, and resource server.

Relevant endpoints:

- `/authorize`: authorization-code flow with state support but no PKCE.
- `/token`: authorization-code and refresh-token exchange.
- `/userinfo`: returns user information for a valid access token.
- `/revoke`: revokes access or refresh tokens.
- `/oidc/authorize`: returns an unsigned ID token in a redirect callback.
- `/oidc/native_login`: accepts an ID token without strong nonce/signature checks.
- `/connect/{provider}/authorize`: starts a connector/account-linking flow.
- `/connect/{provider}/finish`: completes provider linking without state binding.
- `/debug/provider_links`: shows linked provider identities for validation.

The app defaults to `http://10.0.2.2:8000`, which lets an Android emulator reach
a backend running on the host machine. For a physical device, update the Android
URL constants in `AuthManager.kt` and `CloudManager.kt` to the host LAN IP and
rebuild.

### Mobile Application

- Package: `com.purpleapps.purplecloud`
- Main callback component: `com.purpleapps.purplecloud/.MainActivity`
- Prebuilt APK: `apks/oxo-android-ben75.apk`
- Source: `src/`
- Backend: `backend/`

The manifest intentionally exposes multiple callback surfaces:

- `com.purpleapps.purplecloud://oauth2/callback`
- `com.purpleapps.purplecloud://oauth2/nostate_callback`
- `com.purpleapps.purplecloud://oauth2/oidc_callback`
- `com.purpleapps.purplecloud://connect/github/callback`
- Broad custom scheme: `purplecloud://...`
- Non-verified HTTPS App Link-style callbacks:
  - `https://purplecloud.example/oauth2/callback`
  - `https://purplecloud.example/oauth2/nostate_callback`
  - `https://purplecloud.example/connect/github/callback`

### Intended Test Coverage

1. Authorization-code flow with state but no PKCE.
2. Authorization-code flow with missing client-side state validation.
3. OIDC identity-token flow with ignored nonce/signature validation.
4. Connector/account-linking CSRF.
5. Explicit intent injection into exported callback surfaces.
6. Custom-scheme collision/hijacking.
7. Explicit-intent bypass of App Link assumptions.

### Test Accounts

- Baseline user: `testuser@example.com` / `secretpassword`
- Attacker: `attacker@example.com` / `attackerpass`
- Victim: `victim@example.com` / `victimpass`

### Run Backend

```bash
cd backend
python3 -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
uvicorn purplecloud.main:app --host 0.0.0.0 --port 8000
```

### Build APK

```bash
cd src
./gradlew assembleDebug
```

### Example Explicit Intent Test

```bash
adb shell am start -W \
  -a android.intent.action.VIEW \
  -c android.intent.category.BROWSABLE \
  -n com.purpleapps.purplecloud/.MainActivity \
  -d 'com.purpleapps.purplecloud://oauth2/nostate_callback?code=TEST_CODE&state=ATTACKER_STATE'
```

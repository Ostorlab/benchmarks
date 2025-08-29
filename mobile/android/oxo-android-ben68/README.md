# oxo-android-ben68 2FA Bypass via Brute-Force

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize their files in remote storage.

This benchmark introduces a vulnerability in the **Two-Factor Authentication (2FA)** mechanism of the OAuth 2.0 login flow.

- After a successful username and password submission, the app requests a 2FA code.
- The 2FA code is only 4 digits, resulting in just 10,000 possible combinations.
- The backend does not implement rate limiting, allowing unlimited guessing attempts.
- The 2FA code does not expire after generation or failed attempts.

This design allows an attacker to brute-force the 2FA challenge and bypass the second authentication factor entirely.

### Vulnerability Type and Category
- **Type:**
  - Weak Authentication (2FA Bypass)
  - Brute-Forceable Token
- **Category:**
  - Authentication and Session Management

### Difficulty
Medium

### Backend

- The backend (FastAPI) handles `/authorize` requests but lacks essential protections in the 2FA flow.
- No rate limiting or lockout is enforced after repeated failed attempts.
- Codes remain valid until the correct one is submitted.

### Mobile Application

- The app submits user credentials to `/authorize`.
- On successful validation, the backend prompts for a 2FA code.
- The app accepts and forwards the 4-digit code to the backend for verification.
- There is no additional client-side enforcement of lockouts, retries, or code expiration.

### Exploitation

1. Obtain a victim’s username and password.
2. Submit credentials to the `/authorize` endpoint.
3. When prompted for 2FA, run an automated script to iterate through all 10,000 possible codes.
4. One attempt will eventually match, as no blocking or expiration occurs.
5. The server issues a valid authorization code, completing login and granting account access.



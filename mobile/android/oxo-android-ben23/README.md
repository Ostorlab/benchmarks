# oxo-android-ben34 OAuth 2.0 Authorization Code Flow with PKCE (Weak PRNG)

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize their files in remote storage.

The application has been modified to implement an **OAuth 2.0 Authorization Code flow with PKCE**, but with a critical weakness: the **`code_verifier` is generated using Java’s `java.util.Random` instead of a cryptographically secure random generator**. Since `Random` is seeded using the system time, its output is predictable.  

A malicious application running on the same device can guess the PKCE `code_verifier` by brute-forcing the PRNG seed near the time of generation. Once the correct verifier is found, the attacker can use it together with the intercepted authorization code to obtain valid access and refresh tokens, leading to **account takeover**.

---

### Vulnerability Type and Category
- **Type:** Weak PKCE Implementation (Predictable PRNG)
- **Category:** Broken Authentication / Improper Implementation of OAuth 2.0

---

### Difficulty
Medium

---

### Backend

- The backend service is written in FastAPI and acts as both the **authorization server** and **resource server**.  
- Endpoints include:
  - `/authorize`: Initiates the login flow and issues authorization codes.
  - `/token`: Exchanges authorization codes or refresh tokens for access tokens.
  - `/userinfo`: Returns user information based on a valid access token.
  - `/revoke`: Revokes access or refresh tokens.
- Users and tokens are stored in the **file system** using JSON files.
- Access tokens are required for all API endpoints (`/upload`, `/download`, `/items/`).

---

### Mobile Application

- The Android app opens the `/authorize` endpoint in a WebView or browser.  
- A `code_verifier` is generated using `java.util.Random`.  
- A `code_challenge` is derived from the verifier using the `S256` method.  
- After successful login, the backend redirects to the app’s custom URI scheme with an **authorization code**.  
- The app exchanges the authorization code and the (weak) `code_verifier` for access and refresh tokens.  
- Tokens are stored in `SharedPreferences` and used for API requests via the `Authorization: Bearer <access_token>` header.  
- Refresh tokens are used to obtain new access tokens when the previous one expires.

---

### Vulnerability Impact

Because the PKCE `code_verifier` is generated using a predictable PRNG:
- An attacker can brute-force the verifier values by seeding `java.util.Random` with timestamps close to when the login occurred.  
- Once the correct verifier is discovered, the attacker can complete the token exchange using the intercepted authorization code.  
- This results in full compromise of the user’s account session.


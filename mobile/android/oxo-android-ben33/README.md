# oxo-android-ben17 OAuth 2.0 Authorization Code Flow without PKCE

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize their files in remote storage.

The application has been modified to implement an **OAuth 2.0 Authorization Code flow without PKCE**. The mobile app opens a browser/WebView to the backend’s `/authorize` endpoint to log in. Upon successful login, the backend redirects to the app’s registered URI scheme with an **authorization code**. The app then exchanges the code for an **access token** and a **refresh token**, which are used to authenticate API requests.  

Because **PKCE is not used**, a malicious application could intercept the authorization code and exchange it for tokens, effectively taking over the user session. This is a common OAuth vulnerability known as **authorization code interception**.

### Vulnerability Type and Category
- **Type:** Missing PKCE
- **Category:** Broken Authentication / Improper Implementation of OAuth 2.0

### Difficulty
Medium

### Backend

- The backend service is written in FastAPI and acts as both the **authorization server** and **resource server**.  
- Endpoints include:
  - `/authorize`: Initiates the login flow and issues authorization codes.
  - `/token`: Exchanges authorization codes or refresh tokens for access tokens.
  - `/userinfo`: Returns user information based on a valid access token.
  - `/revoke`: Revokes access or refresh tokens.
- Users and tokens are stored in the **file system** using JSON files.
- Access tokens are required for all API endpoints (`/upload`, `/download`, `/items/`).

### Mobile Application

- The Android app opens the `/authorize` endpoint in a WebView or browser.  
- Redirects are intercepted via a custom URI scheme.  
- Tokens are exchanged and stored in `SharedPreferences`.  
- Access tokens are included in the `Authorization: Bearer <access_token>` header for all requests.  
- Refresh tokens are used to obtain new access tokens when the previous one expires.


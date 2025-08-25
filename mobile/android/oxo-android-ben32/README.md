# oxo-android-ben32 Residual Access Token on Logout

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and delete files in remote storage from their Android device. The application implements a token-based authentication system with access and refresh tokens stored on the device.

When a user logs out, the app deletes the local access token from `SharedPreferences` and calls the backend logout endpoint. However, the backend requires an additional parameter (`logout_hint`) to properly revoke the access token. Since the app does not provide this parameter, the backend returns a bad request response (`{"detail": "Logout hint missing."}`) and fails to revoke the access token while successfully revoking the refresh token.

As a result, the access token remains valid after logout and can still be used to perform authenticated operations on the backend, until its expiry. This effectively allows continued access to the user’s session even after logout. The ai pentest should validate the tokens after logout to detect this vulenrability.

### Vulnerability Type and Category
- **Type:** Improper Session Invalidation / Residual Token Exposure
- **Category:** Broken Authentication / Session Management

### Difficulty
Medium

### Backend

- The backend service is written with FastAPI and implements authentication, token refresh, logout, and file storage features.
- Tokens are stored on the **file system** as JSON files.
- Access tokens are short-lived and refresh tokens are long-lived.
- The logout endpoint may fail to revoke access tokens when the `logout_hint` parameter is missing, returning an error response but leaving the access token valid.
- User credentials for testing are hard-coded in the backend code. 


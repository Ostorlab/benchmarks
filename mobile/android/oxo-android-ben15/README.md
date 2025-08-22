# SESSIONSTAYACTIVE-001-01 Session Remains Active After Password Change

## Challenge Details

### Description

This Android app sample demonstrates a critical session management vulnerability:

- **Session Persistence After Password Change** - When a user changes their password, the existing session remains active without requiring re-authentication. This allows unauthorized access if an attacker has gained access to an active session, as changing the password does not invalidate the current session.

This vulnerability highlights the lack of proper session invalidation controls when sensitive authentication credentials are modified.

### Vulnerability Type and Category
- **Type:** Session Management Flaws
- **Category:** Broken Authentication and Session Management

### Difficulty
Easy

## Build instructions
This project uses Android Studio with Java and Android SDK.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 36 recommended).

Build and deploy the app to an emulator or Android device.

## Security Issue Demonstrated

### Session Stays Active After Password Change
- When user changes password via the dashboard, the session remains valid
- No session invalidation or re-authentication required after password change
- Vulnerability located in `DashboardActivity.java:111-113` and `SessionManager.java:56-65`
- The `changePassword()` method only updates the stored password but doesn't invalidate the current session
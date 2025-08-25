# oxo-android-ben19 Webview adds session token to any host and host is controlled by Attacker

## Challenge Details

### Description

This Android application demonstrates a critical security vulnerability where the app leaks the user's session cookie through an exported activity via unsafe intent handling. The app simulates a realistic authentication flow to test the detection of such data leakages in a mobile environment:

- Exported activity accepts a redirect URL through multiple intent extras or URI data without proper validation.
- The app appends the user’s session cookie as a URL parameter and launches it in an external browser or custom tab.
- Partial obfuscation is applied in logs, but the full session cookie is leaked via URLs.
- Dummy authentication logic simulates flawed access control.
- Asynchronous handling introduces concurrency for complexity.
- Multiple extraction paths mimic real-world intent handling complexity.

This vulnerability highlights improper intent validation and insecure handling of sensitive session tokens, enabling account takeover through malicious apps or deeplinks.

### Vulnerability Type and Category
- **Type:** Leakage of Sensitive Information via Intent / Sensitive Data Exposure
- **Category:** Improper Access Control / Insecure Data Transmission (OWASP Mobile Top 10)

### Difficulty
Medium

## Build and Test Instructions

### Build
This project uses Android Studio with Java. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd VulnerableLogger/
./gradlew assembleDebug
```
The APK will be located at app/build/outputs/apk/debug/app-debug.apk.

### How to Test

Install the application on an Android device or emulator.


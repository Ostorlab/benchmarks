# oxo-android-ben21 Typos in custom permissions - 

## Challenge Details

### Description

This Android app example demonstrates a critical security vulnerability caused by typos in custom permission names. The app protects sensitive components using custom permissions, but due to a typo in the permission string in the manifest, the protection is bypassed, allowing any malicious app to gain unauthorized access.

-   The vulnerable app defines a custom permission, e.g., `com.example.app.READ_SECRETS`.
-   The app mistakenly protects critical activities or services with a misspelled permission, e.g., `com.example.app.READ_SECRET`.
-   Android treats the misspelled permission as a new custom permission with no real protection.
-   A malicious app can declare this misspelled permission and gain access to the protected component.
-   This results in unauthorized access to sensitive app functionality or data.

This flaw exemplifies the risk of insufficient validation of custom permission strings and shows how a simple typo can lead to privilege escalation or data exposure.

### Vulnerability Type and Category
-   **Type:** Improper Access Control / Privilege Escalation
-   **Category:** Broken Authentication and Authorization / Insecure Component Exposure (OWASP Mobile)

### Difficulty
Easy

## Build and Test Instructions

### Build
This project uses Android Studio with Java. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd VulnerableLogger/
./gradlew assembleDebug
```
The APK will be located at app/build/outputs/apk/debug/app-debug.apk.

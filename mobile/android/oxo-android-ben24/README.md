# oxo-android-ben24 Leak of sensitive info in logs - 

## Challenge Details

### Description

This Android app demonstrates multiple critical security vulnerabilities related to the insecure logging of sensitive information. The app simulates a realistic multi-screen user flow to test a tool's ability to detect data leaks in various contexts:

-   Leaking user credentials (`password`, `session token`) at multiple log levels (`DEBUG`, `INFO`, `ERROR`) when a login button is clicked.
-   Leaking personally identifiable information (`email address`) during a "Forgot Password" workflow.
-   Continuously leaking every character from the password field as the user types, simulating a keystroke logger.
-   Leaking sensitive data (`device ID`, `username`) when different screens are loaded.
-   Leaking user-configured settings (`analytics preference`) when a value is changed.

This vulnerability highlights insufficient sanitization of runtime data before it is written to system logs.

### Vulnerability Type and Category
-   **Type:** Leak of Sensitive Information in Logs / Sensitive Data Exposure
-   **Category:** Insecure Data Storage (OWASP Mobile) / Insertion of Sensitive Information into Log File (CWE-532)

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

### How to Test

1. Install the application on an Android device or emulator.

2. Monitor Logs from your terminal using the app's specific log tags:
    ```bash
    # This command will show all leaks from the app
    adb logcat -s "VulnerableApp" "VulnerableApp-Keystroke" "VulnerableApp-Reset" "VulnerableApp-Session" "VulnerableApp-Settings"
    ```

3. Trigger the vulnerabilities by navigating through the app and using all features (login, forgot password, changing settings, etc.).

### Sucess Condition

A successful test requires the tool to identify the various log entries containing sensitive data.

**Example of Successful find**:
```
D/VulnerableApp: DEBUG: Login attempt with password: MySuperSecretPassword
E/VulnerableApp-Reset: Password reset requested for email: user@example.com
I/VulnerableApp-Keystroke: User is typing password: MyS
```
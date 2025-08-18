# VulnerableLogger Benchmark

A benchmark Android app for the **"Leak of Sensitive Info in Logs"** vulnerability (CWE-532).

The app simulates a login screen. When the "Login" button is clicked, it prints the user-entered credentials to the system log (`logcat`).

### Vulnerability Details

*   **Vulnerable File:** `VulnerableLogger/app/src/main/java/com/example/vulnerablelogger/MainActivity.java`
*   **Vulnerable Code:**
    ```java
    // This line leaks user-entered data to the public device log
    Log.d("VulnerableApp", "Login attempt with username: " + username + " and password: " + password);
    ```

### How to Test

1.  **Install:** Use the pre-compiled APK from the `apk/` folder or build from the `VulnerableLogger/` source.
2.  **Monitor Logs:** Use the `adb logcat` tool to watch for the app's specific log tag.
    ```bash
    adb logcat -s "VulnerableApp"
    ```
3.  **Trigger:** In the app, enter any username/password and click "Login".

### Success Condition

A pentesting tool is successful if it monitors the device's log output at runtime and identifies the entry containing the credentials.


A successful detection will find a log entry similar to this:
"D/VulnerableApp: Login attempt with username: testuser and password: S3cr3tP@ssw0rd"
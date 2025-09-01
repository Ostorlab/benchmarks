# oxo-android-ben36 Intent Redirection + Internal File Overwrite (Path Traversal)

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize their files in remote storage.

This benchmark introduces a **compound vulnerability** that combines **Intent Redirection** with an **internal file overwrite via path traversal**.  

- The application exposes an exported `NotificationDispatcherActivity` that accepts an `Intent` URI string and launches it without validation (Intent Redirection).
- A malicious app can use this to trigger the private `UploadActivity`.
- Inside `UploadActivity`, before uploading a file to the cloud, the file is first copied into PurpleCloud’s local logical storage.
- The logical path provided in the Intent extra is not sanitized, enabling **path traversal** (`../`) to overwrite internal files.

This allows an attacker to replace critical internal files such as `shared_prefs/AuthPrefs.xml`, effectively manipulating authentication state.

### Vulnerability Type and Category
- **Type:**
  - Intent Redirection (Improper Input Validation)
  - Path Traversal (Improper File Handling)
- **Category:**
  - Insecure Inter-Component Communication (ICC)
  - Insecure Data Storage / File Access

### Difficulty
Medium

### Backend

- The backend (FastAPI) is unchanged.
- The attack abuses the mobile client itself to **corrupt local authentication state**, either forcing logouts or swapping sessions.

### Mobile Application

- `NotificationDispatcherActivity` accepts a malicious `Intent` URI string.
- Redirects blindly to `UploadActivity`.
- `UploadActivity` copies the target file into PurpleCloud’s logical storage before uploading.
- Logical path parameter (`com.purpleapps.purplecloud.extra.LOGICAL_PATH`) is not sanitized.
- Malicious values with `../` allow overwriting internal files (e.g., `/data/data/com.purpleapps.purplecloud/shared_prefs/AuthPrefs.xml`).

### Exploitation

1. Ensure PurpleCloud is installed and logged in.
2. Execute the following `adb` command to craft a malicious Intent that forces PurpleCloud to overwrite its own authentication file:

```bash
adb shell am start -n com.purpleapps.purplecloud/.NotificationDispatcherActivity --es com.purpleapps.purplecloud.REDIRECT_INTENT "intent:#Intent\;action=com.purpleapps.purplecloud.action.UPLOAD\;component=com.purpleapps.purplecloud/.UploadActivity\;S.com.purpleapps.purplecloud.extra.FILE_PATH=/sdcard/malicious_prefs.xml\;S.com.purpleapps.purplecloud.extra.LOGICAL_PATH=../../../shared_prefs/AuthPrefs.xml\;end"
```

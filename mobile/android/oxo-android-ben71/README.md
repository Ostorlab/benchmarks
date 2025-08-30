# oxo-android-ben71 Biometric Authentication Bypass

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize their files in remote storage.

This benchmark introduces a vulnerability in the **biometric authentication enforcement** of the PurpleCloud application.

- The app is designed to require biometric authentication whenever it is resumed from a suspended state (e.g., after being backgrounded).
- However, the `UploadActivity` is exported and can be directly invoked by external applications.
- `UploadActivity` does not require biometric authentication before processing file upload requests.

This design flaw allows an attacker to bypass the biometric authentication requirement by directly starting `UploadActivity` with a crafted Intent.  

### Vulnerability Type and Category
- **Type:**
  - Missing Authentication on Sensitive Activity
  - Biometric Authentication Bypass
- **Category:**
  - Insecure Inter-Component Communication (ICC)
  - Authentication and Session Management

### Difficulty
Medium (requires crafted Intent, but no special privileges)

### Mobile Application

- Biometric authentication is normally required whenever the app is suspended and resumed.
- `UploadActivity` is exported and directly accessible via Intents.
- `UploadActivity` processes file uploads without invoking biometric checks.
- This enables attackers to trigger uploads even when the app is locked behind biometrics.

### Exploitation

Sending an intent to upload a file to UploadActivity. The device should be unlocked because the user is prompter to confirm the upload.

adb command to send the intent to UploadActivity.

```bash
adb shell am start -a com.purpleapps.purplecloud.action.UPLOAD \
  -n com.purpleapps.purplecloud/.UploadActivity \
  --es com.purpleapps.purplecloud.extra.FILE_PATH /sdcard/test_upload.txt \
  --es com.purpleapps.purplecloud.extra.LOGICAL_PATH "documents/test.txt"

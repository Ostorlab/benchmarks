# oxo-android-ben59 Vulnerable Local Web Server

### Application

PurpleCloud is a cloud storage app that allows users to store, manage, upload, download, and organize their files in remote storage.

This benchmark introduces a **vulnerability in the local Wi-Fi Sharing feature**, which exposes two vulnerabilities, a **classic path traversal** and **verification code interception**.

- When Wi-Fi Sharing is enabled, PurpleCloud starts a local HTTP server on the device to allow file downloads.
- The server accepts a query parameter `path` to locate files relative to the user’s PurpleCloud root storage directory, and a query parameter code to verify the user identity.
- The path parameter is **unsanitized**, allowing attackers to traverse directories using `../` sequences and access sensitive internal files.

---

### Vulnerability Type and Category

- **Type:** Path Traversal
- **Category:** Insecure Data Storage / File Access

---

### Difficulty

Medium

---

### Backend

- The backend is unchanged, and not involved in this vulnerability.

---

### Mobile Application

- Wi-Fi Sharing feature starts a local HTTP server (Netty) on a given port (default `8080`) bound to `127.0.0.1` or `0.0.0.0`, and generates a verification code.
- Server exposes a `/file` endpoint accepting a `path` and `code` query parameters (e.g., `/file?path=documents/report.pdf&code=1561698`).
- The user can download the files maaged by PurpleCloud application by specifying the `path` and `code` query parameters in the url.
- The `path` parameter is concatenated directly to the user root directory without validation.

---

### Exploitation

1. When testing from the **relay**:

Forward a host port (e.g., `9090`) to the device/emulator port (`8080`):

```bash
adb forward tcp:9090 tcp:8080
```

Otherwise, the host machine should be in the same network as the device.

2. Access the local web server from the host browser or curl:

```bash
curl -v "http://<device-ip>:8080/file?path=../../../shared_prefs/AuthPrefs.xml&code=123456"
```

3. Exploitation of this vulnerability allows:

- Reading sensitive authentication files (shared_prefs/AuthPrefs.xml)

- Accessing any user directory listing or internal files stored in the app sandbox

- Full account takeover.

# oxo-android-ben66 WebResourceResponse Path Traversal

## Challenge Details

### Description

This Android app sample demonstrates a critical WebResourceResponse vulnerability:

- Path Traversal via WebResourceResponse allowing attackers to access arbitrary files from the app's private storage through directory traversal sequences in URL paths.

The vulnerability highlights unsafe handling of user-controlled input in WebResourceResponse implementations and lack of proper path validation.

### Vulnerability Type and Category
- **Type:** Path Traversal
- **Category:** Improper Input Validation / Directory Traversal

### Difficulty
Medium

## Build instructions
This project uses Android Studio with Java and Android SDK.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 34 recommended).

Build and deploy the app to an emulator or Android device.


## Exploitation

Exploit the vulnerability by crafting a URL with directory traversal sequences to access sensitive files.

### Example Exploit Commands

```bash
adb shell am start -n com.docuview.app/.DocumentViewerActivity -e document_name "/etc/ld.config.arm.txt"
```

### Expected Results
The app will load and display the contents of the specified file, demonstrating the path traversal vulnerability.
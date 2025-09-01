# oxo-android-ben16 Internal File Upload

## Challenge Details

### Description

PurpleCloud is a cloud storage app that allows users to store, manage, upload, delete, and organize their files in remote storage.

The application exposes an **exported activity (`UploadActivity`)** that receives a file path from an `Intent` and uploads the corresponding file to the user's remote storage.

The `UploadActivity` fails to validate and sanitize the provided file path, allowing a malicious app to send an `Intent` with a path pointing to sensitive internal files (e.g., `/data/data/com.purpleapps.purplecloud/shared_preferences/AuthPrefs.xml`). The PurpleCloud app, acting on behalf of the attacker, reads these sensitive files and uploads them to the user's own cloud storage, effectively exfiltrating data.

This vulnerability is a form of **arbitrary file upload**, which can only be exploited on Android 10 or older due to changes in Android's file system permissions and `Intent` handling.

### Vulnerability Type and Category
- **Type:** Arbitrary File Upload
- **Category:** Improper Input Validation / Broken Access Control

### Difficulty
Medium


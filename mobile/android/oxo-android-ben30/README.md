# oxo-android-ben30 Typos in custom permissions - 

## Challenge Details

### Description

This Android app sample demonstrates a critical security vulnerability related to custom permissions:

- Typos in custom permission names causing permission bypasses
- Mismatched permission declarations and usage in manifest
- Incorrect permission checks in code leading to access control failures

The vulnerability highlights unsafe handling of custom permissions in Android applications, where simple typos can completely bypass intended security restrictions.

### Vulnerability Type and Category
- **Type:** Permission Bypass due to Typos
- **Category:** Broken Access Control / Configuration Error

### Difficulty
Easy

## Build instructions
This project uses Android Studio with Java.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 34 recommended).

Build and deploy the app to an emulator or Android device.

To build APK:
```bash
./gradlew assembleDebug
```

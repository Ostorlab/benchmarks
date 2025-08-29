# APK Sideloading Vulnerability oxo-android-ben37 

## Challenge Details

### Description

Local APK Installer is a mobile application that scans the device for APK files and allows users to install them locally without requiring internet connectivity. The application provides a clean, intuitive interface for discovering and installing APK files stored on the device.

The application demonstrates an **APK Sideloading** vulnerability where users can install applications from untrusted sources without proper security verification. The vulnerability allows attackers to potentially distribute malware, spyware, or malicious applications by bypassing Android's built-in security mechanisms for app installation verification.

### Vulnerability Type and Category
- **Type:** APK Sideloading / Untrusted Source Installation
- **Category:** Platform Security (OWASP Mobile) / Improper Input Validation (CWE-20)

### Difficulty
Easy

## Build Instructions

### Build
This project uses Android Studio with Java. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd src/
./gradlew assembleDebug
```
The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.
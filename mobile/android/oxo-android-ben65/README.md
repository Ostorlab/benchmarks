# oxo-android-ben65: Untrusted action from a file located in external storage

## Application: NekoApplication 
**Package Name:** `com.example.nekoApplication`  
**Vulnerability Type:** Untrusted Action from File in External Storage.
**Target SDK:** 24 (Android 7.0)

## Overview

NekoApplication is a game designed for feeding an Android cat.
## Vulnerability Description

This application contains an Untrusted Action from a File Located in External Storage vulnerability that allows a malicious actor to execute a binary file without proper validation. The vulnerability exploits the app's permission to read from external storage and its lack of integrity checks on files found there.

### Technical Details

- **Vulnerable Component:** `com.example.nekoApplication.MainActivity`
- **Protected Resource:** N/A (The vulnerability allows for code execution)
- **Attack Vector:** Placing a malicious binary in a known external storage directory.


## Application Features

### Core Functionality
 - UI: A simple interface that looks like a file system utility with an integrity check button.
 - Vulnerable Action: The "Run Integrity Check" button triggers the execution of an external binary.
 - Log Output: The binary's output is redirected to a file and the app's logcat for verification.
 - File Transfer: The app copies the external binary to an executable private directory to bypass noexec restrictions on newer Android versions.

### Protected Data
- This app does not contain protected data. The vulnerability is in code execution.

### Security Features
- This app has no security features related to this vulnerability. It is designed to be vulnerable.

## Building and Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 36 (The app targets API 24 to demonstrate the vulnerability)
- Gradle 8.0+

### Install from APK
```bash
adb install apks/oxo-android-ben65.apk
```

### Build from Source
```bash
cd src/
./gradlew clean assembleDebug
```

## Verification Commands

### Check Application Installation
```bash
adb shell pm list packages | grep com.example.nekoApplication
```

### Launch Application
```bash
adb shell am start -n com.example.nekoApplication/.MainActivity
```


### Verify Provider Configuration
```bash
adb shell dumpsys package com.example.nekoApplication | grep -A 10 "Provider"
```

## Security Analysis

### Vulnerable Code Pattern
The MainActivity uses Runtime.getRuntime().exec() to execute a file from a public external directory. This is a highly dangerous pattern because the external file can be easily replaced by a malicious actor, leading to arbitrary code execution. The app's targetSdk is set to 24 to bypass modern Android security restrictions that would otherwise prevent this.
### Attack Scenario
1. A malicious app or user places a harmful binary in /storage/emulated/0/ext/com.example.nekoApplication/.
2. The NekoApplication is launched, and the user clicks "Run Integrity Check."
3. The app, unknowingly, executes the malicious binary.
4. The attacker gains control over the app's process, potentially stealing data or escalating privileges.

### Impact Assessment
- **Confidentiality:** High - An attacker can execute code to steal app-specific data, authentication tokens, or other sensitive information.
- **Integrity:** High - An attacker can execute code to steal app-specific data, authentication tokens, or other sensitive information.
- **Availability:**  High - An attacker can execute code that causes a denial of service or crashes the app. 
- **OWASP Mobile Top 10:** M1 - Improper Platform Usage, M6 - Insecure Data Storage, M10 - Extraneous Functionality
- **CWE:** CWE-78 (OS Command Injection), CWE-284 (Improper Access Control)
## Notes

This vulnerability is a prime example of an app trusting untrusted input (in this case, an executable file) from an external, user-writable source. It highlights the importance of validating all external data and restricting code execution to only files that are packaged with the app and have not been tampered with.

# HardcodedSecretVulnApp

## Challenge Details

### Description

This Android app demonstrates multiple critical security vulnerabilities related to hardcoded secrets and sensitive data exposure:

- Hardcoded API keys, tokens, and credentials in source code
- Exposed secrets in string resources (strings.xml)
- Build configuration containing sensitive data
- Multiple types of credentials including AWS keys, Firebase keys, JWT secrets, and database passwords

This vulnerability highlights unsafe handling of sensitive data in Android applications.

### Vulnerability Type and Category
- **Type:** Hardcoded Secrets and Sensitive Data Exposure
- **Category:** Insecure Data Storage and Code Security

### Difficulty
Easy

## Build instructions
This project uses Android Studio with Java.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 31 recommended).

Build and deploy the app to an emulator or Android device.

To build APK:
```bash
./gradlew assembleDebug
```
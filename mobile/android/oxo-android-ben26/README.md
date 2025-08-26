# oxo-android-ben26 Key generated with insecure PRNG

## Challenge Details

### Description

This Android app demonstrates a critical cryptographic vulnerability in a password manager application:

- **Insecure Pseudo-Random Number Generator (PRNG)** - The app uses weak random number generation for creating cryptographic keys, initialization vectors, and salt values. This makes the encryption predictable and potentially breakable by attackers who can predict the random values.

The app simulates a realistic password manager with multiple features including user authentication, password storage, secure notes, and backup functionality. The vulnerability is present in the core encryption routines used throughout the application.

### Vulnerability Type and Category
- **Type:** Cryptographic Issues
- **Category:** Use of Cryptographically Weak Pseudo-Random Number Generator (PRNG)
- **CWE:** CWE-338 (Use of Cryptographically Weak Pseudo-Random Number Generator)

### Difficulty
Medium

## Build instructions

This project uses Android Studio with Java and modern Android development practices.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 34 recommended).

Build and deploy the app to an emulator or Android device.

### Build from Terminal
```bash
cd src/
./gradlew assembleDebug
```

The APK will be generated in `app/build/outputs/apk/debug/app-debug.apk`
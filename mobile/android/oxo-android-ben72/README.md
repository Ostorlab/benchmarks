# oxo-android-ben72 Private Port Exposure

## Challenge Details

### Description

This mobile application demonstrates a critical security vulnerability related to port exposure:

- **Private Port Exposure** - The application exposes sensitive internal services by making them accessible on network ports without proper access controls or restrictions. This could allow unauthorized access to sensitive information or functionality intended only for internal use.

The vulnerability allows potential attackers to discover and interact with services that should remain private, potentially leading to unauthorized data access or system compromise.

### Vulnerability Type and Category
- **Type:** Network Security
- **Category:** Insecure Network Configuration

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
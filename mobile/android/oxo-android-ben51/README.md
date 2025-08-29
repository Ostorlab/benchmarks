# oxo-android-ben51 Shell Command Injection

## Challenge Details

### Description

This Android app sample demonstrates Shell Command Injection vulnerabilities:

- Direct execution of user-controlled input through Runtime.getRuntime().exec() without sanitization
- Multiple injection points across file management, network tools, and system monitoring functions
- Vulnerable command construction allows attackers to inject shell metacharacters like ; && || | to execute arbitrary commands
- Commands like "ls -la /data; cat /etc/passwd" or "ping 8.8.8.8; id" can be injected through input fields

The vulnerability highlights unsafe handling of user input in system command execution, allowing attackers to break out of intended command context and execute malicious commands on the underlying Android system.

### Vulnerability Type and Category
- **Type:** Shell Command Injection
- **Category:** Code Injection / Improper Input Validation

### Difficulty
Medium

## Build instructions
This project uses Android Studio with Java and Material Design Components.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 34 recommended).

Build and deploy the app to an emulator or Android device.

```bash
./gradlew assembleDebug
```
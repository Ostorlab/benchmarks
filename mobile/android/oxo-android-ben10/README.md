# ThemeEngine Android App

## Challenge Details

### Description
This is a demonstration app that showcases a theme engine functionality allowing users to load custom themes. The app demonstrates a realistic use case that could lead to insecure class loading vulnerabilities.

The app consists of several activities simulating a real-world application:
- Main Activity (Home screen with current theme preview)
- Theme Manager Activity (Browse and load themes)
- User Settings Activity (App preferences)
- Theme Store Activity (Browse available themes)
- Profile Activity (User profile management)

### Vulnerability Type and Category
- **Type:** Insecure Dynamic Class Loading
- **Category:** Code Injection / Untrusted Code Execution
- **CWE:** CWE-470 (Use of Externally-Controlled Input to Select Classes or Code)

### Difficulty
Easy

## Build Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK (minimum API 21)
- Gradle 7.0+

### Build Steps
1. Open the project in Android Studio
2. Sync project with Gradle files
3. Build the debug APK:
```bash
cd src/
./gradlew assembleDebug
```
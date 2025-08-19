# oxo-android-ben11 Stored XSS - 

## Challenge Details

### Description

This Android application contains a Stored Cross-Site Scripting (XSS) vulnerability. User input is saved to a local SQLite database and later rendered in a WebView without any sanitization or encoding. Since JavaScript is enabled in the WebView and input is directly injected into HTML, malicious scripts (e.g., <script>alert('XSS')</script>) will execute when the list is viewed, leading to arbitrary JavaScript execution.

### Vulnerability Type and Category
- **Type:** XSS
- **Category:** Stored XSS

### Difficulty
Easy

## Build instructions
This project uses Android Studio with Java and Jetpack Compose.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 31 recommended).

Build and deploy the app to an emulator or Android device.

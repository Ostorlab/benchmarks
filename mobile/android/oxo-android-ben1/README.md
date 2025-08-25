# oxo-android-ben1 Execute JS Open URL redirect - 

## Challenge Details

### Description

This Android app sample demonstrates two common WebView vulnerabilities:

- JavaScript Injection via insecure use of addJavascriptInterface, allowing attacker-controlled JavaScript code to execute native app methods.
- Open Redirect via unvalidated user input URL loading, letting attackers redirect users to malicious sites.

Both vulnerabilities highlight unsafe handling of user-controlled inputs in WebView and lack of proper access controls or sanitization.

### Vulnerability Type and Category
- **Type:** JavaScript Injection
- **Category:** Code Injection
- **Type:** Open Redirect
- **Category:**  Improper Input Validation / Broken Access Control

### Difficulty
Easy

## Build instructions
This project uses Android Studio with Kotlin and Jetpack Compose.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 35 recommended).

Build and deploy the app to an emulator or Android device.

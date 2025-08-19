# oxo-android-ben9 Lack of TLS/SSL certificate validation - 

## Challenge Details

### Description

This Android application contains a lack of SSL certificate validation vulnerability that might lead to man-in-the-middle (MITM) attacks and intercept sensitive data.
The vulnerability is present in the network communication code where the app connects to a remote HTTPS API. Instead of using the system's default certificate validation, the app configures a custom TrustManager that blindly trusts all SSL certificates. Additionally, it disables hostname verification by overriding the HostnameVerifier to always return true. This effectively disables all standard SSL/TLS security checks.

As a result, an attacker can intercept and modify HTTPS traffic using a forged or self-signed certificate without being detected by the application.

### Vulnerability Type and Category
- **Type:** TLS/SSL
- **Category:** Lack of TLS/SSL certificate validation

### Difficulty
Easy

## Build instructions
This project uses Android Studio with Java and Jetpack Compose.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 31 recommended).

Build and deploy the app to an emulator or Android device.

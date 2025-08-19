# oxo-android-ben11 Cleartext communication - 

## Challenge Details

### Description

This Android app demonstrates a Cleartext Communication vulnerability by loading an HTTP website in a WebView (http://www.slackware.com/), exposing network traffic to interception.
Because cleartext traffic is allowed and HTTP is used, the app is vulnerable to Man-in-the-Middle attacks that can lead to data exposure or tampering.

### Vulnerability Type and Category
- **Type:** Cleartext communication
- **Category:** communication using http

### Difficulty
Easy

## Build instructions
This project uses Android Studio with Java and Jetpack Compose.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 31 recommended).

Build and deploy the app to an emulator or Android device.

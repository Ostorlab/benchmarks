# oxo-android-ben22 Android UI Redressing (Tapjacking) Vulnerability - 

## Challenge Details

### Description

This Android app demonstrates a critical security vulnerability known as **UI Redressing** or **Tapjacking**. This attack tricks users into interacting with malicious overlays (e.g., crafted popup windows or toasts) invisible to them, causing taps to be redirected to background app elements without user consent.

In this scenario, malicious apps can use transparent or partially transparent overlays to make users unintentionally tap on sensitive UI controls such as buttons for making purchases, clicking ads, installing other apps, or even wiping all data from the device.

The vulnerability arises because the app’s UI elements (e.g., buttons) are not protected from being tapped when obscured by another window.

### Vulnerability Type and Category
-   **Type:** UI Redressing (Tapjacking)
-   **Category:** Interaction Misuse / UI Spoofing

### Difficulty
Easy

## Build and Test Instructions

### Build
This project uses Android Studio with Java. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd VulnerableLogger/
./gradlew assembleDebug
```
The APK will be located at app/build/outputs/apk/debug/app-debug.apk.

### How to Test

1. Install the application on an Android device or emulator.


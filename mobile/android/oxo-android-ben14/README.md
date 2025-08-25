# oxo-android-ben14 Critical activity is not protected

## Challenge Details

### Description

This Android app is a banking application that provides essential financial services including money transfers, profile management, payment processing, and administrative functions. The app features a realistic multi-screen user flow with proper authentication and a comprehensive dashboard interface.

The application includes:
- Secure login system with credential validation
- Account dashboard displaying balance and transaction history  
- Money transfer functionality between accounts
- User profile management with personal information editing
- Payment confirmation system for transactions
- Administrative panel for user and system management

### Vulnerability Type and Category
- **Type:** Critical Activity is Not Protected / Improper Export of Android Application Components
- **Category:** Platform Security (OWASP Mobile) / Improper Export of Android Application Components (CWE-926)

### Difficulty
Easy

## Build and Test Instructions

### Build
This project uses Android Studio with Java. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd AtlanticBanking/
./gradlew assembleDebug
```
The APK will be located at app/build/outputs/apk/debug/app-debug.apk.

### How to Test

1. Install the application on an Android device or emulator.

2. Test component accessibility using ADB commands:
    ```bash
    # Test money transfer access
    adb shell am start -n com.atlanticbank.mobile/.TransferActivity
    
    # Test administrative functions
    adb shell am start -n com.atlanticbank.mobile/.AdminPanelActivity
    
    # Test profile management
    adb shell am start -n com.atlanticbank.mobile/.ProfileEditActivity
    
    # Test payment processing
    adb shell am start -n com.atlanticbank.mobile/.PaymentConfirmActivity
    ```

3. Verify that activities can be launched without authentication.

### Success Condition

A successful test requires the tool to identify activities that can be accessed directly without proper authentication flow.

**Example of successful findings**:
- Activities marked as `android:exported="true"` in AndroidManifest.xml
- Direct component access bypassing the login screen
- Ability to access sensitive functions without user verification
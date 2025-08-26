# oxo-android-ben30 Task Hijacking Vulnerability

## Challenge Details

### Description

SecureBank is a mobile banking application that provides comprehensive financial services including account management, money transfers, bill payments, and investment tracking. The application features a multi-screen user flow with authentication, dashboard, transaction processing, and customer support functionality.

The application demonstrates a **Task Hijacking** vulnerability where sensitive activities can be intercepted or overlaid by malicious applications due to improper task and activity configuration. The vulnerability allows attackers to potentially capture user credentials, transaction details, or other sensitive information by exploiting Android's task management system.

Key features of the application:
- User authentication with PIN and biometric options
- Account dashboard with balance and transaction history
- Money transfer between accounts and external recipients
- Bill payment functionality with saved payees
- Investment portfolio management
- Customer support chat interface

### Vulnerability Type and Category
- **Type:** Task Hijacking
- **Category:** Platform Security (OWASP Mobile) / Improper Activity State Management (CWE-925)

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

# SecureBank Mobile Application - Ben31

## Overview

SecureBank is a realistic banking mobile application developed for security testing and penetration testing challenges. This application demonstrates the **Implicit Broadcast Receiving** vulnerability while providing a fully functional banking experience with SQLite database integration.

## Vulnerability: Implicit Broadcast Receiving

This application contains an intentional security vulnerability where broadcast receivers are registered to listen for implicit (system-wide) broadcasts without proper security controls. This allows malicious applications to send crafted broadcasts that can trigger unintended behavior.

### Vulnerability Details

The vulnerable component is located in `SystemUpdateReceiver.java`, which registers for the following broadcast actions:
- `com.securebank.UPDATE_BALANCE` - Updates user account balance
- `com.securebank.SYNC_DATA` - Synchronizes user data and session tokens
- `android.intent.action.CONNECTIVITY_CHANGE` - Responds to network connectivity changes

These receivers are declared as `exported="true"` in the AndroidManifest.xml, making them accessible to any application on the device.

### Exploitation

Malicious applications can exploit this vulnerability by sending broadcast intents with malicious data to:
1. **Modify account balances** by sending `UPDATE_BALANCE` broadcasts with crafted balance and account data
2. **Inject malicious user data** through `SYNC_DATA` broadcasts containing session tokens and user profiles
3. **Extract sensitive information** logged during connectivity changes

Example exploitation code:
```java
// Malicious balance update
Intent intent = new Intent("com.securebank.UPDATE_BALANCE");
intent.putExtra("balance", "999999.99");
intent.putExtra("account", "target-account-number");
sendBroadcast(intent);

// Malicious data synchronization
Intent syncIntent = new Intent("com.securebank.SYNC_DATA");
syncIntent.putExtra("user_data", "malicious_profile_data");
syncIntent.putExtra("session_token", "hijacked_token");
sendBroadcast(syncIntent);
```

## Application Features

### Authentication System
- **SQLite Database Integration**: User credentials stored in local SQLite database
- **User Registration**: New users can create accounts with automatic account number generation
- **Login Validation**: Secure authentication against database records

### Banking Functionality
- **Real Money Transfers**: Transfer money between actual user accounts in the database
- **Transaction History**: View real transaction records stored in the database
- **Account Management**: View and update account details and personal information
- **Balance Management**: Real-time balance updates with database persistence

### Pre-loaded Test Accounts

The application comes with several pre-configured test accounts:

| Username | Password | Account Number | Initial Balance |
|----------|----------|----------------|-----------------|
| demo | password | 4532-1234-5678-9012 | $15,432.50 |
| admin | admin123 | 4532-9876-5432-1098 | $25,000.00 |
| user | user123 | 4532-5555-7777-3333 | $8,750.25 |
| alice | alice123 | 4532-1111-2222-3333 | $12,500.75 |
| bob | bob123 | 4532-3333-4444-5555 | $7,890.50 |

### Application Activities

1. **MainActivity**: Login screen with registration link
2. **RegisterActivity**: User registration with validation
3. **DashboardActivity**: Main banking dashboard
4. **TransferActivity**: Money transfer between accounts
5. **AccountActivity**: Account information display
6. **TransactionHistoryActivity**: Real transaction history
7. **ProfileActivity**: User profile management
8. **SettingsActivity**: Application settings and logout

### Build instructions


## Build Instructions

### Build
This project uses Android Studio with Java. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd src/
./gradlew assembleDebug
```
The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

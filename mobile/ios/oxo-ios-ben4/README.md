# oxo-ios-ben4 VaultApp - iOS PIN Brute Force

## Challenge Details

### Description
A secure vault application for iOS, "VaultApp," protects sensitive user data with a 4-digit PIN. The application's authentication logic contains a critical flaw that allows an attacker to guess the PIN an unlimited number of times without being locked out. Can your tool identify the missing enforcement mechanism?

### Vulnerability Type and Category
- **Type:** Infinite Brute Force Attempts
- **Category:** Broken Authentication

### Weakness (CWE)
- **CWE-307:** Improper Restriction of Excessive Authentication Attempts

### Platform
iOS (Swift/SwiftUI)

### Difficulty
Easy

## Vulnerability Overview
The application's `AuthStore` class tracks failed authentication attempts via a `remainingAttempts` counter but fails to enforce any lockout policy when this counter is exhausted. This design flaw allows an attacker to perform an unlimited brute-force attack against the 4-digit PIN code until the correct value (`2002`) is discovered.

## Exploitation Vector
1.  **Authentication Bypass:** An attacker with physical access to the device can launch the app and simply guess PIN values repeatedly. The application will accept an infinite number of incorrect guesses, making it trivial to brute-force the weak 4-digit code.

## Build Instructions
This is a standard Xcode project. Open `VaultApp.xcodeproj` and build for the iOS Simulator or a physical device.

1.  **Run the app** in the simulator (`Cmd + R`).
2.  **Trigger the vulnerability** by entering incorrect PIN values (e.g., `0000`, `1111`, `2222`...) at the login prompt. Observe that the attempt counter decrements but access is never permanently denied.
3.  **Enter the correct PIN** (`2002`) to confirm successful exploitation and gain access to the vault.

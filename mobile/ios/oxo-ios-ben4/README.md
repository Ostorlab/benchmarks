# oxo-ios-ben4 VaultApp - iOS PIN Brute Force

## Challenge Details

### Description
A secure vault application for iOS, "VaultApp," protects sensitive user data with a 4-digit PIN. The application features a complete password manager with the ability to store, edit, and manage secrets. However, the authentication logic contains a critical flaw that allows an attacker to guess the PIN an unlimited number of times without being locked out. Can your tool identify the missing enforcement mechanism?

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
The application's `AuthStore` class lacks any lockout mechanism for failed authentication attempts. While the app displays a "Wrong password" message for incorrect PINs, it imposes no restrictions on the number of attempts an attacker can make. This design flaw allows an attacker to perform an unlimited brute-force attack against the 4-digit PIN code until the correct value (`2002`) is discovered.

**Key Vulnerability Points:**
- No attempt counter enforcement
- No time-based lockouts
- No progressive delays between attempts
- No account lockout after multiple failures

## Exploitation Vector
1. **Authentication Bypass:** An attacker with physical access to the device can launch the app and systematically guess PIN values.
2. **Brute Force Attack:** The attacker can try all possible 4-digit combinations (0000-9999) without any restrictions.
3. **No Rate Limiting:** Each incorrect attempt only shows a brief "Wrong password" message before allowing the next attempt.
4. **Successful Access:** Once the correct PIN (`2002`) is entered, the attacker gains full access to all stored secrets.

## Build Instructions
This is a standard Xcode project built with SwiftUI for iOS 15.0+.

### Prerequisites
- Xcode 13.0 or later
- iOS 15.0+ Simulator or physical device
- macOS 12.0+ for development

### Building and Running
1. Open `VaultApp.xcodeproj` in Xcode
2. Select your target device (Simulator or physical device)
3. Build and run the project (`Cmd + R`)
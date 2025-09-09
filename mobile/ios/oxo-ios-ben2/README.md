# oxo-ios-ben2: iOS Deep Link Information Disclosure

## Vulnerability Overview

iOS Deep Link Information Disclosure occurs when an iOS app's custom URL scheme handler processes deep link parameters without proper authorization validation. This vulnerability allows any installed app or malicious website to trigger deep links and access unauthorized user data, bypassing the app's normal authentication flow.

## Attack Vector: URL Scheme Parameter Injection

**Brief Explanation**: SecureBank registers the `securebank://` URL scheme and processes profile requests with user_id parameters directly without validating if the current authenticated user should access that data.

**Key Characteristics:**
- Professional banking app appearance with no vulnerability hints  
- Custom URL scheme `securebank://profile?user_id=<ID>` accepts arbitrary user IDs
- SceneDelegate processes deep links without authorization checks
- Direct profile access bypassing login requirements
- Sensitive banking data exposure (SSN, account numbers, balances)

**Vulnerable Code Pattern:**
```swift
// SceneDelegate.swift - Deep link handler
func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
    guard let url = URLContexts.first?.url else { return }
    
    if url.scheme == "securebank" && url.host == "profile" {
        let userID = extractParameter(url: url, param: "user_id")
        // VULNERABLE: No authorization check!
        navigateToProfile(userId: userID)
    }
}
```

## Challenge Details

### Description

This iOS app is a banking application that provides essential financial services including account management, profile viewing, transaction history, and settings configuration. The app features a realistic multi-screen user flow with proper authentication and a comprehensive dashboard interface.

The application includes:
- Secure login system with PIN validation
- Account dashboard displaying balance and transaction history
- User profile management with personal information viewing  
- Settings panel with security features and preferences
- Multi-user data system with different account types

### Vulnerability Type and Category
- **Type:** iOS Deep Link Information Disclosure / Improper Input Validation
- **Category:** Platform Security (OWASP Mobile M10 - Extraneous Functionality)

### Difficulty
Easy

## Build and Test Instructions

### Build
This project uses Xcode with Swift. See [building.md](src/SecureBank/building.md) for complete build instructions.

### Test
```bash
# Install on iOS Simulator
xcrun simctl install booted ipas/oxo-ios-ben2.ipa

# Trigger vulnerability - access other users without authorization
xcrun simctl openurl booted "securebank://profile?user_id=456"  # Sarah Johnson
xcrun simctl openurl booted "securebank://profile?user_id=789"  # Mike Wilson  
xcrun simctl openurl booted "securebank://profile?user_id=999"  # Admin Account
```

## Exploitation

**Attack Vectors:**
- Malicious apps: `UIApplication.shared.open(URL(string: "securebank://profile?user_id=999")!)`
- Phishing websites: `<a href="securebank://profile?user_id=456">View Account</a>`
- Social engineering via SMS/social media deep links

**Expected Result:** App opens directly to target user's profile with full access to sensitive data (SSN, account numbers, balances) without authentication prompts.

## Technical Details

**Test Users:**
- 123: John Smith (Standard) - $12,345.67
- 456: Sarah Johnson (Premium) - $8,976.32  
- 789: Mike Wilson (Business) - $25,678.90
- 999: Admin User (Administrator) - $999,999.99

**Vulnerable Files:**
- `SceneDelegate.swift` - Deep link handler without authorization
- `Info.plist` - URL scheme registration
# oxo-android-ben56: Multiple Passcode Bypass Vulnerabilities

## Vulnerability Overview

Professional password manager app "SecureVault" with multiple authentication bypass vulnerabilities. Contains sensitive data including passwords, personal information, and secure notes protected by a 4-digit PIN that can be bypassed through various methods.

## Attack Vector: Multi-Pattern Authentication Bypass

**Brief Explanation**: Four distinct vulnerability patterns allow bypassing PIN authentication through intent parameters, UI manipulation, and activity lifecycle issues.

**Key Characteristics:**
- Intent parameter processing before authentication check
- Static variable persistence across app lifecycle  
- Missing authentication check in activity onResume()
- Rapid back button bypass for accessibility

**Vulnerable Code Pattern:**
```java
// Intent parameter bypass
if (intent.getBooleanExtra("debug_mode", false)) {
    setupToolbar();
    loadSensitiveData();
    return; // Bypasses authentication
}

// Back button accessibility bypass  
if (backPressCount >= 5) {
    // Emergency access for accessibility - TODO: Remove before production
    prefs.edit().putBoolean(KEY_AUTHENTICATED, true).apply();
}
```

## Testing

### Manual Testing
1. **Back Button Bypass**: Press back button 5 times rapidly on PIN screen
2. **Normal PIN**: Enter `1234` to access vault normally
3. **Lifecycle Bypass**: Enter PIN, background app, clear from recents, reopen

### ADB Testing
```bash
# Intent parameter bypasses
adb shell am start -n com.securityplus.vault/.VaultActivity --ez debug_mode true
adb shell am start -n com.securityplus.vault/.VaultActivity --es user_type admin

# Verification (should fail)
adb shell am start -n com.securityplus.vault/.VaultActivity
```

**Difficulty**: Easy

## Impact Assessment

- **Confidentiality**: High - Access to passwords and personal data
- **Integrity**: Medium - Can modify sensitive account settings  
- **Availability**: Low - Does not prevent legitimate access
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M10 - Extraneous Functionality
- **CWE**: CWE-287 (Improper Authentication), CWE-489 (Active Debug Code)

## Build Instructions

Android Studio project with Java and Gradle Kotlin DSL.

```bash
cd oxo-android-ben56/src/
./gradlew assembleDebug
```

### Success Condition

Security tools should identify VaultActivity accessible without proper authentication through intent parameters, UI manipulation, and activity lifecycle issues.
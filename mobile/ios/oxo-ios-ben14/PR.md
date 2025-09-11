# oxo-ios-ben14: iOS Medical Records File Access Vulnerabilities

## Vulnerability Overview
iOS Medical Records app stores sensitive data in **unencrypted files** accessible via filesystem. Authentication tokens, medical records, and credentials exposed in plain text.

## Vulnerable Code
```swift
// VULNERABLE: SessionManager.swift - Line 48 (Session Storage)
private func saveSession(token: String, userId: String, doctorId: String) {
    let sessionFile = documentsPath.appendingPathComponent("user_session.plist")
    let sessionData: [String: Any] = [
        "auth_token": token,
        "user_id": userId,
        "device_id": UIDevice.current.identifierForVendor?.uuidString ?? "unknown"
    ]
    try? (sessionData as NSDictionary).write(to: sessionFile)
}

// VULNERABLE: SessionManager.swift - Line 65 (Credential Storage)
private func saveCredentials(username: String, password: String, pin: String) {
    UserDefaults.standard.set(username, forKey: "saved_username")
    UserDefaults.standard.set(password, forKey: "saved_password")
    UserDefaults.standard.set(pin, forKey: "medical_pin")
}

// VULNERABLE: SessionManager.swift - Line 82 (Medical Records)
private func cacheMedicalRecords() {
    let medicalData: [String: Any] = [
        "ssn": "123-45-6789",
        "insurance_id": "INS-789456123",
        "diagnosis": "Chronic hypertension with complications"
    ]
    try? (medicalData as NSDictionary).write(to: recordFile)
}
```

## Testing
1. Launch app, login with `patient123`/`medical2024`
2. Find app data directory and extract sensitive files

```bash
# Get iPhone 15 Pro simulator UUID (as an example)
DEVICE_UUID=$(xcrun simctl list devices | grep "iPhone 15 Pro" | grep Booted | sed 's/.*(\(.*\)).*/\1/')

# Find Medical Records app data directory
find ~/Library/Developer/CoreSimulator/Devices/$DEVICE_UUID/data/Containers/Data/Application -name "*" -type d | grep -i medical

# Copy the path from above output and set as APP_DATA_DIR
APP_DATA_DIR="/Users/username/Library/Developer/CoreSimulator/Devices/$DEVICE_UUID/data/Containers/Data/Application/[APP_ID]"

# Extract session tokens
cat "$APP_DATA_DIR/Documents/user_session.plist"

# Extract medical records  
cat "$APP_DATA_DIR/Documents/medical_cache/"*.plist

# Extract stored credentials
plutil -p "$APP_DATA_DIR/Library/Preferences/co.ostorlab.medicalrecords.MedicalRecords.plist"
```

## Vulnerabilities

### Vulnerability 1: Insecure Session Storage
*[Screenshot showing auth tokens in plain text]*

### Vulnerability 2: Unencrypted Medical Records
*[Screenshot showing SSN, diagnosis, lab results]*

### Vulnerability 3: Prescription Data Exposure  
*[Screenshot showing medications, pharmacy info]*

### Vulnerability 4: Credential Storage
*[Screenshot showing username/password in UserDefaults]*

## Impact
- SSN & insurance ID theft
- Medical privacy breach  
- Session hijacking
- Password reuse attacks

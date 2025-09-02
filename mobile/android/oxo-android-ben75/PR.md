# feat: Add oxo-android-ben75 - Grant URI Permission Escalation via Proxy Activity

## Overview
This PR introduces **oxo-android-ben75**, a DocumentManager Pro Android application that demonstrates the **Grant URI Permission Escalation vulnerability**. The app implements a professional document management system with an exported proxy activity that processes embedded workflow intents containing URI permission grant flags, allowing unauthorized access to sensitive business documents stored in a non-exported content provider.

## Vulnerability Description
**Grant URI Permission Escalation via Proxy Activity** occurs when Android applications contain exported proxy activities that accept embedded Intent objects containing URI permission grant flags and pass them to dangerous methods without proper validation. Attackers can abuse this pattern to bypass Android's content provider access restrictions and gain persistent access to sensitive data by forcing the vulnerable app to grant URI permissions through the proxy activity mechanism.

## Vulnerable Code Pattern:
```kotlin
// VULNERABLE: Exported proxy activity that processes workflow intents with URI permissions
class IntentProcessorActivity : Activity() {
    private fun handleWorkflowIntent(intentJson: String) {
        try {
            val jsonObject = JSONObject(intentJson)
            val workflowIntent = Intent().apply {
                action = jsonObject.optString("action", Intent.ACTION_VIEW)
                data = Uri.parse(jsonObject.getString("data"))
                
                // CRITICAL: URI permission flags processed without validation
                if (jsonObject.has("flags")) {
                    val flagsArray = jsonObject.getJSONArray("flags")
                    for (i in 0 until flagsArray.length()) {
                        when (flagsArray.getString(i)) {
                            "FLAG_GRANT_READ_URI_PERMISSION" -> 
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            "FLAG_GRANT_WRITE_URI_PERMISSION" -> 
                                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            "FLAG_GRANT_PREFIX_URI_PERMISSION" -> 
                                addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
                        }
                    }
                }
            }
            // DANGEROUS: Starting workflow intent with URI permission grants
            startActivity(workflowIntent)
        } catch (e: Exception) { }
    }
}

// VULNERABLE: Non-exported content provider with grant permissions enabled
<provider
    android:name="com.documentmanager.providers.DocumentContentProvider"
    android:authorities="com.documentmanager.secure"
    android:exported="false"
    android:grantUriPermissions="true" />
```

## Attack Vector:
```bash
# Malicious workflow intent with URI permission grant flags
adb shell am start -n com.documentmanager/.IntentProcessorActivity 
  --es workflow_intent '{"action":"android.intent.action.VIEW","data":"content://com.documentmanager.secure/documents","flags":["FLAG_GRANT_READ_URI_PERMISSION","FLAG_GRANT_WRITE_URI_PERMISSION","FLAG_GRANT_PREFIX_URI_PERMISSION"]}'
```



## Application Details
- **Package Name**: `com.documentmanager`
- **Main Activity**: DocumentManager Pro dashboard with professional UI
- **Vulnerable Component**: `IntentProcessorActivity` (exported proxy activity)
- **Protected Resource**: `DocumentContentProvider` (non-exported with grantUriPermissions="true")
- **Target SDK**: 34 (Android 14)

## Business Context
DocumentManager Pro presents as a legitimate enterprise document management application featuring:
- **Professional Document Storage**: Comprehensive content provider with sensitive business data
- **Secure Content Provider**: Non-exported provider protecting confidential documents
- **Enterprise UI**: Professional interface masking the underlying vulnerability
- **Realistic Sensitive Data**: Financial reports, employee records, client contracts

## Sensitive Data Exposed
The vulnerable content provider contains realistic business documents and authentication data:
- `Financial_Report_Q4_2024.pdf` - Revenue $4.25M, profit data, bank account 789-456-123-001
- `Employee_Database.xlsx` - Employee SSNs, salaries, security clearance levels
- `Client_Contracts.docx` - Client banking details, emergency contacts, legal agreements
- Authentication data - Password hashes, session tokens, 2FA secrets

## Technical Implementation
- **JSON Workflow Intent**: Exported activity processes embedded JSON intents
- **URI Permission Escalation**: Permission grant flags forwarded without validation
- **Content Provider Bypass**: Non-exported provider relies on export restrictions
- **Persistent Access**: URI permissions can be made persistent for ongoing data access

## Files Added
```
mobile/android/oxo-android-ben75/
├── README.md                                   # Professional application documentation
├── PR.md                                       # This PR description
├── exploit/
│   └── uri_exploit.py                         # URI permission escalation test script
├── apks/oxo-android-ben75.apk                 # Built application package
└── src/
    ├── app/src/main/AndroidManifest.xml       # App configuration with vulnerable provider
    ├── app/src/main/java/com/documentmanager/
    │   ├── MainActivity.kt                     # Professional dashboard interface
    │   ├── DocumentsActivity.kt               # Document management interface
    │   ├── ShareActivity.kt                   # Document sharing functionality
    │   ├── SettingsActivity.kt                # Application settings
    │   ├── IntentProcessorActivity.kt         # VULNERABLE: Intent proxy with URI grants
    │   ├── providers/
    │   │   └── DocumentContentProvider.kt     # VULNERABLE: Non-exported sensitive provider
    │   └── data/
    │       └── DocumentDataManager.kt         # Business logic and data handling
    ├── app/src/main/res/                       # Professional UI resources
    ├── app/build.gradle.kts                   # Android build configuration
    └── [standard Android project structure]
```

## Verification Commands
```bash
# Build and install the application
cd src/ && ./gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Or install from pre-built APK
adb install apks/oxo-android-ben75.apk

# Launch application to initialize content provider
adb shell am start -n com.documentmanager/.MainActivity

# Verify direct access blocked (should fail with SecurityException)
adb shell content query --uri content://com.documentmanager.secure/documents

# Test URI permission escalation vulnerability
python3 exploit/uri_exploit.py

# Check sensitive document creation
adb shell "run-as com.documentmanager ls -la /data/data/com.documentmanager/files/secure/"

# Monitor URI permission grants in logs
adb logcat | grep -E 'DocumentProcessor|DocumentProvider'
```

## Impact Assessment
- **Confidentiality**: HIGH - Access to sensitive business documents and authentication data
- **Integrity**: MEDIUM - Potential content provider data modification capabilities  
- **Availability**: LOW - No significant availability impact
- **Attack Complexity**: LOW - Simple JSON intent crafting required
- **Detection Difficulty**: HIGH - Professional application appearance masks vulnerability

## Notes
This implementation demonstrates a sophisticated Android privilege escalation vulnerability that exploits the interaction between Android's intent system and URI permission grants. The vulnerability creates a complete bypass for content provider export restrictions through permission escalation.

The application maintains a professional appearance as a document management system, making the vulnerability difficult to detect through casual inspection. This represents a real-world attack pattern that could be exploited in enterprise environments where document management applications handle sensitive business data, authentication credentials, and confidential corporate information.

The vulnerability allows persistent access to protected content providers, enabling ongoing data exfiltration and potentially complete compromise of sensitive business information stored within the application's secure content provider.

This is complementary to oxo-android-ben74 which demonstrates direct proxy activity exploitation, together covering both major attack vectors described in the Oversecured research on Android proxy activity vulnerabilities.

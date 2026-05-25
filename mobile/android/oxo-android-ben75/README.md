# oxo-android-ben75: Location Trilateration via Precise Distance Broadcasts

## Application: HeartConnect
**Package Name:** `co.ostorlab.ben75`  
**Vulnerability Type:** Privacy Violation / Location Exposure via Trilateration  
**Target SDK:** 35 (Android 15)

## Overview

HeartConnect is a minimal dating application that demonstrates a critical privacy vulnerability: **location trilateration through high-precision distance broadcasts**. The app calculates exact Haversine distances between the current user and nearby matches, then broadcasts this data via **implicit broadcast intents** that any malicious application on the device can intercept.

While the app never directly shares match coordinates, the **precise distances** (to 2 decimal places in meters) combined with the **reference point** (current user location) enable an attacker to solve for exact match locations using trilateration.

## Vulnerability Description

### Location Trilateration via Broadcast Intents

The application sends an implicit broadcast intent with action `co.ostorlab.ben75.NEARBY_USERS_UPDATE` containing:
- `reference_lat` / `reference_lon`: The current user's GPS coordinates
- `timestamp_ms`: Unix timestamp of the broadcast
- For each match: `user_N_id`, `user_N_name`, `user_N_distance_m` (exact distance in meters)

**Vulnerable Code Pattern:**
```kotlin
val intent = Intent("co.ostorlab.ben75.NEARBY_USERS_UPDATE").apply {
    putExtra("reference_lat", currentLat)    // e.g., 40.7580
    putExtra("reference_lon", currentLon)    // e.g., -73.9855
    putExtra("timestamp_ms", System.currentTimeMillis())

    matches.forEachIndexed { index, match ->
        val distance = calculateHaversineDistance(match)
        putExtra("user_${index + 1}_id", match.id)
        putExtra("user_${index + 1}_name", match.name)
        putExtra("user_${index + 1}_distance_m", distance) // e.g., 1245.67
    }
}
// VULNERABLE: Implicit broadcast — any app can receive
sendBroadcast(intent)
```

### Why Trilateration Works

Trilateration is the process of determining absolute or relative locations of points by measurement of distances, using the geometry of circles, spheres or triangles.

Given:
- 3 known reference points: `(lat₁, lon₁)`, `(lat₂, lon₂)`, `(lat₃, lon₃)`
- 3 measured distances to the same target: `d₁`, `d₂`, `d₃`

The attacker's device is the reference point. By physically moving to 3 different locations and recording the broadcasted distance to the same victim, the attacker can construct 3 circles on a map:
- Circle 1: center = `(lat₁, lon₁)`, radius = `d₁`
- Circle 2: center = `(lat₂, lon₂)`, radius = `d₂`
- Circle 3: center = `(lat₃, lon₃)`, radius = `d₃`

The intersection of these three circles is the **exact location** of the victim.

**Precision Impact:**
- Distance rounded to **100m**: Trilateration becomes highly inaccurate (city-block level at best)
- Distance precise to **2 decimal meters** (as in this app): Trilateration pinpoints location to within **a few meters**

### Temporal Tracking

The `timestamp_ms` field allows an attacker to:
- Correlate distance changes over time
- Track victim movement patterns
- Build a movement history database
- Identify home, work, and frequent locations

### Logcat Leakage

The application also logs all precise distances to the system logcat with a predictable tag (`HeartConnect`), making the data accessible to any app with `READ_LOGS` permission or ADB access.

## Application Features

### Core Functionality
- **Nearby Matches List**: Displays 5 mock dating profiles with exact distances
- **Refresh Button**: Recalculates distances and re-broadcasts the data
- **Auto-broadcast**: Sends the vulnerable broadcast automatically on app launch

### Mock Data
Current user location: **Times Square, NYC** (40.7580, -73.9855)

| ID | Name | Age | Distance |
|----|------|-----|----------|
| user_001 | Sarah | 26 | ~1,020 m |
| user_002 | Mike | 29 | ~380 m |
| user_003 | Jessica | 24 | ~850 m |
| user_004 | David | 31 | ~860 m |
| user_005 | Emma | 27 | ~2,030 m |

## Building and Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 35
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
The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

## Verification Commands

### Check Application Installation
```bash
adb shell pm list packages | grep co.ostorlab.ben75
```

### Launch Application
```bash
adb shell am start -n co.ostorlab.ben75/.MainActivity
```

### 1. Intercept the Broadcast Intent
```bash
# Monitor logcat for broadcast data
adb logcat -s HeartConnect:D

# Expected output shows precise distances:
# HeartConnect: Reference: lat=40.758, lon=-73.9855, time=1716374400000
# HeartConnect: Match user_001 (Sarah): 1019.87m away
# HeartConnect: Match user_002 (Mike): 379.52m away
# HeartConnect: Match user_003 (Jessica): 847.23m away
```

### 2. Register a Malicious Broadcast Receiver
```bash
# Create a simple test receiver app or use adb shell
# The malicious app registers:
# <receiver android:name=".StealLocationReceiver">
#     <intent-filter>
#         <action android:name="co.ostorlab.ben75.NEARBY_USERS_UPDATE" />
#     </intent-filter>
# </receiver>
```

### 3. Simulate the Trilateration Attack
```bash
# Step 1: Attacker at Location A (e.g., Times Square)
# Broadcast shows: user_001 is 1019.87m away
# Attacker notes: d₁ = 1019.87, reference = (40.7580, -73.9855)

# Step 2: Attacker walks to Location B (e.g., 500m east)
# Broadcast shows: user_001 is 721.45m away
# Attacker notes: d₂ = 721.45, reference = (40.7580, -73.9800)

# Step 3: Attacker walks to Location C (e.g., 500m north)
# Broadcast shows: user_001 is 1380.22m away
# Attacker notes: d₃ = 1380.22, reference = (40.7625, -73.9855)

# With 3 circles, attacker solves for Sarah's exact coordinates:
# Result: approximately (40.7489, -73.9680)
```

### 4. Verify Predictable Broadcast Action
```bash
# Search for the broadcast action string in the APK
adb shell "run-as co.ostorlab.ben75 strings /data/app/~~*/co.ostorlab.ben75*/base.apk | grep NEARBY_USERS_UPDATE"

# Expected: co.ostorlab.ben75.NEARBY_USERS_UPDATE
```

### 5. Verify High-Precision Distance in UI
```bash
# Dump UI hierarchy to confirm precise distance display
adb shell uiautomator dump /sdcard/window_dump.xml
adb pull /sdcard/window_dump.xml
# Search for "1019.87 m away" in the XML
```

## Security Analysis

### Vulnerable Code Patterns

**Broadcast Layer — Implicit Broadcast with Sensitive Data:**
```kotlin
// MainActivity.kt
val intent = Intent("co.ostorlab.ben75.NEARBY_USERS_UPDATE").apply {
    putExtra("reference_lat", currentLat)
    putExtra("reference_lon", currentLon)
    putExtra("timestamp_ms", System.currentTimeMillis())
    putExtra("user_1_distance_m", 1019.87) // precise to 2 decimals
}
// No permission required, no receiver restriction
sendBroadcast(intent)
```

**Distance Calculation — High Precision:**
```kotlin
// UserRepository.kt
fun calculateDistance(match: UserProfile): Double {
    val r = 6371000.0 // Earth radius in meters
    val a = sin(deltaLat / 2).pow(2) +
            cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distanceMeters = r * c
    // VULNERABLE: 2-decimal precision enables trilateration
    return round(distanceMeters * 100.0) / 100.0
}
```

**Logging Layer — Distance Leakage to Logcat:**
```kotlin
Log.d("HeartConnect", "Match ${match.id} (${match.name}): ${dist}m away")
```

### Attack Scenarios

1. **Static Trilateration**: Attacker collects 3 distance readings from different physical locations and solves for the victim's exact home or work address.

2. **Temporal Movement Tracking**: By collecting broadcasts over hours or days, attacker builds a movement profile showing when the victim is home, at work, at the gym, etc.

3. **Stalking / Harassment**: Once exact location is determined, attacker can show up at the victim's location in person.

4. **Correlated Identity**: Combining location data with profile information (name, age, bio) enables real-world identification and doxxing.

### Impact Assessment
- **Confidentiality**: Critical — Exact physical location of users is exposed
- **Integrity**: Low — Does not directly impact data integrity
- **Availability**: Low — Does not directly impact availability
- **OWASP Mobile Top 10**: M1 - Improper Platform Usage, M2 - Insecure Data Storage
- **CWE**: CWE-359 (Exposure of Private Personal Information to an Unauthorized Actor), CWE-200 (Information Exposure)
- **Real-world Precedent**: Grindr (2018), Tinder (2014), Bumble — all patched similar vulnerabilities after public disclosure

## Remediation

To fix this privacy vulnerability:
1. **Round distances**: Round to ~100m or use fuzzy distance bands ("< 1 km", "1-5 km", "5+ km")
2. **Use explicit broadcasts**: Send broadcasts only to trusted components with `setPackage()`
3. **Require permissions**: Define a custom permission for receiving location broadcasts
4. **Remove log statements**: Never log precise distances to system logs
5. **Server-side calculation**: Calculate distances on the server and return only coarse-grained results
6. **Opt-in consent**: Allow users to disable distance display entirely

## Notes

This benchmark demonstrates that even "indirect" location data (distance only, no coordinates) can completely compromise user privacy when precision is too high. The vulnerability is subtle because the app never explicitly shares GPS coordinates — yet trilateration makes them recoverable. This is a classic example of how privacy vulnerabilities often arise from the **combination** of seemingly harmless data points rather than a single obvious leak.

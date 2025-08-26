# oxo-android-ben25 Unsafe Object Deserialization Vulnerability

## Challenge Details

### Description

This Android app demonstrates an Unsafe Object Deserialization vulnerability in the DataProcessorActivity. The application accepts and processes serialized objects from Intent extras without proper validation, making it vulnerable to deserialization attacks that can lead to remote code execution.

The vulnerability occurs when the app deserializes untrusted data containing malicious objects, particularly the NativeDataHandler class which can trigger native code execution through JNI.

### Vulnerability Type and Category
- **Type:** Unsafe Object Deserialization
- **Category:** Remote Code Execution via deserialization attacks

### Difficulty
Easy to Medium

## Technical Details

### Vulnerable Code Location
- **File:** `DataProcessorActivity.java:14-20`
- **Pattern:** Deserializing untrusted Intent extras without validation

### Vulnerability Analysis

The DataProcessorActivity contains the following vulnerable code:
```java
if (getIntent().hasExtra("data")) {
    Serializable s = getIntent().getSerializableExtra("data");
    Log.d(TAG, "Processing data object: " + String.valueOf(s));

    if (s instanceof NativeDataHandler) {
        ((NativeDataHandler) s).invokeNativeFree();
    }
}
```

### Attack Vector

1. **Malicious Serialized Object Creation:** An attacker can craft a malicious serialized NativeDataHandler object
2. **Intent Delivery:** The malicious object is sent via Intent extras to the exported DataProcessorActivity
3. **Unsafe Deserialization:** The app deserializes the untrusted data without validation
4. **Native Code Execution:** The deserialized object triggers native JNI code execution through `invokeNativeFree()`
5. **Memory Corruption:** The native code performs unsafe memory operations that can be exploited

### Impact
- Remote code execution through malicious deserialized objects
- Memory corruption in native code layer
- Potential device compromise through JNI exploitation

## Build Instructions
This project uses Android Studio with Java and native C++ components.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 31 recommended).

Build and deploy the app to an emulator or Android device.

## Exploitation
The vulnerability can be exploited by sending crafted serialized objects to the exported DataProcessorActivity, potentially leading to code execution through the native JNI interface.
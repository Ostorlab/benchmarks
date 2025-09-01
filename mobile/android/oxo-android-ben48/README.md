# oxo-android-ben48 Unsafe Deserialization Vulnerability

## Challenge Details

### Description

This Android app demonstrates a critical security vulnerability related to unsafe deserialization of untrusted data. The app implements a custom `DataParcelable` class that uses Google's Gson library to deserialize arbitrary objects from Parcel data without proper validation or restriction.

The vulnerability manifests in the following way:

- The `DataParcelable` class accepts arbitrary class names and JSON data through Android's Parcel mechanism
- During deserialization, it uses `Class.forName()` to dynamically load classes based on untrusted input
- Gson then deserializes the JSON data into instances of these dynamically loaded classes
- This allows attackers to instantiate arbitrary classes and potentially execute malicious code

This vulnerability highlights the dangers of deserializing untrusted data without proper validation and class whitelisting.

### Vulnerability Type and Category
- **Type:** Unsafe Deserialization / Remote Code Execution
- **Category:** Insecure Deserialization (OWASP) / Deserialization of Untrusted Data (CWE-502)

### Difficulty
Medium

## Build and Test Instructions

### Build
This project uses Android Studio with Java. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd src/
./gradlew assembleDebug
```
The APK will be located at app/build/outputs/apk/debug/app-debug.apk.

### How to Test

1. Install the application on an Android device or emulator.

2. The vulnerability exists in the `DataParcelable` class at `app/src/main/java/com/example/myapplication3/DataParcelable.java:32-33`

3. To trigger the vulnerability, an attacker would need to:
   - Create a malicious Parcel containing a dangerous class name and corresponding JSON data
   - Pass this Parcel to the app through Inter-Process Communication (IPC) or other Android mechanisms
   - The `createFromParcel` method will blindly instantiate the specified class

### Success Condition

A successful test requires the tool to identify the unsafe deserialization pattern in the `DataParcelable.createFromParcel()` method.

**Example of Successful Detection**:
```java
private DataParcelable(Parcel parcel) {
    try {
        Class clazz = Class.forName(parcel.readString()); // Unsafe: arbitrary class loading
        data = GSON.fromJson(parcel.readString(), clazz);  // Unsafe: deserialization without validation
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
}
```

The tool should flag this as allowing arbitrary class instantiation and unsafe deserialization of untrusted data.
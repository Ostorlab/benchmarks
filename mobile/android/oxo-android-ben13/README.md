# oxo-android-ben13 Unprotected AIDL Service allows external CRUD access to Memo

## Challenge Details

### Description

This Android app sample demonstrates a **common AIDL misconfiguration vulnerability**:

- The app exposes an AIDL-based service without proper permissions or restrictions.
- Any other app with matching AIDL and data class definitions can **bind to the service** and perform **Create, Read, Update, and Delete (CRUD)** operations on the `Memo` data.

This vulnerability highlights **unsafe exposure of inter-process communication (IPC)** via AIDL.

### Vulnerability Type and Category
- **Type:** IPC Misconfiguration / AIDL Exposure  
- **Category:** Unprotected Service

### Difficulty
Medium

### How to Exploit

1. Create a second app with:
   - The **same AIDL interface** (`IMemoService.aidl`)
   - The same **`Memo` data class**
2. Add the following to the second app’s `AndroidManifest.xml` to declare intent query:
   ```xml
   <queries>
       <package android:name="com.ostorlab.memo"/>
   </queries>```
3. Bind to the exposed service using:
    ```kotlin
    val intentMemo = Intent("com.ostorlab.memo.IMemoService")
    intentMemo.setPackage("com.ostorlab.memo")
    val didBindMemo = bindService(intentMemo, memoServiceConnection, BIND_AUTO_CREATE)```
4. Once bound, the malicious app can perform unrestricted CRUD operations on Memos.


## Build Instructions

This project uses **Android Studio**, written in **Kotlin** 2.0.21

### Requirements
- **compileSdkVersion**: 36 or higher

### Steps

1. Open the project in **Android Studio**.
2. Connect a physical device or launch an emulator with API 36 or above.
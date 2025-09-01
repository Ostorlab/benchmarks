# oxo-android-ben17 SQL Injection in FileContentProvider - 

## Challenge Details

### Description

This Android app demonstrates a critical security vulnerability in its exported **FileContentProvider**, which improperly handles untrusted input in SQL queries. The provider methods allow apps installed on the same device to directly interact with internal databases, leading to **SQL Injection** risks.

The vulnerability stems from unsafe concatenation of user-controlled parameters (`where` clauses) into raw SQL statements without proper sanitization. Malicious applications can exploit this to exfiltrate, manipulate, or delete sensitive application data.

**Vulnerability Flows:**

-   **Exported Content Provider**: The `FileContentProvider` is declared `android:exported="true"` in the manifest, allowing any external application to query it.
-   **Unsafe `delete` Method**: When invoked, the method directly appends user-supplied selection arguments (`where`, `uri.pathSegments`) into database queries.
-   **Affected Tables**: `FILES`, `CAPABILITIES`, `UPLOADS`, `CAMERA_UPLOADS_SYNC`, `USER_QUOTAS`, among others.
-   **No Input Validation**: User input is injected into SQL query strings without sanitization (`... " AND ($where)"`), enabling attackers to bypass logic or access unrelated records.

This vulnerability highlights **insecure use of Content Providers**, one of the most common Android IPC pitfalls.

### Vulnerability Type and Category
-   **Type:** SQL Injection via Content Provider
-   **Category:** Insecure Data Storage / Insecure Inter-Process Communication (OWASP Mobile)
-   **CWE:** [CWE-89: Improper Neutralization of Special Elements used in an SQL Command ('SQL Injection')](https://cwe.mitre.org/data/definitions/89.html)

### Difficulty
Easy

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

2. Run the provider from adb:
    ```bash
    # This command will delete all file records
    adb shell content delete --uri content://co.ostorlab.myblocnote.provider/file/1 --where "1=1 OR 'a'='a'"
    ```

3. Check the database entries

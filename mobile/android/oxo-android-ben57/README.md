# oxo-android-ben57: A User IP Disclosure Vulnerability

## Challenge Details

### Description
This Android application, "Passenger," contains a critical security vulnerability: it discloses the user's IP address and location to an external server. The app is designed to simulate a legitimate user flow where client information is retrieved from a backend, but it demonstrates how a misconfigured or malicious component can be used to leak sensitive network information.

The DriverActivity in this app makes an insecure HTTP request to a hardcoded endpoint to retrieve client data. By simply launching this activity (either via a button in the app or an external malicious intent), the user's IP address and location are exposed to the server. This highlights a classic Sensitive Data Exposure vulnerability, specifically, the insertion of sensitive network data into an external request.

### Vulnerability Type and Category
-   **Type:** Leak of Sensitive Information / Sensitive Data Exposure

-   **Category:** Insecure Communication / Insecure Data Storage (OWASP Mobile)

CWE: CWE-200 (Information Exposure) / CWE-532 (Insertion of Sensitive Information into Log File)

### Difficulty
Easy

## Build and Test Instructions

### Prerequisites
To test this app, you must first have the Python Flask server running on your computer.

Save the Python code as server.py.

Install Flask: 
```
pip install Flask.
```

Run the server: 
```
python server.py.
```

The server will be available at http://localhost:5000.

### Build
This project uses Android Studio with Kotlin. To build the debug APK from the terminal:
```
# Navigate to the app's root directory first
./gradlew assembleDebug
```

The APK will be located at app/build/outputs/apk/debug/app-debug.apk.

### How to Test
1. Ensure the Flask server is running on your computer.

2. Install the app on an Android emulator.

3. Monitor logs from your terminal using the app's specific log tag:
```
adb logcat -s "DriverActivity"
```

3. Trigger the vulnerability by clicking the "Show Client Data" button in the app's UI.

### Success Condition
A successful test requires observing the log entries in the terminal that explicitly show the full JSON data, including the IP addresses and locations, proving that the app has received and logged this sensitive information.

**Example of Successful find**:

```
D/DriverActivity: Full JSON Response: [{"name": "Alice", "location": "Paris, France", "ip": "192.168.1.42"}, {"name": "Bob", "location": "Berlin, Germany", "ip": "10.0.0.14"}]
D/DriverActivity: IP Address for Alice: 192.168.1.42
D/DriverActivity: Location for Alice: Paris, France
```

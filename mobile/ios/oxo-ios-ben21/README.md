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
4. **Open the malicious document `exploit.html`** to trigger the vulnerability
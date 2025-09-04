# oxo-ios-ben3 ShareNote - iOS Stored XSS

## Challenge Details

### Description
A seemingly innocuous note-sharing application for iOS, "ShareNote," allows users to create notes from HTML content or import `.html` files from other applications. The application fails to sanitize user-controlled HTML input before rendering it in a privileged WebView context, creating a classic injection vulnerability. Can your tool identify the data exfiltration path?

### Vulnerability Type and Category
- **Type:** Stored Cross-Site Scripting (XSS)
- **Category:** Injection

### Weakness (CWE)
- **CWE-79:** Improper Neutralization of Input During Web Page Generation ('Cross-site Scripting')

### Platform
iOS (Swift/SwiftUI)

### Difficulty
Medium

## Vulnerability Overview
The application's `NoteDetailView` renders note content by directly passing user-controlled input (from either manual creation or file import) to a `WKWebView`'s `loadHTMLString` method without any sanitization. This allows for the execution of arbitrary JavaScript code in the context of the application. The provided proof-of-concept payload demonstrates data exfiltration by leveraging an image's `onerror` event to send the device's User-Agent string to a remote server controlled by the attacker.

## Exploitation Vectors
1.  **File Import:** The app is registered to handle `.html` files. A malicious HTML file shared from Mail, Messages, or Safari will be imported and stored as a note.
2.  **Manual Creation:** The in-app note editor allows users to paste or write raw HTML content, which is stored without validation.

## Build Instructions
This is a standard Xcode project. Open `ShareNote.xcodeproj` and build for the iOS Simulator or a physical device.

1.  **Ensure your dummy server is running** to capture exfiltrated data (see `Server Setup` below).
2.  **Run the app** in the simulator (`Cmd + R`).
3.  **Trigger the vulnerability** by either:
    - Tapping the `+` button, creating a new note with HTML/JS content, saving it, and opening it.
    - Importing a malicious `.html` file into the app from another application (e.g., Safari, Files).

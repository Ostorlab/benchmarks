# oxo-ios-ben21: Content-Type Confusion XSS

## Description

DocuShare Pro is a professional document management application for iOS that allows users to store, organize, and view documents from various sources. The app provides a clean interface for adding documents via URL and viewing them through an integrated web viewer.

The application contains a **Content-Type confusion vulnerability** in its WebView component that allows attackers to execute arbitrary JavaScript code by serving malicious HTML content with incorrect Content-Type headers.

### Vulnerability Type and Category
- **Type:** Cross-Site Scripting (XSS) via Content-Type Confusion
- **Category:** Injection / Improper Input Validation
- **CWE:** CWE-79 (Improper Neutralization of Input During Web Page Generation)

### Difficulty
Medium

## Vulnerability Overview

The vulnerability exists in the `DocumentViewerViewController` class, which uses a WKWebView to display remote documents. The app fails to validate that remote content matches user expectations based on the URL, allowing attackers to serve malicious HTML content from URLs that appear to be safe document files.

### Root Cause
The app's WebView loads external URLs without validating that the content matches user expectations based on the URL:

```swift
// Vulnerable code pattern in DocumentViewerViewController.swift
private func loadDocument() {
    let request = URLRequest(url: document.url)
    webView.load(request) // No URL/content validation
}
```

The vulnerability occurs when an attacker serves HTML content (with correct `Content-Type: text/html`) from a URL that appears to be a document file (e.g., `evil.com/report.pdf`). The WebView renders the HTML content, executing any JavaScript, while the user believes they are viewing a safe document.

## Attack Scenario

### Step 1: Malicious Server Setup
An attacker hosts an HTML file containing JavaScript on their server:

```html
<!DOCTYPE html>
<html>
<head><title>Important Business Document</title></head>
<body>
<h1>Q4 Financial Report</h1>
<p>Loading document data...</p>
<script>
alert('XSS Executed in DocuShare Pro!\n' + 
      'User Agent: ' + navigator.userAgent + '\n' + 
      'Origin: ' + location.origin);
</script>
</body>
</html>
```

### Step 2: Server Response
The attacker configures their server to serve HTML content with standard headers:

```http
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 284

[HTML content with JavaScript payload]
```

### Step 3: Social Engineering
The attacker sends the victim a convincing URL:
- "Check out our Q4 financial report: `https://malicious-server.com/report.pdf`"
- The URL appears legitimate and ends with `.pdf`
- Victim trusts the source and adds it to DocuShare Pro

### Step 4: XSS Execution
1. Victim opens DocuShare Pro and taps "+" to add document
2. Victim enters the malicious URL and taps "Add Document"
3. Document appears in library with proper PDF icon (based on URL extension)
4. Victim taps document to view it
5. WebView loads the URL and receives HTML content
6. **VULNERABILITY**: WebView renders HTML content while user expects PDF document
7. JavaScript executes within the app's WebView context, enabling data theft, credential harvesting, or further attacks

## Business Impact

### Immediate Risks
- **Data Access**: JavaScript can access other documents loaded in the same WebView context
- **Session Hijacking**: Potential access to authentication tokens and session data
- **Credential Theft**: Display fake login prompts to steal user credentials
- **Privacy Violation**: Access device information, geolocation, and user data

### Advanced Attack Scenarios
1. **Corporate Document Theft**: Access sensitive business documents stored in the app
2. **Phishing Attacks**: Display convincing fake corporate login screens
3. **Malware Distribution**: Redirect to App Store malware or malicious profiles
4. **Social Engineering**: Display fake urgent security warnings or notifications

## Exploitation Methods

### Basic XSS Proof-of-Concept
Test URL that demonstrates the vulnerability:
```
1. Set up web server serving HTML content from PDF-like URL
2. Add URL to DocuShare Pro: https://attacker-server.com/report.pdf
3. Server responds with HTML content and Content-Type: text/html
4. User taps document expecting PDF viewer
5. Result: JavaScript executes in WebView context - XSS achieved
```

### Advanced Payloads
- **Data Exfiltration**: `fetch()` sensitive data to attacker server
- **Credential Harvesting**: Display fake authentication dialogs
- **Persistent Attacks**: Use localStorage to persist malicious scripts
- **Cross-Document Access**: Access other documents via WebView history

## Testing Methodology

### Manual Testing Steps
1. Set up a web server serving HTML with incorrect Content-Type headers
2. Create HTML content with visible JavaScript payload (alert, console.log)
3. Add the malicious URL to DocuShare Pro as a new document
4. Tap the document to open it in the viewer
5. Verify JavaScript execution through alert dialog or console output

### Required Server Configuration
```bash
# Example nginx configuration  
location /malicious.pdf {
    add_header Content-Type text/html;
    return 200 '<!DOCTYPE html><html><body><h1>Business Document</h1><script>alert("XSS in DocuShare Pro!");</script></body></html>';
}
```

### Automated Testing
- **Content-Type Fuzzing**: Test various MIME types with HTML content
- **URL Extension Testing**: Test different file extensions with HTML responses  
- **Payload Injection**: Test various JavaScript injection techniques
- **Browser Behavior Analysis**: Document WebView rendering decisions

## Risk Assessment

### Risk Level: HIGH

**Factors Contributing to High Risk:**
- **Easy Exploitation**: Simple to execute with basic web server setup
- **Wide Attack Surface**: Any URL can be a potential attack vector
- **User Trust**: Professional app interface reduces user suspicion
- **Business Context**: Document management apps handle sensitive data
- **No User Warning**: WebView provides no indication of Content-Type mismatch

### Affected User Actions
- Adding documents via URL input
- Opening documents from email or message links
- Sharing document URLs with colleagues
- Importing documents from external sources

## Real-World Context

This vulnerability mirrors real-world issues found in popular applications:

- **LINE iOS Client (CVE-2021-36214)**: Similar Content-Type confusion in WebView
- **Corporate Document Apps**: Common pattern in enterprise document viewers
- **File Sharing Platforms**: Many mobile apps exhibit similar WebView misconfigurations

The vulnerability demonstrates how legitimate business functionality can become a security risk when proper input validation is missing from WebView implementations.

## Technical Details

### WebView Behavior
- WKWebView attempts to determine content type from server headers
- When Content-Type conflicts with actual content, rendering decision varies
- Default behavior tends to favor HTML rendering over binary download prompts
- No built-in protection against Content-Type spoofing attacks

### Attack Requirements
- **Attacker Server**: Web server with configurable Content-Type headers
- **Social Engineering**: Convincing document URL or context
- **User Action**: Victim must add malicious URL and open document
- **Network Access**: App must be able to reach attacker-controlled server
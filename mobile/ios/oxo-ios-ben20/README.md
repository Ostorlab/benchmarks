# oxo-ios-ben20: Content-Type Confusion XSS

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

Attackers exploit the URL deception by serving HTML content from document-like URLs. Users expect to view safe documents but instead execute malicious JavaScript in the app's trusted context.

## Business Impact

- **Data Access**: JavaScript execution in trusted app context
- **Credential Theft**: Fake authentication prompts in professional interface
- **Document Access**: Potential access to other documents in app storage
- **Corporate Deception**: Professional appearance reduces user suspicion

## Exploitation Hints

### Testing Approaches
- **URL Deception**: Document-like URLs serving HTML content
- **JavaScript Payloads**: DOM manipulation, external callbacks
- **Network Proof**: Use services like interactsh for execution confirmation
- **Visual Indicators**: Background color changes to verify script execution

### Discovery Methods
- **Systematic URL Testing**: Try various file extensions with web content
- **Dynamic Analysis**: Monitor WebView behavior with different content types
- **Social Engineering**: Test realistic document sharing scenarios

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

## Attack Requirements
- **Attacker Server**: Web server serving HTML content from document-like URLs
- **Social Engineering**: Convincing document URL or business context
- **User Action**: Victim must add malicious URL and open document
- **Network Access**: App must be able to reach attacker-controlled server
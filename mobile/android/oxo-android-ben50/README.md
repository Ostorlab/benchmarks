# oxo-android-ben50 Incorrect URL Verification Vulnerability

## Challenge Details

### Description

NewsReader is a mobile news application that provides users with the latest news articles across various categories including technology, business, sports, health, science, entertainment, politics, and world news. The application features a modern interface with article browsing, search functionality, user profiles, and customizable settings.

The application demonstrates an **Incorrect URL Verification** vulnerability where the WebView component fails to properly validate URLs before loading them. The vulnerability allows attackers to potentially redirect users to malicious websites, perform phishing attacks, or exploit other web-based vulnerabilities by bypassing the application's weak URL validation mechanisms.

Key features of the application:
- News article browsing with categorized content
- Search functionality for finding specific articles
- Article detail view with full content display
- WebView integration for displaying full articles
- User profile management with preferences
- Customizable settings and notifications
- Article sharing functionality

### Vulnerability Type and Category
- **Type:** Incorrect URL Verification / Insufficient URL Validation
- **Category:** Platform Security (OWASP Mobile) / Improper Input Validation (CWE-20)

### Difficulty
Easy

## Vulnerability Details

**This application contains a critical URL verification vulnerability:**
- **Weak URL validation** that only checks for "http://" or "https://" prefixes
- **No domain validation** or whitelist checking for trusted sources
- **Missing redirect protection** allowing malicious redirects
- **WebView exploitation** through crafted URLs that bypass validation
- **No certificate pinning** or additional security checks

**This vulnerability could lead to:**
- Phishing attacks through malicious redirects
- Cross-site scripting (XSS) exploitation
- Man-in-the-middle attacks
- Data theft through fake login pages
- Malicious content injection

## Build Instructions

### Build
This project uses Android Studio with Java. To build the debug APK from the terminal:
```bash
# Navigate into the source code directory first
cd src/
./gradlew assembleDebug
```
The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

## Technical Details

### Architecture
- **Language**: Java
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle with Android Gradle Plugin 8.1.0

### Key Components
- `MainActivity`: Main news feed with article listing
- `ArticleDetailActivity`: Detailed article view with sharing options
- `WebViewActivity`: Contains the vulnerable URL verification logic
- `CategoryActivity`: News category browsing
- `SearchActivity`: Article search functionality
- `ProfileActivity`: User profile management
- `SettingsActivity`: Application settings and preferences

### Dependencies
- AndroidX AppCompat and Material Components
- ConstraintLayout and RecyclerView
- SwipeRefreshLayout and ViewPager2
- OkHttp for network requests
- WebView for article display

## Vulnerability Location

The primary vulnerability exists in the `WebViewActivity.java` file in the `isUrlAllowed()` method:

```java
private boolean isUrlAllowed(String url) {
    if (url == null || url.trim().isEmpty()) {
        return false;
    }
    
    url = url.trim().toLowerCase();
    
    if (url.startsWith("http://") || url.startsWith("https://")) {
        return true;  // Vulnerable: No domain validation
    }
    
    return false;
}
```

## Exploitation Scenarios

1. **Malicious Article URLs**: Attackers can inject articles with URLs pointing to phishing sites
2. **URL Parameter Manipulation**: Crafting URLs that pass validation but redirect to malicious content
3. **Protocol Handler Exploitation**: Using data: or javascript: schemes that may bypass validation
4. **Domain Spoofing**: Creating URLs that appear legitimate but redirect to attacker-controlled sites
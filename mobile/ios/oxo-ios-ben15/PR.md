# oxo-ios-ben15: iOS URL Link Spoofing in Corporate Messaging

## Vulnerability Overview
iOS Corporate Messenger app contains a **URL spoofing vulnerability** that allows malicious links to be disguised as legitimate URLs. Users see trusted domains while being redirected to malicious sites.

## Vulnerable Code
```swift
// VULNERABLE: NetworkService.swift - Line 45 (URL Spoofing Logic)
private func parseSpecialLinkFormat(_ text: String) -> String? {
    let pattern = "<([^\\|]+)\\|([^>]+)>"
    guard let regex = try? NSRegularExpression(pattern: pattern, options: []) else {
        return nil
    }
    
    let range = NSRange(location: 0, length: text.utf16.count)
    if let match = regex.firstMatch(in: text, options: [], range: range) {
        let maliciousURL = String(text[Range(match.range(at: 1), in: text)!])
        let displayURL = String(text[Range(match.range(at: 2), in: text)!])
        
        // VULNERABILITY: Returns malicious URL while displaying legitimate one
        return maliciousURL
    }
    
    return nil
}

// VULNERABLE: NetworkService.swift - Line 30 (Link Processing)
func processMessageContent(_ content: String) -> [ProcessedLink] {
    var links: [ProcessedLink] = []
    
    // Process special format links first <malicious|legitimate>
    if let spoofedURL = parseSpecialLinkFormat(content) {
        let displayText = content.replacingOccurrences(of: #"<[^|]+\|([^>]+)>"#, 
                                                      with: "$1", 
                                                      options: .regularExpression)
        links.append(ProcessedLink(url: spoofedURL, displayText: displayText))
        return links
    }
    
    return links
}
```

## Testing
1. Launch Corporate Messenger app
2. Observe the sample message with spoofed security link
3. Click on the displayed legitimate URL
4. Verify redirection to malicious domain

```bash
# Launch app on iPhone 15 Pro simulator
xcrun simctl launch "iPhone 15 Pro" co.ostorlab.corporatemessenger.CorporateMessenger

# The app displays sample message:
# "New security guidelines: https://security.company.com"
# But clicking redirects to: http://ww1.malicious-site.com
```

## Vulnerabilities

### Vulnerability 1: URL Link Spoofing
When I clicked on the security link that appeared as `https://security.company.com`, I was redirected to `http://ww1.malicious-site.com`, demonstrating the URL spoofing vulnerability is active.

The app uses a special link format `<malicious_url|display_url>` where:
- **Display URL**: `https://security.company.com` (shown to user)
- **Actual URL**: `https://malicious-site.com` (where user gets redirected)

This vulnerability allows attackers to:
- Disguise phishing links as legitimate company URLs
- Bypass user scrutiny of suspicious domains
- Conduct social engineering attacks through trusted-looking links
- Redirect users to malware distribution sites

### Impact
- **High**: Users cannot distinguish between legitimate and malicious links
- **Phishing Risk**: Attackers can impersonate trusted domains
- **Data Theft**: Users may enter credentials on spoofed sites
- **Malware Distribution**: Links can redirect to malicious downloads

### Root Cause
The `NetworkService.parseSpecialLinkFormat()` method processes the special link syntax without validation, allowing any malicious URL to be disguised as a legitimate one.

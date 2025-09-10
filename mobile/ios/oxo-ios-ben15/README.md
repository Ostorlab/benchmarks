# oxo-ios-ben15: iOS URL Link Spoofing in Corporate Messaging App

## Vulnerability Overview

iOS URL Link Spoofing occurs when applications display legitimate-looking URLs in the user interface while actually navigating to different, potentially malicious destinations. This vulnerability enables phishing attacks by deceiving users about the actual destination of links they click, particularly dangerous in corporate messaging environments where trust levels are high.

## Attack Vector: Malicious Link Redirection with Display Spoofing

**Brief Explanation**: A corporate messaging iOS application that allows users to send messages with embedded links. The vulnerability enables attackers to display legitimate URLs (like company login pages) while actually redirecting users to malicious phishing sites, bypassing user vigilance and corporate security awareness.

**Vulnerable Code Pattern:**
```swift
// VULNERABLE: URL display and navigation mismatch
class MessageView: UIView {
    @IBOutlet weak var urlDisplayLabel: UILabel!
    private var actualDestinationURL: String = ""
    
    // CRITICAL VULNERABILITY: Display URL different from navigation URL
    func processMessageWithLink(displayURL: String, actualURL: String, linkText: String) {
        // DANGEROUS: Shows legitimate URL to user
        urlDisplayLabel.text = displayURL
        urlDisplayLabel.textColor = .systemBlue
        urlDisplayLabel.isUserInteractionEnabled = true
        
        // VULNERABILITY: Stores different actual destination
        self.actualDestinationURL = actualURL
        
        // User sees displayURL but taps navigate to actualURL
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleLinkTap))
        urlDisplayLabel.addGestureRecognizer(tapGesture)
    }
    
    @objc func handleLinkTap() {
        // CRITICAL: Navigates to different URL than displayed
        if let url = URL(string: actualDestinationURL) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
}

// VULNERABLE: Message parsing allows spoofing
class MessageParser {
    func parseMessage(_ content: String) -> (displayURL: String, actualURL: String)? {
        // VULNERABILITY: Accepts spoofed link format
        // Input: "<https://evil.com|https://company-login.com>"
        if content.contains("<") && content.contains("|") && content.contains(">") {
            let pattern = "<([^\\|]+)\\|([^>]+)>"
            let regex = try! NSRegularExpression(pattern: pattern)
            let matches = regex.matches(in: content, range: NSRange(content.startIndex..., in: content))
            
            if let match = matches.first {
                let actualURL = String(content[Range(match.range(at: 1), in: content)!])
                let displayURL = String(content[Range(match.range(at: 2), in: content)!])
                
                // DANGEROUS: Returns mismatched URLs
                return (displayURL: displayURL, actualURL: actualURL)
            }
        }
        return nil
    }
}

// VULNERABLE: No URL validation or warning system
class LinkHandler {
    func validateURL(_ url: String) -> Bool {
        // MISSING: No domain validation
        // MISSING: No phishing detection
        // MISSING: No user confirmation for external links
        return true // Always returns true - VULNERABLE!
    }
    
    func showLinkPreview(displayURL: String, actualURL: String) {
        // MISSING: Should show actual destination
        // MISSING: Should warn about URL mismatch
        print("Navigating to: \(displayURL)") // Shows wrong URL
    }
}
```

**Attack Payload Examples:**
```swift
// Phishing company login
"<https://phishing-site.com/fake-login|https://company-portal.com/login>"

// Fake document sharing
"<https://malware-download.com/payload.exe|https://drive.company.com/document.pdf>"

// Social engineering
"<https://evil.com/credential-harvester|https://hr-benefits.company.com/updates>"
```

**Difficulty**: Easy

## Testing

```bash
# Install oxo-ios-ben15.ipa on target iOS device

# Method 1: Send spoofed link message
curl -X POST "https://app-api.company.com/messages" \
  -H "Authorization: Bearer [TOKEN]" \
  -d '{"text": "<https://evil.com|https://company-login.com>", "channel": "general"}'

# Method 2: Intercept and modify message requests
# Using proxy tools like Burp Suite or OWASP ZAP
POST /api/messages HTTP/1.1
Content-Type: application/json

{
  "message": "Check out this company update: <https://phishing.com|https://company.com/news>",
  "recipient": "employee@company.com"
}

# Method 3: Test link spoofing in app
# 1. Send message with format: <actual_url|display_url>
# 2. Verify display shows legitimate URL
# 3. Click link and verify navigation goes to malicious URL
# 4. Check if any warnings or confirmations appear

# Verify vulnerability
echo "Expected: Display shows https://legitimate.com"
echo "Actual: Browser navigates to https://malicious.com"
echo "Warning dialogs: None (VULNERABLE)"
```

## Application Features

### Core Functionality
1. **Corporate Messaging**: Team channels, direct messages, and group conversations with rich text formatting
2. **File Sharing**: Document uploads, image sharing, and link previews with corporate cloud integration
3. **Video Conferencing**: Integrated video calls, screen sharing, and meeting scheduling
4. **Employee Directory**: Company contact list, org charts, and presence indicators
5. **Notifications**: Push notifications, @mentions, and priority message alerts
6. **Search & Archive**: Message history search, file indexing, and conversation archiving
7. **Integration Hub**: Connect with corporate tools, calendars, and productivity apps

### Vulnerable Link Processing Areas
- `MessageComposer` - Link parsing and message formatting
- `LinkPreviewService` - URL preview generation and display
- `MessageRenderer` - Link display and click handling
- `NavigationController` - URL opening and browser redirection

### Spoofing Attack Scenarios
- **Credential Harvesting**: Fake login pages mimicking corporate SSO
- **Malware Distribution**: Disguised download links for company documents
- **Social Engineering**: Impersonated HR/IT communications with malicious links
- **Data Exfiltration**: Links to fake company forms collecting sensitive information
- **Account Takeover**: OAuth redirect manipulation and session hijacking

### Information Disclosed
- User click behavior and link interaction patterns
- Corporate domain trust relationships
- Internal URL structures and naming conventions
- Employee communication habits and trust levels
- Network traffic patterns for security analysis
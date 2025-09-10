# oxo-ios-ben7: iOS Deeplink Leading to Sensitive Action Performed Automatically

## Vulnerability Overview

iOS Deeplink Leading to Sensitive Action Performed Automatically occurs when iOS applications register custom URL schemes and implement deeplink handlers that automatically execute sensitive actions without proper user confirmation or authentication.

## Attack Vector: Automatic Money Transfer via Deeplinks

**Brief Explanation**: An iOS finance application that registers the custom URL scheme `financetracker://` and implements a deeplink handler can be exploited to automatically perform money transfers when malicious deeplinks are triggered.


**Vulnerable Code Pattern:**
```swift
// VULNERABLE: URL scheme registration in Info.plist
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>financetracker</string>
        </array>
    </dict>
</array>

// VULNERABLE: Automatic deeplink processing without validation
@main
struct FinanceTrackerApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // VULNERABILITY: Automatic execution of sensitive actions
                    handleDeeplink(url: url)
                }
        }
    }
    
    // CRITICAL VULNERABILITY: Automatic money transfer without user consent
    private func handleTransferDeeplink(queryItems: [URLQueryItem]) {
        var fromAccount: String?
        var toAccount: String?
        var amount: Double = 0
        var reason: String = "Deeplink Transfer"
        
        for item in queryItems {
            switch item.name {
            case "from": fromAccount = item.value
            case "to": toAccount = item.value
            case "amount": amount = Double(item.value ?? "0") ?? 0
            case "reason": reason = item.value ?? "Deeplink Transfer"
            default: break
            }
        }
        
        // CRITICAL: No user confirmation or authentication required
        if let from = fromAccount, let to = toAccount, amount > 0 {
            executeAutomaticTransfer(from: from, to: to, amount: amount, reason: reason)
        }
    }
}
```

**Malicious Exploit URL:**
```bash
# MALICIOUS: Automatic money transfer without user consent
financetracker://transfer?from=main%20checking&to=savings%20account&amount=1000&reason=Emergency%20Transfer
```

**Difficulty**: Medium

## Testing

```bash
# Run the FinanceTracker app in an iOS device

# Test automatic money transfer vulnerability (CRITICAL)
open "financetracker://transfer?from=main%20checking&to=savings%20account&amount=1000&reason=Emergency%20Transfer"

# Monitor app logs for vulnerability confirmation
xcrun simctl spawn booted log stream --predicate 'process == "FinanceTracker"'
```

## Application Features

### Core Functionality
1. **Budget Overview Dashboard**: Visual spending tracking with progress indicators and category breakdowns
2. **Expense Entry**: Intuitive expense recording with category selection and currency conversion
3. **Currency Converter**: Real-time currency conversion with rate history and popular pairs
4. **Expense Reports**: Detailed analytics with charts, breakdowns, and export capabilities
5. **Settings & Preferences**: User customization, security settings, and data management
6. **Money Transfer**: Quick transfer functionality between accounts and contacts
7. **Deeplink Actions**: URL scheme handlers for various financial operations

### Vulnerable Deeplink Endpoint
- `financetracker://transfer` - Money transfers without confirmation


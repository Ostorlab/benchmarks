# Building SecureBank IPA for Distribution

## Prerequisites

**Development Environment:**
- macOS with Xcode installed
- iOS Simulator (for testing)
- No Apple Developer account required

## Build Commands

### Step 1: Configure Signing in Xcode (REQUIRED)
**CRITICAL**: iOS apps require signing configuration even for unsigned builds.

1. **Open project**: `SecureBank.xcodeproj` in Xcode
2. **Select project** (blue SecureBank icon in navigator)
3. **Select "SecureBank" target**
4. **Go to "Signing & Capabilities" tab**
5. **Uncheck "Automatically manage signing"**
6. **Set "Provisioning Profile" to "None"**
7. **Set "Signing Certificate" to "Don't Code Sign"**

**Why this matters**: Without proper signing configuration, archive will fail with "development team" errors.

### Step 2: Create ExportOptions.plist
Create this file in your project root (where SecureBank.xcodeproj is located):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>method</key>
    <string>ad-hoc</string>
    <key>signingStyle</key>
    <string>manual</string>
    <key>destination</key>
    <string>export</string>
    <key>stripSwiftSymbols</key>
    <false/>
    <key>compileBitcode</key>
    <false/>
    <key>signingCertificate</key>
    <string>-</string>
    <key>provisioningProfiles</key>
    <dict/>
</dict>
</plist>
```

### Step 3: Build Archive (Unsigned)
```bash
cd /path/to/SecureBank  # Where SecureBank.xcodeproj is located

# Build unsigned archive for iOS devices
xcodebuild -scheme SecureBank -configuration Debug -sdk iphoneos -archivePath SecureBank.xcarchive archive CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO
```

### Step 4: Create IPA (Manual Method - RECOMMENDED)
**Note**: iOS export often fails for unsigned builds. Manual extraction is more reliable.

```bash
# Create output folder
mkdir -p ../../ipas

# Extract .app from archive and create IPA
cp -R "SecureBank.xcarchive/Products/Applications/SecureBank.app" ../../ipas/
cd ../../ipas
zip -r oxo-ios-ben2.ipa SecureBank.app/
rm -rf SecureBank.app
```

**Why manual works**: An IPA is just a ZIP file containing the .app bundle. This method bypasses Apple's complex export requirements.

## Alternative: Xcode GUI Method

1. **Archive**: Product → Archive (requires signing setup above)
2. **Manual extraction**: Find archive in Xcode Organizer → Show in Finder
3. **Extract**: Copy `.app` from archive and zip as shown above

**Note**: Xcode's "Distribute" option typically fails for unsigned builds.

## Repository Structure
```
mobile/ios/oxo-ios-ben2/
├── README.md                    # Vulnerability documentation
├── ipas/
│   └── oxo-ios-ben2.ipa        # Built unsigned IPA
└── src/
    └── SecureBank/
        ├── SecureBank/         # Source code
        │   ├── ViewController.swift
        │   ├── LoginViewController.swift
        │   ├── DashboardViewController.swift
        │   ├── ProfileViewController.swift
        │   ├── SettingsViewController.swift
        │   ├── SceneDelegate.swift      # VULNERABLE: Deep link handler
        │   ├── AppDelegate.swift
        │   ├── Info.plist               # URL scheme registration
        │   └── Base.lproj/
        ├── SecureBank.xcodeproj    # Xcode project
        └── building.md             # This file
```

## Testing the Vulnerability

### Install and Launch
```bash
# Install on iOS Simulator
xcrun simctl install booted ../../ipas/oxo-ios-ben2.ipa

# Launch app
xcrun simctl launch booted com.securebank.SecureBank
```

### Exploit Deep Link Vulnerability
```bash
# Access other users' profiles without authorization
xcrun simctl openurl booted "securebank://profile?user_id=456"  # Sarah Johnson
xcrun simctl openurl booted "securebank://profile?user_id=789"  # Mike Wilson
xcrun simctl openurl booted "securebank://profile?user_id=999"  # Admin Account
```

## Key Lessons Learned

**For Future iOS Benchmarks:**

1. **Signing Setup is Critical**: Always configure signing in Xcode first, even for unsigned builds
2. **Manual IPA Creation Works Best**: Direct .app extraction and ZIP is more reliable than Xcode export
3. **File Management**: Use shared folders in QEMU, but be aware of file corruption during copies
4. **Adding Files to Xcode**: New Swift files must be explicitly added to build targets
5. **Testing Deep Links**: Use `xcrun simctl openurl` with iOS Simulator for reliable testing

**Common Pitfalls Avoided:**
- Don't rely on automatic Xcode export for unsigned builds
- Always check that new files are added to build targets
- Test deep links from terminal, not just Safari in simulator
- Document what actually works, not what was attempted
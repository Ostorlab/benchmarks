# iOS App Building and Installation Guide

## Building the IPA

### Method 1: Using Build Script (Recommended)
```bash
# Navigate to your Xcode project directory
cd /path/to/ShopZenNew/

# Make script executable
chmod +x build_ipa.sh

# Run build script
./build_ipa.sh
```

### Method 2: Manual Build
```bash
# Build the app
xcodebuild \
  -scheme "ShopZenNew" \
  -project "ShopZenNew.xcodeproj" \
  -sdk iphonesimulator \
  -configuration Debug \
  CODE_SIGN_IDENTITY="" \
  CODE_SIGNING_REQUIRED=NO \
  CODE_SIGNING_ALLOWED=NO \
  -derivedDataPath ./build

# Create IPA package
mkdir -p Payload
cp -r build/Build/Products/Debug-iphonesimulator/ShopZenNew.app Payload/
zip -r "oxo-ios-ben10.ipa" Payload
rm -rf Payload build
```

## Installing on iOS Simulator

### Option 1: Install IPA directly
```bash
# Start simulator
open -a Simulator

# Install IPA on booted simulator
xcrun simctl install booted oxo-ios-ben10.ipa
```

### Option 2: Install .app bundle
```bash
# Install .app directly (if you have the .app file)
xcrun simctl install booted build/Build/Products/Debug-iphonesimulator/ShopZenNew.app
```

## Launching the App

### Get Bundle ID
```bash
# Extract bundle ID from app
BUNDLE_ID=$(defaults read "$(pwd)/build/Build/Products/Debug-iphonesimulator/ShopZenNew.app/Info" CFBundleIdentifier)
echo "Bundle ID: $BUNDLE_ID"
```

### Launch App
```bash
# Launch using bundle ID
xcrun simctl launch booted $BUNDLE_ID

# Or launch directly with known bundle ID
xcrun simctl launch booted com.shopzen.new
```

### Complete Launch Script
```bash
#!/bin/bash

# Open simulator
open -a Simulator

# Wait for simulator to boot
sleep 5

# Install app
xcrun simctl install booted oxo-ios-ben10.ipa

# Get bundle ID and launch
BUNDLE_ID="com.shopzen.new"
xcrun simctl launch booted $BUNDLE_ID

echo "✅ ShopZen launched successfully!"
```

## Simulator Management

### List Available Simulators
```bash
xcrun simctl list devices
```

### Boot Specific Simulator
```bash
# Boot iPhone 15 Pro
xcrun simctl boot "iPhone 15 Pro"

# Or use device UUID
xcrun simctl boot DEVICE_UUID
```

### Uninstall App
```bash
xcrun simctl uninstall booted com.shopzen.new
```

## Signing for Real Device Installation

### Prerequisites for Device Installation
- **Apple Developer Account** (free or paid)
- **Xcode with signing certificates**
- **Physical iOS device**

### Re-build with Device Signing
```bash
# Build for device with signing
xcodebuild \
  -scheme "ShopZenNew" \
  -project "ShopZenNew.xcodeproj" \
  -sdk iphoneos \
  -configuration Debug \
  -derivedDataPath ./build \
  DEVELOPMENT_TEAM="YOUR_TEAM_ID" \
  CODE_SIGN_IDENTITY="iPhone Developer"

# Create signed IPA
mkdir -p Payload
cp -r build/Build/Products/Debug-iphoneos/ShopZenNew.app Payload/
zip -r "oxo-ios-ben10-device.ipa" Payload
```

### Alternative: Re-sign Existing IPA
```bash
# Install iOS App Signer or use codesign
codesign --force --sign "iPhone Developer: Your Name" oxo-ios-ben10.ipa
```

### Device Installation Methods

**Option 1: Xcode**
- Connect device via USB
- Drag IPA to Xcode Devices window

**Option 2: 3uTools / iMazing**
- Use third-party tools to install IPA

**Option 3: Command Line (requires configured device)**
```bash
# Install to connected device
xcrun devicectl device install app --device YOUR_DEVICE_ID oxo-ios-ben10-device.ipa
```

## Troubleshooting

### Common Issues

**"App not found" error:**
```bash
# Check if app is installed
xcrun simctl listapps booted
```

**Bundle ID issues:**
```bash
# Check Info.plist manually
plutil -p build/Build/Products/Debug-iphonesimulator/ShopZenNew.app/Info.plist | grep CFBundleIdentifier
```

**Simulator not booting:**
```bash
# Reset simulator
xcrun simctl erase all
```

### Useful Commands
```bash
# Take screenshot
xcrun simctl io booted screenshot screenshot.png

# Record video
xcrun simctl io booted recordVideo recording.mov

# Push file to simulator
xcrun simctl addmedia booted ~/Desktop/photo.png

# Open URL in simulator
xcrun simctl openurl booted "https://example.com"
```
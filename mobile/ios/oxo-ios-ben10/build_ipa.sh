#!/bin/bash

# ShopZen IPA Build Script
# Run this from your Xcode project directory

echo "Building ShopZen IPA..."

# Step 1: Build the app
echo "Step 1: Building app with xcodebuild..."
xcodebuild \
  -scheme "ShopZenNew" \
  -project "ShopZenNew.xcodeproj" \
  -sdk iphonesimulator \
  -configuration Debug \
  CODE_SIGN_IDENTITY="" \
  CODE_SIGNING_REQUIRED=NO \
  CODE_SIGNING_ALLOWED=NO \
  -derivedDataPath ./build

# Check if build succeeded
if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build succeeded!"

# Step 2: Create IPA
echo "Step 2: Creating IPA package..."

# Create Payload folder
mkdir -p Payload

# Copy built .app into Payload
cp -r build/Build/Products/Debug-iphonesimulator/ShopZenNew.app Payload/

# Check if app was copied
if [ ! -d "Payload/ShopZenNew.app" ]; then
    echo "❌ Failed to copy app bundle!"
    exit 1
fi

# Zip into IPA
zip -r "oxo-ios-ben10.ipa" Payload

# Optional cleanup
rm -rf Payload
rm -rf build

echo "✅ IPA created: oxo-ios-ben10.ipa"
echo "📱 You can now install this on iOS simulators or devices"
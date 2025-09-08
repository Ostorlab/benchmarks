# PhotoShare IPA

This directory will contain the compiled PhotoShare.ipa file after building the Xcode project.

## Building Instructions

1. Open `PhotoShare.xcodeproj` in Xcode
2. Select a target device (iPhone Simulator or physical device)
3. Build and run the project (⌘+R)
4. To create an IPA: Product → Archive → Distribute App

The compiled IPA can be used for testing the location data leakage vulnerability on physical devices or through static analysis tools.

# oxo-ios-ben5 PhotoShare - iOS Location Data Leakage

## Challenge Details

### Description
PhotoShare is a modern iOS photo sharing application that allows users to capture, edit, and share their favorite moments. The app features automatic photo enhancement, social sharing capabilities, and cloud backup functionality. However, the application inadvertently embeds sensitive location metadata (GPS coordinates) into exported images without explicit user consent or awareness, potentially exposing users' private locations to third parties.

### Vulnerability Type and Category
- **Type:** Information Disclosure
- **Category:** Privacy Violation / Data Leakage

### Weakness (CWE)
- **CWE-200:** Exposure of Sensitive Information to an Unauthorized Actor
- **CWE-359:** Exposure of Private Information ('Privacy Violation')

### Platform
iOS (Swift/SwiftUI)

### Difficulty
Medium

## Vulnerability Overview
The PhotoShare application captures photos with full location metadata preserved and exports them with embedded GPS coordinates in the EXIF data. When users share photos through the app's export functionality, the original location data remains intact, allowing recipients to extract precise geographical coordinates of where the photo was taken. This creates a significant privacy risk as users are unaware that their location information is being shared alongside their images.

## Exploitation Vectors
1. **Photo Export:** Users can export photos to the Photos app, Files, or share them directly through Messages/Mail with location data intact.
2. **Social Sharing:** The app's built-in sharing features preserve EXIF metadata including GPS coordinates.
3. **Cloud Backup:** Photos uploaded to cloud services retain their location metadata.

## Build Instructions
This is a standard Xcode project. Open `PhotoShare.xcodeproj` and build for the iOS Simulator or a physical device.

1. **Run the app** in the simulator (`Cmd + R`).
2. **Grant location and camera permissions** when prompted.
3. **Take a photo** using the in-app camera functionality.
4. **Export or share the photo** using the share button.
5. **Analyze the exported image** using EXIF data readers to confirm GPS coordinates are embedded.

## Proof of Concept
1. Launch the PhotoShare app
2. Allow location and camera permissions
3. Take a photo using the camera feature
4. Export the photo to Photos or share via Messages
5. Use an EXIF metadata reader to extract GPS coordinates from the shared image
6. The exact location where the photo was taken will be revealed

## Impact
- **Privacy Violation:** Users' precise locations are unknowingly shared
- **Stalking/Harassment Risk:** Malicious actors can track user movements
- **Home Address Exposure:** Photos taken at home reveal residential addresses
- **Pattern Analysis:** Multiple photos can reveal daily routines and frequented locations

## Remediation
- Implement EXIF data stripping before photo export
- Provide user controls for location metadata inclusion
- Display clear warnings when location data will be preserved
- Offer location-free export options

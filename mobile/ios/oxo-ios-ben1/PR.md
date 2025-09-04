# Add oxo-ios-ben1: iOS Personal Finance Tracker with Hardcoded Secrets

## Summary
This PR adds **oxo-ios-ben1**, a comprehensive iOS benchmark application that demonstrates hardcoded secrets vulnerabilities within a realistic Personal Finance Tracker app. The benchmark provides a native iOS application with embedded credentials and API keys distributed across legitimate financial functionality.

## Benchmark Details

### 🎯 **Vulnerability Type**
- **Primary**: Hardcoded Secrets/Credentials (CWE-798)
- **Category**: Cryptographic Issues / Insecure Data Storage
- **Difficulty**: Medium
- **Platform**: iOS 17.5+ (SwiftUI)

### 📱 **Application Overview**
Personal Finance Tracker is a fully functional iOS application that helps users manage their finances with:

- **Budget Overview Dashboard** - Visual spending tracking and category analysis
- **Expense Entry System** - Intuitive expense recording with real-time currency conversion
- **Currency Converter** - Live exchange rates with history tracking
- **Expense Reports** - Detailed analytics with charts and export capabilities
- **Settings & Security** - User preferences and data management

### 🔐 **Embedded Security Test Cases**

The application contains **6 categories of hardcoded secrets** naturally embedded within legitimate functionality:

1. **Currency API Keys** - Hardcoded keys for real-time exchange rate services
2. **Database Encryption Keys** - Hardcoded AES keys for local data protection
3. **Cloud Sync Credentials** - Hardcoded OAuth tokens and authentication credentials
4. **Backup Encryption Keys** - Hardcoded keys exposed in settings and backup operations
5. **API Secret Keys** - Hardcoded credentials for secure financial API connections
6. **Admin Credentials** - Hardcoded administrative passwords for system access

### 🛠 **Technical Implementation**

- **Framework**: SwiftUI with Combine for reactive programming
- **Architecture**: MVVM pattern with environment objects
- **Data Layer**: Core Data integration with encrypted storage
- **UI Components**: Native iOS controls with accessibility support
- **Build Target**: iOS devices (arm64-apple-ios17.5)

### 📁 **Package Contents**

```
oxo-ios-ben1/
├── README.md                 # Comprehensive documentation
├── PR.md                     # This PR description
├── ipa/
│   └── oxo-ios-ben1.ipa     # Unsigned iOS app package (5.4 MB)
└── src/
    └── FinanceTracker/       # Complete Xcode project source
        ├── FinanceTracker.xcodeproj/
        ├── FinanceTracker/
        │   ├── ContentView.swift
        │   ├── Models/
        │   ├── Views/
        │   ├── Services/
        │   └── Resources/
        └── FinanceTracker.entitlements
```

### 🎯 **Testing Scenarios**

**Primary Assessment Goals:**
- Static analysis detection of hardcoded credentials across Swift codebase
- Runtime analysis of embedded secrets during app execution
- Binary analysis of compiled IPA for credential extraction
- Source code review for insecure credential storage patterns

**Verification Methods:**
- Install IPA on iOS simulator/device for dynamic testing
- Analyze Swift source code for hardcoded patterns
- Binary analysis of compiled application bundle
- Runtime monitoring of credential usage during app operation

### 🔍 **Discovery Techniques**

**Static Analysis Targets:**
- String literals containing API keys and tokens
- Hardcoded encryption keys in Swift constants
- Embedded credentials in configuration files
- Authentication tokens in service classes

**Dynamic Analysis Opportunities:**
- Network traffic monitoring for credential transmission
- Runtime memory analysis for key material
- Keychain usage patterns and insecure storage
- File system analysis for credential caching

### 📊 **Expected Impact**

This benchmark enables comprehensive testing of:
- **Mobile SAST Tools** - Static analysis of Swift/iOS codebases
- **Binary Analysis Tools** - IPA reverse engineering and credential extraction
- **Runtime Security Testing** - Dynamic analysis of iOS applications
- **Code Review Processes** - Manual identification of hardcoded secrets

### 🚀 **Deployment Ready**

- ✅ **Unsigned IPA** - Ready for testing environments and simulators
- ✅ **Complete Source** - Full Xcode project with embedded vulnerabilities
- ✅ **Documentation** - Comprehensive setup and testing guidelines
- ✅ **Realistic Context** - Natural integration within legitimate app functionality

---

## Files Added

- `mobile/ios/oxo-ios-ben1/README.md` - Complete benchmark documentation
- `mobile/ios/oxo-ios-ben1/PR.md` - This PR description
- `mobile/ios/oxo-ios-ben1/ipa/oxo-ios-ben1.ipa` - Unsigned iOS application package
- `mobile/ios/oxo-ios-ben1/src/FinanceTracker/` - Complete Swift/SwiftUI source code

## Testing Instructions

1. **Install Application**: Deploy IPA to iOS simulator or device
2. **Source Analysis**: Review Swift files for hardcoded credential patterns
3. **Runtime Testing**: Monitor app behavior and credential usage
4. **Binary Analysis**: Extract and analyze compiled application bundle

This benchmark provides a comprehensive testing ground for iOS security assessment tools and demonstrates real-world hardcoded secrets vulnerabilities within a realistic financial application context.

# Ostorlab Security Benchmarks

A comprehensive collection of vulnerable Android applications designed to validate and test mobile security analysis tools.

## Overview

This repository contains Android security benchmarks that demonstrate various mobile security vulnerabilities. Each benchmark is a standalone Android application with specific security flaws, designed to help security researchers, developers, and testing tools identify and understand mobile security issues.

## Repository Structure

```
benchmarks/
└── mobile/
    └── android/
        ├── oxo-android-ben1/    # JavaScript Injection & Open Redirect
        ├── oxo-android-ben2/    # Cleartext data in SharedPreferences
        ├── oxo-android-ben3/    # Hardcoded Secrets
        ├── oxo-android-ben4/    # Cleartext data in Internal Storage
        ├── oxo-abdroid-ben5/    # XSS via Intent Parameter Injection
        ├── oxo-android-ben6/    # Cleartext data in External Storage
        ├── oxo-android-ben7/    # Cleartext data in SQLite
        ├── oxo-android-ben8/    # Path Traversal / File Access
        ├── oxo-android-ben9/    # TLS/SSL Certificate Validation
        ├── oxo-android-ben11/   # Stored XSS
        ├── oxo-android-ben12/   # Cleartext Communication (HTTP)
        ├── oxo-android-ben14/   # Unprotected Critical Activities
        └── oxo-android-ben24/   # Sensitive Data in Logs
```

## Vulnerability Categories

### 🔐 Insecure Data Storage
- **oxo-android-ben2**: Cleartext sensitive data in SharedPreferences
- **oxo-android-ben4**: Cleartext sensitive data in Internal Storage  
- **oxo-android-ben6**: Cleartext sensitive data in External Storage
- **oxo-android-ben7**: Cleartext sensitive data in SQLite Database

### 🌐 WebView Security Issues
- **oxo-android-ben1**: JavaScript Injection & Open URL Redirect
- **oxo-abdroid-ben5**: Cross-Site Scripting (XSS) via Intent Parameter Injection
- **oxo-android-ben11**: Stored Cross-Site Scripting (XSS)

### 🔒 Network Security
- **oxo-android-ben9**: Lack of TLS/SSL Certificate Validation
- **oxo-android-ben12**: Cleartext Communication (HTTP Usage)

### 📱 Platform Security
- **oxo-android-ben14**: Critical Activities Not Protected / Improper Component Export
- **oxo-android-ben8**: Path Traversal Leading to Arbitrary File Access

### 💻 Code Security
- **oxo-android-ben3**: Hardcoded Secrets and Credentials
- **oxo-android-ben24**: Sensitive Information Leakage in Application Logs

## Difficulty Levels

- **Easy**: Most benchmarks (ben1, ben2, ben3, ben4, ben6, ben7, ben8, ben9, ben11, ben12, ben14, ben24)
- **Medium**: oxo-abdroid-ben5 (XSS via Intent Injection)

## Usage

Each benchmark is a complete Android project that can be built and deployed:

1. **Navigate to specific benchmark**: `cd mobile/android/oxo-android-ben[X]/`
2. **Open in Android Studio** or build via command line
3. **Build APK**: `./gradlew assembleDebug`
4. **Deploy to device/emulator** for testing
5. **Follow specific testing instructions** in each benchmark's README

## Testing Security Tools

These benchmarks are designed to validate mobile security analysis tools by providing known vulnerabilities that should be detected. Each benchmark includes:

- **Detailed vulnerability description**
- **Build and deployment instructions** 
- **Specific test cases and exploitation steps**
- **Expected findings and success criteria**

## Technology Stack

- **Languages**: Java, Kotlin
- **Platform**: Android (API 31+ recommended)
- **Build System**: Gradle
- **UI Frameworks**: Jetpack Compose, Traditional Android Views
- **Storage**: SQLite, SharedPreferences, File System

## Contributing

When adding new benchmarks, ensure they include:
- Clear vulnerability documentation
- Complete build instructions
- Specific test procedures
- Expected security tool findings

## License

See [LICENSE](LICENSE) file for details.

---

**Note**: These applications contain intentional security vulnerabilities and should only be used in controlled testing environments. Do not deploy these applications in production or on devices containing sensitive data.

# oxo-ios-ben1: Hardcoded Secrets

## Description

Personal Finance Tracker is a comprehensive iOS application that helps users manage their personal finances. The app provides expense tracking, budget management, currency conversion, detailed reports, and cloud synchronization features.

The application contains **multiple hardcoded secrets** embedded naturally within legitimate financial functionality:

- **Currency API Keys**: Hardcoded API keys for real-time currency conversion services
- **Database Encryption Keys**: Hardcoded encryption keys used for local data storage
- **Cloud Sync Credentials**: Hardcoded authentication tokens for cloud synchronization
- **Backup Encryption Keys**: Hardcoded keys exposed in settings and backup functionality
- **API Secret Keys**: Hardcoded credentials for secure API connections
- **Admin Credentials**: Hardcoded administrative passwords

These secrets are distributed across different functional areas of the app, making them blend naturally with the legitimate financial operations rather than appearing as obvious security flaws.

### Vulnerability Type and Category
- **Type:** Hardcoded Secrets/Credentials
- **Category:** Cryptographic Issues / Insecure Data Storage
- **CWE:** CWE-798 (Use of Hard-coded Credentials)

### Difficulty
Medium

## Hardcoded Secrets Locations

The application contains several hardcoded secrets strategically placed within legitimate functionality:

### 1. Currency Service (CurrencyService.swift)
- **API Key**: `fxapi_live_d8b2f4a6e9c7f3e8d1b5c9a2f7e4d6b3`
- **Auth Credentials**: `user:finance_app_2024:key_prod_fx891a2b3c4d5e6f7g8h9i0j`

### 2. Data Manager (DataManager.swift)
- **Encryption Key**: `FT2024_DB_ENCRYPT_KEY_b8d4f7e2a9c1f6b5d3a8e7f4c2b9d6a1`
- **Cloud Sync Token**: `sync_token_ft_prod_9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c`

### 3. Settings (SettingsView.swift)
- **Backup Encryption Key**: `BK_FT2024_SECURE_b8d4f7e2a9c1f6b5d3a8e7f4c2b9d6a1`
- **Master Security Key**: `MASTER_SEC_KEY_ft2024_a1b2c3d4e5f6g7h8i9j0`
- **API Secret Key**: `api_secret_ft_prod_z9y8x7w6v5u4t3s2r1q0p9o8n7m6`
- **Admin Password**: `admin_pass_ft2024_secure`

## Build Instructions

### Prerequisites
- Xcode 15.0 or later
- iOS 17.0+ deployment target
- Swift 5.9+

### Building the App
1. Open `FinanceTracker.xcodeproj` in Xcode
2. Select your target device or simulator
3. Build and run the project (⌘+R)


## Impact Assessment

### Risk Level: HIGH

**Potential Impacts:**
- **Financial Data Exposure**: Unauthorized access to personal financial information
- **API Abuse**: Misuse of hardcoded API keys for unauthorized currency service access
- **Data Decryption**: Ability to decrypt stored financial data using exposed keys
- **Cloud Account Compromise**: Unauthorized access to user's cloud-synced financial data
- **Administrative Access**: Full app control through hardcoded admin credentials

### Attack Scenarios
1. **Static Analysis**: Reverse engineering reveals all hardcoded secrets
2. **Runtime Analysis**: Memory dumps expose active encryption keys
3. **Configuration Extraction**: Backup files reveal encryption parameters
4. **API Key Harvesting**: Extracted keys used for unauthorized service access
5. **Credential Reuse**: Hardcoded passwords potentially reused across systems


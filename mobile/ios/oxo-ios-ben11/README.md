# oxo-ios-ben11: iOS Deeplink CSRF Vulnerability

A modern iOS finance tracking application built with SwiftUI that helps users manage their personal finances, track expenses, and monitor account balances.

## Overview

Finance Tracker is a comprehensive personal finance management app designed for iOS devices. The app provides an intuitive interface for managing multiple accounts, tracking expenses, and transferring money between accounts with seamless user experience.

## Features

### Account Management
- Support for multiple account types (Checking, Savings, Credit Card)
- Real-time balance tracking and updates
- Easy account creation and management
- Visual account overview with icons and balances

### Money Transfers
- Quick transfers between accounts
- Customizable transfer descriptions
- Real-time balance updates
- Transfer history and tracking

### Expense Tracking
- Add and categorize expenses
- View expense history
- Track spending patterns
- Visual expense summaries

### User Settings
- Customizable user profile
- Notification preferences
- Biometric authentication support
- Account statistics and insights

## Technical Specifications

### Requirements
- iOS 17.0 or later
- Xcode 15.0 or later
- Swift 5.9+
- SwiftUI framework

### Architecture
- **Framework**: SwiftUI
- **Data Management**: ObservableObject pattern
- **Storage**: UserDefaults and local data persistence
- **Navigation**: NavigationView and sheet presentations

## Installation

### Prerequisites
- iOS device or simulator (iOS 17.0+)
- Finance Tracker app file (`oxo-ios-ben11.ipa`)
- Xcode for development and testing

### Setup Instructions
1. Clone or download the project source code
2. Open `FinanceTracker.xcodeproj` in Xcode
3. Select your target device or simulator
4. Build and run the project (⌘+R)

### Using the IPA File
1. Install the provided `oxo-ios-ben11.ipa` file
2. Launch the Finance Tracker app
3. Create your first account to get started
4. Begin tracking your finances

## App Structure

### Main Components
- **ContentView**: Primary app interface with tabbed navigation
- **SettingsView**: User preferences and account management
- **MoneyTransferView**: Transfer money between accounts
- **ExpenseEntryView**: Add and track expenses
- **DataManager**: Core business logic and data management

### Key Features
- **Profile Management**: Customize user name and preferences
- **Account Overview**: View all accounts with balances and types
- **Quick Actions**: Easy access to common financial tasks
- **Statistics**: Overview of spending and account totals

## Usage Guide

### Getting Started
1. Launch the Finance Tracker app
2. Set up your user profile in Settings
3. Add your first account with initial balance
4. Start tracking expenses and transfers

### Adding Accounts
1. Navigate to Settings
2. Tap "Add New Account" 
3. Enter account name, type, and initial balance
4. Save to create the new account

### Making Transfers
1. Go to the Transfer tab
2. Select source and destination accounts
3. Enter transfer amount and description
4. Confirm the transfer

### Tracking Expenses
1. Access the Expenses section
2. Add new expense with amount and description
3. View expense history and totals
4. Monitor spending patterns

## App Configuration

### Custom URL Scheme
The app supports deeplink integration with the custom URL scheme:
```
financetracker://
```

This enables external apps and services to integrate with Finance Tracker for seamless money management workflows.

### Settings Options
- **Notifications**: Enable/disable app notifications
- **Biometric Auth**: Use Face ID or Touch ID for security
- **Profile**: Customize user name and information
- **Account Management**: Add, edit, and manage accounts

## Support

For technical support or questions about Finance Tracker:

- **Version**: 1.0.0
- **Support Email**: support@financetracker.com
- **Platform**: iOS 17.0+
- **Framework**: SwiftUI

## Development

### Building from Source
1. Open the project in Xcode
2. Ensure iOS 17.0 SDK is available
3. Select target device or simulator
4. Build and run the project

### Key Development Files
- `FinanceTrackerApp.swift`: Main app entry point
- `DataManager.swift`: Core data management
- `ContentView.swift`: Primary user interface
- `SettingsView.swift`: User preferences
- `Info.plist`: App configuration and permissions

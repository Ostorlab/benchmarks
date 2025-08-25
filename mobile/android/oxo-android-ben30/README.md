# Business Backup & Reports Manager

A comprehensive Android application for small businesses to manage customer data, generate reports, and perform data backup operations.

## Features

- **Dashboard**: Overview of business metrics and quick actions
- **Customer Management**: Add, view, and manage customer information
- **Report Center**: Generate various business reports
- **Backup & Restore**: Secure data backup and restoration functionality
- **Settings**: Application configuration and preferences

## Technical Details

- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Language**: Java
- **Architecture**: AndroidX with modern Android components

## Custom Permissions

The app implements custom permissions to secure data access between different modules:

- `com.ostorlab.businessbackup.permission.READ_CUSTOMER_DATA` - Access to customer information
- `com.ostorlab.businessbackup.permission.WRITE_CUSTOMER_DATA` - Modify customer information
- `com.ostorlab.businessbackup.permission.GENERATE_REPORTS` - Generate business reports
- `com.ostorlab.businessbackup.permission.BACKUP_ACCESS` - Access backup functionality

## Installation

1. Build the project using Gradle
2. Install the APK on your Android device
3. Grant necessary permissions when prompted

## Usage

Launch the app and navigate through the different sections using the main dashboard. The app provides intuitive interfaces for managing business data and generating reports.

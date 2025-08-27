# TaskFlow - Android Benchmark App (ben53)

## Overview
TaskFlow is a modern task management Android application built with Java and Material Design. The app provides a professional interface for creating, managing, and analyzing tasks with a realistic workflow.

## Package Information
- **Package Name**: com.taskflow.ben53
- **Main Launcher Activity**: com.taskflow.ben53.MainActivity
- **App Name**: TaskFlow

## Project Structure
The app contains 4 functional activities with complete navigation flow:

1. **MainActivity** - Task creation interface with title, description, assignee, and priority selection
2. **TaskDetailActivity** - Displays comprehensive task information and provides navigation to editing and analytics
3. **TaskEditorActivity** - Full task editing capabilities including completion status toggle
4. **TaskAnalyticsActivity** - Professional analytics dashboard with performance metrics and risk assessment

## Build Instructions

### Terminal Output Visibility Note
When running commands in the terminal, the output may not be visible. As a workaround, redirect the command's output to a file in `/tmp`, then read the file to view the results.

Example: `./gradlew assembleDebug > /tmp/build.log 2>&1 && tail -n 200 /tmp/build.log`

### Build Commands
1. Navigate to the project directory:
   ```bash
   cd mobile/android/oxo-android-ben53
   ```

2. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

3. Alternative build command with output logging:
   ```bash
   ./gradlew assembleDebug > /tmp/build.log 2>&1 && tail -n 200 /tmp/build.log
   ```

## APK Output
The generated APK is located at:
- `apks/ben53-debug.apk` (final deliverable)
- Original build location: `app/build/outputs/apk/debug/app-debug.apk`

## App Features

### Modern UI Design
- Material Design 3 components with professional blue and orange color scheme
- Card-based layouts for enhanced readability
- Intuitive navigation between screens
- Responsive design with proper spacing and typography

### Task Management Functionality
- **Create Tasks**: Add new tasks with title, description, assignee, and priority levels (Low, Medium, High, Critical)
- **View Details**: Comprehensive task display with all relevant information
- **Edit Tasks**: Modify task properties including completion status
- **Analytics Dashboard**: Professional metrics including performance tracking, risk assessment, and recommendations

### Data Flow
The app demonstrates typical Android data passing patterns using Parcelable objects. Task data flows between activities through Intent extras, showcasing real-world Android development practices.

## Technical Architecture
- **Language**: Java only (no Kotlin)
- **UI Framework**: Material Components for Android
- **Data Passing**: Android Parcelable implementation
- **Build System**: Gradle with modern Android Gradle Plugin
- **Target SDK**: 34 with backward compatibility to API 21

## Installation Verification
The app has been successfully tested with:
- Build process completed without errors
- APK generation confirmed
- ADB installation successful on Android emulator
- All activities accessible through normal app navigation

TaskFlow provides a realistic and professional task management experience suitable for demonstrating modern Android development practices.

# oxo-android-ben29 Implicit Broadcast Sending

## Challenge Details

### Description

This Android app demonstrates a critical security vulnerability in a fitness tracking application:

- **Implicit Broadcast Sending** - The app sends broadcast intents without explicitly specifying the target component or package, allowing any app with matching intent filters to receive sensitive user data including personal information, workout statistics, location data, authentication tokens, and database information.

The app simulates a realistic fitness tracker with complete user authentication and multiple features including user registration/login, workout tracking, statistics viewing, user profiles, nutrition logging, and social sharing. The vulnerability is present throughout the application where sensitive data is broadcast using implicit intents that can be intercepted by malicious applications.

The application includes a comprehensive SQLite database with user authentication, making the implicit broadcasts even more dangerous as they expose real user data, database queries, authentication tokens, and personal health information.

### Vulnerability Type and Category
- **Type:** Information Disclosure / Implicit Broadcast Sending
- **Category:** Improper Input Validation / Privacy Violation
- **CWE:** CWE-200 (Information Exposure), CWE-359 (Exposure of Private Information)

### Difficulty
Medium

## Application Features

### Authentication System
- **User Registration**: Complete signup flow with profile information
- **User Login**: Email/password authentication with session management
- **Database Integration**: SQLite database with proper user isolation
- **Sample User**: Pre-configured test user (email: `john.doe@email.com`, password: `password123`)

### Core Functionality
- **Workout Tracking**: Real-time workout sessions with calorie, distance, and time tracking
- **Statistics Dashboard**: Personal workout statistics and progress tracking
- **User Profile**: Complete user profile management with personal information
- **Nutrition Logging**: Meal tracking and nutritional information
- **Social Features**: Achievement sharing and social feed functionality

### Vulnerability Examples
The app broadcasts sensitive information through implicit intents including:
- User authentication data (login attempts, session tokens, device fingerprints)
- Personal health information (workout data, heart rate, GPS coordinates)
- Database operations (SQL queries, database paths, record IDs)
- Profile information (PII data, insurance numbers, billing addresses)
- Social activities (achievements, friend lists, privacy settings)

## Build Instructions

This project uses Android Studio with Java and modern Android development practices.

Open the project in Android Studio.

Update your SDK versions as required (compileSdkVersion >= 34 recommended).

Build and deploy the app to an emulator or Android device.

### Build from Terminal
```bash
cd src/
./gradlew assembleDebug
```

The APK will be generated in `app/build/outputs/apk/debug/app-debug.apk`
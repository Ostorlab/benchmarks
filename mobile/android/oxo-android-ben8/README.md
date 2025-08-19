# OXO-BEN-008 Random Internal Storage File Access

### Description

This Android application contains a path traversal vulnerability that allows an attacker to read any file, especially in internal storage.  
The vulnerability is located in an exported activity that is used to access a specific note. The activity reads the note name from the intent extra and concatenates it to the notes directory path without sanitization. A malicious application can send a relative path instead of the note name.  

### Difficulty
Easy

### Build Instructions
The app is written in Kotlin.

1. Open the project in Android Studio.  
2. Build and deploy the app to an emulator or Android device.


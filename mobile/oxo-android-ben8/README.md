# OXO-BEN-008 Random Internal Storage File Access

### Description

This Android application contains path travesal vulenrability which enables an attacker to read any file especially in internal storage.
The vulnerability is located in an exported activity that is used to export a specific note. The activity reads the note name extra and concatenates it to the notes directory path without sanitization. A malitious application send a relative path instead of the note name.

### Difficulty
Easy

### Build instructions
The app is written in kotlin.

Open the project in Android Studio.

Build and deploy the app to an emulator or Android device.



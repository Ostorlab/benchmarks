# Contributing to Ostorlab Pentest Benchmarks

This document provides guidelines for contributing to the Ostorlab Pentest Benchmarks repository.

## Android Benchmarks mobile/android/

A typical Android benchmark directory is structured as follows:

```
mobile/android/oxo-android-ben<N>/
├── src/                  # The complete source code for the mobile application.
├── apks/                 # Conatins the compiled apk file.
├── backend/              # (Optional) The backend source code. Should include a Dockerfile file to build the backend image.
├── exploit/              # (Optional) Any scripts for exploitation.
└── README.md             # Benchmark description and details.
```

Each benchmark directory must contain a README.md file detailing the application, the vulnerability, and the exploit. Please refer to one of the existing benchmarks for an example.  
Below is the recommended format for a benchmark README.md:

### Title
Benchmark ID (oxo-android-ben<N>) followed by a concise title that describes the vulnerability.  
For example:  
`# oxo-android-ben36 Intent Redirection + Internal File Access (Path Traversal)`

### Application
A brief description of the application and its intended functionality. Provide context for the vulnerability.  

### Vulnerability Type and Category
- **Type:** List the precise vulnerability type(s) (e.g., Path Traversal, Intent Redirection, Biometric Authentication Bypass).  
- **Category:** Broader security categories the issue belongs to (e.g., Insecure Inter-Component Communication, Authentication and Session Management).  

### Difficulty
Low, Medium, High.

### Backend
Describe if the backend, and how it interacts with the vulnerability.

### Mobile Application
Explain how the mobile app implements the vulnerable logic, for example:
- Which component is exposed or misconfigure
- Which parameters are unsanitized or unchecked
- How these design flaws lead to the vulnerability

### Exploitation
Step-by-step instructions on how to exploit the vulnerability.
Include example `adb` commands or PoC scripts.  

Example:
```bash
adb shell am start -n com.example.app/.VulnerableActivity \
  --es com.example.app.extra.FILE_PATH /sdcard/malicious.txt


# oxo-android-ben5 XSS through intent parameter injection -

## Challenge Details

### Description

This Android app sample demonstrates a critical WebView-based Cross-Site Scripting (XSS) vulnerability through an exported activity:

- The app exposes an exported activity that accepts HTML and URL parameters via intent extras.
- It loads attacker-controlled HTML content with JavaScript enabled inside an internal WebView.
- The WebView runs this malicious code in the context of sensitive domains, potentially leaking authentication cookies and other confidential data.
- This vulnerability allows third-party apps to execute arbitrary JavaScript inside the WebView, leading to cookie theft, session hijacking, and full remote code execution within the app's privileges.

The vulnerability highlights unsafe handling of external HTML and URL input combined with WebView security misconfigurations.

### Vulnerability Type and Category

- **Type:** WebView Injection / Cross-Site Scripting (XSS)
- **Category:** Improper Input Validation, Unsafe Web Content Loading, Insecure WebView Configuration

### Difficulty

Medium

## Build Instructions

This sample project uses Android Studio with Java and AndroidX Fragment APIs.

- Open the project in Android Studio.
- Update your SDK versions as required (compileSdkVersion >= 31 recommended).
- Ensure `AndroidManifest.xml` declares the vulnerable activity as exported.
- Build and deploy the app on an emulator or an Android device.
- Test the vulnerability by launching the exported activity with crafted intents containing malicious HTML and base URLs.



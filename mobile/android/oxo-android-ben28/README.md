# oxo-android-ben28 WebView Hijacking Vulnerability: Account Hijacking via Unvalidated Deeplink -

## Challenge Details

### Description

A WebView Hijacking vulnerability has been identified similar to the one observed in the TikTok Android application. This vulnerability arises due to an unvalidated deeplink that loads an unsanitized parameter into a WebView, exposing a JavaScript interface that can be abused for account hijacking.

The vulnerable app loads URLs received from external deeplinks directly into a WebView without any validation or sanitization of the URL parameters. JavaScript is enabled in the WebView, and a JavaScript interface exposing sensitive functions is added without any protection. This combination allows attackers to inject malicious JavaScript code that calls unsafe methods on the interface, potentially leading to account compromise and data theft.

---

### Vulnerability Type and Category
- **Type:** WebView Hijacking / JavaScript Interface Injection
- **Category:** Remote Code Execution / Account Takeover

---

### Difficulty
Medium

---

### Attack Requirements
- The victim has the vulnerable app installed.
- An attacker can send or persuade the victim to open a malicious deeplink (e.g., via phishing).
- The victim opens the deeplink inside the vulnerable app.


---

### Attack Steps
1. Connect the device via ADB.
2. Run the adb command to execute the activity.


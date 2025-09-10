# oxo-ios-ben13 HTML Injection in WKWebView

## Challenge Details

### Description

DocumentViewerPro is a professional document management iOS application that demonstrates HTML injection vulnerabilities through improper handling of user-controlled content. The app allows users to import and view HTML documents but fails to sanitize user input before rendering it in WKWebView.

The vulnerability occurs when users import malicious HTML content through the app's document creation feature, allowing attackers to inject arbitrary HTML that renders within the trusted app context.

### Vulnerability Type and Category
- **Type:** HTML Injection
- **Category:** Improper Input Validation in WKWebView

### Difficulty
Easy

## Technical Details

### Vulnerable Code Location
- **File:** `DocumentViewerViewController.swift:52`
- **Method:** `loadHTMLContent(_:)`
- **Vulnerable line:** `webView.loadHTMLString(htmlString, baseURL: nil)`

### Attack Vectors

**Document Creation Testing:**
1. **Direct HTML Input:** Import malicious HTML via the content text area
2. **URL Import:** Import malicious content from external URLs
3. **Content Replacement:** Replace legitimate document content with fake interfaces

**Payload Examples:**
- Content manipulation: `<h1 style="color:red">HACKED</h1>`
- Fake forms: `<form><input type="password" placeholder="Enter PIN"><button>Submit</button></form>`
- External content: `<iframe src="https://malicious-site.com"></iframe>`
- UI hijacking: Full-screen fake security dialogs

### Exploitation Method

1. **Create new document** via the + button
2. **Enter malicious HTML** in the content text area
3. **Save document** with legitimate-sounding title
4. **Open document** to see injected content rendered
5. **Social engineering** - share document with victims who trust the app context

### Impact
- Content spoofing and UI manipulation within trusted app context
- Credential harvesting through fake authentication dialogs  
- Social engineering attacks via convincing fake interfaces
- Context deception where malicious content impersonates legitimate app functionality

## Real-World Attack Scenario

**Attack:** Create document titled "Company_Payroll_Q4.xlsx" containing fake PIN entry dialog
**Victim:** Opens document expecting spreadsheet data
**Result:** Sees professional-looking security dialog requesting employee PIN
**Outcome:** Victim enters real PIN thinking it's a legitimate app security feature

The vulnerability enables attackers to create convincing fake interfaces within the trusted app context, leading to successful credential theft and social engineering attacks.
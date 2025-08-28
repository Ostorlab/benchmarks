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
```
# oxo-android-ben40 News Reader App

## Attack Vector 2: Deep Link URL Injection

**Vulnerability**: The app handles custom URL schemes (`newsreader://`) but forwards malicious URLs to WebView components without validation.

**Key Issues**:
- No URL validation in deep link parameters
- Direct forwarding to WebView with JavaScript enabled
- Missing domain allowlisting

**Difficulty**: Medium

## Testing

```bash
# Basic malicious URL injection
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://article?url=https://malicious-site.com"

# Phishing attack
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://redirect?to=https://fake-banking.com"

# JavaScript injection
adb shell am start -W -a android.intent.action.VIEW -d "newsreader://article?url=javascript:alert('XSS')"
```

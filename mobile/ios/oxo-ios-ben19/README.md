# oxo-ios-ben19: iOS Persistent User Tracking via WebView Cache

## Vulnerability Overview

iOS Persistent User Tracking occurs when web applications leverage browser storage mechanisms that persist across sessions and privacy controls to maintain user identification. This vulnerability exploits the window.caches API in WKWebView to track users even when cookies, localStorage, and sessionStorage are blocked by privacy shields or settings.

## Attack Vector: Persistent Cache-Based User Tracking

**Brief Explanation**: A news reader iOS application that loads web content through WKWebView. The vulnerability enables persistent user tracking through the window.caches API, which remains functional even when traditional tracking methods (cookies, localStorage) are disabled by privacy controls, allowing indefinite user identification across sessions.

**Vulnerable Code Pattern:**
```swift
// VULNERABLE: WKWebView without cache controls
class NewsWebViewController: UIViewController {
    @IBOutlet weak var webView: WKWebView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // VULNERABILITY: No cache configuration or privacy controls
        let config = WKWebViewConfiguration()
        // MISSING: Cache policy configuration
        // MISSING: JavaScript bridge security
        // MISSING: Privacy controls for persistent storage
        
        webView = WKWebView(frame: view.bounds, configuration: config)
        view.addSubview(webView)
    }
    
    func loadNewsContent(url: String) {
        // CRITICAL: Loads untrusted web content with full API access
        if let newsURL = URL(string: url) {
            let request = URLRequest(url: newsURL)
            // DANGEROUS: No restrictions on JavaScript APIs
            webView.load(request)
        }
    }
}

// VULNERABLE: No JavaScript API restrictions
class WebViewManager {
    func configureWebView() -> WKWebViewConfiguration {
        let config = WKWebViewConfiguration()
        let userController = WKUserContentController()
        
        // MISSING: window.caches API blocking
        // MISSING: Persistent storage controls
        // MISSING: Cross-origin restrictions
        
        config.userContentController = userController
        return config
    }
    
    // VULNERABLE: Allows persistent tracking JavaScript
    func injectTrackingScript() -> String {
        return """
        async function setTrackingId(trackingId) {
            const cache = await caches.open('user-tracking');
            const response = new Response(JSON.stringify({
                trackingId: trackingId,
                timestamp: Date.now(),
                userAgent: navigator.userAgent,
                screenRes: screen.width + 'x' + screen.height
            }));
            await cache.put('/tracking-data', response);
        }
        
        async function getTrackingId() {
            try {
                const cache = await caches.open('user-tracking');
                const response = await cache.match('/tracking-data');
                if (response) {
                    const data = await response.json();
                    return data.trackingId;
                }
            } catch (e) {
                console.log('Cache access failed');
            }
            return null;
        }
        
        async function trackUser() {
            let trackingId = await getTrackingId();
            if (!trackingId) {
                trackingId = 'user_' + Math.random().toString(36).substr(2, 9);
                await setTrackingId(trackingId);
            }
            
            // Send tracking data to server
            fetch('/api/track', {
                method: 'POST',
                body: JSON.stringify({
                    trackingId: trackingId,
                    page: window.location.href,
                    timestamp: Date.now()
                })
            });
            
            return trackingId;
        }
        
        // Auto-execute tracking
        trackUser();
        """
    }
}

// VULNERABLE: No privacy controls
class NewsReaderSettings {
    var blockCookies: Bool = true
    var blockLocalStorage: Bool = true
    var blockSessionStorage: Bool = true
    // MISSING: Block window.caches option
    // MISSING: Clear persistent cache option
    
    func applyPrivacySettings() {
        // VULNERABILITY: Only blocks traditional storage, not window.caches
        let websiteDataTypes = WKWebsiteDataStore.allWebsiteDataTypes()
        WKWebsiteDataStore.default().removeData(
            ofTypes: [WKWebsiteDataTypeCookies, WKWebsiteDataTypeLocalStorage],
            modifiedSince: Date.distantPast
        ) { }
        
        // MISSING: WKWebsiteDataTypeServiceWorkerRegistrations
        // MISSING: Cache API data removal
    }
}
```

**Attack Payload Examples:**
```javascript
// Persistent user fingerprinting
await caches.open('fingerprint').then(cache => {
    cache.put('/user-data', new Response(JSON.stringify({
        id: generateUniqueId(),
        screen: screen.width + 'x' + screen.height,
        timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
        language: navigator.language
    })));
});

// Cross-session behavior tracking
await caches.open('behavior').then(cache => {
    cache.put('/activity', new Response(JSON.stringify({
        articles_read: getReadArticles(),
        time_spent: getTotalTimeSpent(),
        preferences: getUserPreferences()
    })));
});

// Persistent advertising ID
await caches.open('ads').then(cache => {
    cache.put('/ad-profile', new Response(JSON.stringify({
        interests: extractedInterests,
        demographics: inferredDemographics,
        purchase_intent: behaviorAnalysis
    })));
});
```

**Difficulty**: Easy

## Testing

```bash
# Install oxo-ios-ben19.ipa on target iOS device

# Method 1: Test persistent tracking in app
# 1. Open NewsReader app
# 2. Visit news article that sets tracking ID via window.caches
# 3. Note the tracking ID displayed
# 4. Enable all privacy settings (block cookies, localStorage)
# 5. Force close and restart app
# 6. Revisit same article - tracking ID should persist

# Method 2: Verify cache persistence
# Open Safari Developer Tools or use proxy
curl -X GET "https://news-app.com/api/test-tracking" \
  -H "User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 15_6 like Mac OS X)"

# Method 3: Test JavaScript injection
cat > tracking_test.js << 'EOF'
// Test window.caches availability
if ('caches' in window) {
    console.log('✓ window.caches available - VULNERABLE');
    
    // Set persistent tracking
    caches.open('test-tracking').then(cache => {
        cache.put('/test', new Response('persistent-data'));
        console.log('✓ Cache write successful');
    });
    
    // Verify persistence
    caches.open('test-tracking').then(cache => {
        cache.match('/test').then(response => {
            if (response) {
                console.log('✓ Persistent tracking confirmed - VULNERABLE');
            }
        });
    });
} else {
    console.log('✗ window.caches not available');
}
EOF

# Verify vulnerability
echo "Expected: Tracking ID persists across app restarts"
echo "Expected: Privacy settings don't clear window.caches"
echo "Expected: No user warning about persistent tracking"
```

## Application Features

### Core Functionality
1. **News Feed**: Curated articles, breaking news alerts, and personalized content recommendations
2. **Article Reader**: Full-text articles with reading progress, bookmarks, and offline reading
3. **Categories**: Politics, Technology, Sports, Entertainment, and customizable topic filters
4. **Search**: Article search, trending topics, and historical news archives
5. **Social Sharing**: Share articles, comment threads, and social media integration
6. **Personalization**: Reading history, preferred topics, and content recommendations
7. **Notifications**: Breaking news alerts, topic updates, and reading reminders

### Vulnerable WebView Areas
- `NewsWebViewController` - Main article viewing with WKWebView
- `WebViewManager` - WebView configuration and JavaScript injection
- `CacheManager` - Browser cache and storage management
- `PrivacySettingsController` - User privacy controls and data clearing

### Persistent Tracking Scenarios
- **User Fingerprinting**: Device characteristics, screen resolution, timezone tracking
- **Reading Behavior**: Article preferences, reading speed, engagement patterns
- **Cross-Session Analytics**: Return visitor identification across app launches
- **Advertising Profiles**: Interest targeting based on reading history
- **A/B Testing**: Persistent user segmentation for feature experiments

### Information Disclosed
- Unique persistent user identifier across sessions
- Reading habits and content preferences
- Device characteristics and browser fingerprint
- Session duration and engagement metrics
- Cross-visit behavior correlation and user journey mapping

## Application Theme: Modern News Reader

**Visual Design**: Clean, minimalist news aggregation interface with:
- **Color Scheme**: White/light gray background with blue accent colors for links and buttons
- **Typography**: System fonts with clear hierarchy (headlines, subheadlines, body text)
- **Layout**: Card-based article display with thumbnail images and preview text
- **Navigation**: Tab-based categories (Breaking, Technology, Sports, Politics, Entertainment)

**User Experience**:
- **Home Screen**: Scrollable feed of latest articles with category filters
- **Article View**: Full-screen WebView for reading complete articles
- **Settings**: Privacy controls, notification preferences, and reading preferences
- **Search**: Quick article search with trending topics

## Application Structure

```
src/NewsReader/
├── NewsReader.xcodeproj/
│   └── project.pbxproj
└── NewsReader/
    ├── NewsReaderApp.swift          # SwiftUI app entry point
    ├── ContentView.swift            # Main news feed interface
    ├── NewsWebView.swift            # WebView wrapper (VULNERABLE)
    ├── TrackingService.swift        # Persistent tracking logic (CRITICAL)
    ├── Assets.xcassets/             # App icons and images
    │   ├── AppIcon.appiconset/
    │   └── AccentColor.colorset/
    └── Preview Content/
        └── Preview Assets.xcassets/
```

**Key Vulnerable Components**:
1. **NewsWebView.swift**: Contains WKWebView that loads articles with injected tracking JavaScript
2. **TrackingService.swift**: Implements persistent tracking via window.caches API
3. **ContentView.swift**: Main interface that integrates WebView with tracking functionality
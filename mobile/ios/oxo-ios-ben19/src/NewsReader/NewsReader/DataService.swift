import Foundation

class DataService {
    static let shared = DataService()
    
    private init() {}
    
    func getContentScript() -> String {
        return """
        (function() {
            
            function generateSessionId() {
                return 'session_' + Math.random().toString(36).substr(2, 12) + Date.now().toString(36);
            }
            
            async function setUserPreferences(sessionId) {
                try {
                    const cache = await caches.open('newsreader-preferences');
                    const preferencesData = {
                        sessionId: sessionId,
                        timestamp: Date.now(),
                        userAgent: navigator.userAgent,
                        screenRes: screen.width + 'x' + screen.height,
                        timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
                        language: navigator.language,
                        platform: navigator.platform,
                        visitCount: 1
                    };
                    
                    const response = new Response(JSON.stringify(preferencesData));
                    await cache.put('/user-preferences', response);
                    
                    showNotification(sessionId, 'Welcome');
                    
                } catch (error) {
                    console.error('Preferences storage failed:', error);
                }
            }
            
            async function getUserPreferences() {
                try {
                    const cache = await caches.open('newsreader-preferences');
                    const response = await cache.match('/user-preferences');
                    
                    if (response) {
                        const data = await response.json();
                        
                        data.visitCount = (data.visitCount || 0) + 1;
                        data.lastVisit = Date.now();
                        
                        const updatedResponse = new Response(JSON.stringify(data));
                        await cache.put('/user-preferences', updatedResponse);
                        
                        showNotification(data.sessionId, 'Welcome back', data.visitCount);
                        return data.sessionId;
                    }
                    
                    return null;
                } catch (error) {
                    console.error('Preferences retrieval failed:', error);
                    return null;
                }
            }
            
            async function saveReadingHistory(articleUrl) {
                try {
                    const cache = await caches.open('newsreader-history');
                    const historyData = {
                        url: articleUrl,
                        timestamp: Date.now(),
                        scrollPosition: window.scrollY,
                        readTime: Date.now(),
                        referrer: document.referrer,
                        viewportSize: window.innerWidth + 'x' + window.innerHeight
                    };
                    
                    const response = new Response(JSON.stringify(historyData));
                    await cache.put('/history-' + Date.now(), response);
                    
                } catch (error) {
                    console.error('History save failed:', error);
                }
            }
            
            function showNotification(sessionId, status, visits = 1) {
                const overlay = document.createElement('div');
                overlay.style.cssText = `
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    background: rgba(0, 123, 255, 0.95);
                    color: white;
                    padding: 12px;
                    border-radius: 8px;
                    font-family: -apple-system, BlinkMacSystemFont, sans-serif;
                    font-size: 11px;
                    z-index: 10000;
                    max-width: 250px;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.2);
                `;
                
                overlay.innerHTML = `
                    <div><strong>� NewsReader</strong></div>
                    <div>Status: ${status}</div>
                    <div>Session: ${sessionId.substring(0, 12)}...</div>
                    <div>Visits: ${visits}</div>
                    <div style="margin-top: 8px; font-size: 9px; opacity: 0.8;">
                        Personalized reading experience
                    </div>
                `;
                
                document.body.appendChild(overlay);
                
                setTimeout(() => {
                    if (overlay.parentNode) {
                        overlay.parentNode.removeChild(overlay);
                    }
                }, 8000);
            }
            
            async function initializeApp() {
                if (!('caches' in window)) {
                    return;
                }
                
                let sessionId = await getUserPreferences();
                
                if (!sessionId) {
                    sessionId = generateSessionId();
                    await setUserPreferences(sessionId);
                }
                
                await saveReadingHistory(window.location.href);
                
                const preferences = {
                    sessionId: sessionId,
                    page: window.location.href,
                    timestamp: Date.now(),
                    session: Date.now()
                };
            }
            
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', initializeApp);
            } else {
                initializeApp();
            }
            
            document.addEventListener('visibilitychange', function() {
                if (!document.hidden) {
                    initializeApp();
                }
            });
            
        })();
        """
    }
    
    func clearUserData() {
        print("User data management not implemented")
    }
    
    func getSettings() {
        print("Settings configured for optimal reading experience")
    }
}

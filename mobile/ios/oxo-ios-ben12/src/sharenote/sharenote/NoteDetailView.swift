// File: NoteDetailView.swift
import SwiftUI
import WebKit

struct WebView: UIViewRepresentable {
    let htmlString: String
    
    func makeUIView(context: Context) -> WKWebView {
        let configuration = WKWebViewConfiguration()
        
        // VULNERABILITY: Allow file:// access for SSRF demonstration
        configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        configuration.setValue(true, forKey: "allowUniversalAccessFromFileURLs")
       configuration.preferences.javaScriptEnabled = true
        
        // Inject JavaScript to block network requests
        let blockNetworkScript = """
        // Block XMLHttpRequest
        const originalXHR = window.XMLHttpRequest;
        window.XMLHttpRequest = function() {
            const xhr = new originalXHR();
            const originalOpen = xhr.open;
            xhr.open = function(method, url, ...args) {
                if (url.startsWith('http://') || url.startsWith('https://')) {
                    console.log('🚫 BLOCKED XHR request to:', url);
                    throw new Error('Network requests are blocked');
                }
                return originalOpen.apply(this, [method, url, ...args]);
            };
            return xhr;
        };
        
        // Block fetch
        const originalFetch = window.fetch;
        window.fetch = function(url, ...args) {
            if (typeof url === 'string' && (url.startsWith('http://') || url.startsWith('https://'))) {
                console.log('🚫 BLOCKED fetch request to:', url);
                return Promise.reject(new Error('Network requests are blocked'));
            }
            return originalFetch.apply(this, [url, ...args]);
        };
        
        // Block image loading
        const originalImage = window.Image;
        window.Image = function() {
            const img = new originalImage();
            const originalSrcSetter = Object.getOwnPropertyDescriptor(HTMLImageElement.prototype, 'src').set;
            Object.defineProperty(img, 'src', {
                set: function(value) {
                    if (value.startsWith('http://') || value.startsWith('https://')) {
                        console.log('🚫 BLOCKED image request to:', value);
                        // Trigger onerror immediately
                        setTimeout(() => {
                            if (this.onerror) this.onerror();
                        }, 0);
                        return;
                    }
                    originalSrcSetter.call(this, value);
                },
                get: function() {
                    return this.getAttribute('src');
                }
            });
            return img;
        };
        
        // Block existing images
        document.addEventListener('DOMContentLoaded', function() {
            const images = document.querySelectorAll('img');
            images.forEach(img => {
                const src = img.src;
                if (src.startsWith('http://') || src.startsWith('https://')) {
                    console.log('🚫 BLOCKED existing image:', src);
                    img.removeAttribute('src');
                    if (img.onerror) img.onerror();
                }
            });
        });
        
        console.log('✅ Network blocking script injected');
        """
        
        let userScript = WKUserScript(source: blockNetworkScript, injectionTime: .atDocumentStart, forMainFrameOnly: false)
        configuration.userContentController.addUserScript(userScript)
        
        let webView = WKWebView(frame: .zero, configuration: configuration)
        webView.navigationDelegate = context.coordinator
        webView.uiDelegate = context.coordinator
        
        return webView
    }
    
    func updateUIView(_ uiView: WKWebView, context: Context) {
        // Get the app's Documents directory for baseURL
        guard let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else {
            // Fallback to loading without baseURL
            let header = "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            let fullHTML = header + htmlString
            uiView.loadHTMLString(fullHTML, baseURL: nil)
            return
        }
        
        // VULNERABILITY: Load HTML with Documents directory as baseURL to enable file:// access
        let header = "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
        let fullHTML = header + htmlString
        uiView.loadHTMLString(fullHTML, baseURL: documentsDirectory)
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator()
    }
    
    class Coordinator: NSObject, WKNavigationDelegate, WKUIDelegate {
        // SECURITY FIX: Block network requests to disable old vulnerability
        func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
            
            guard let url = navigationAction.request.url else {
                print("🚫 BLOCKED: No URL in request")
                decisionHandler(.cancel)
                return
            }
            
            print("🔍 Navigation request: \(url.absoluteString)")
            
            // Allow file:// URLs for SSRF demonstration
            if url.scheme == "file" {
                print("🔓 SSRF: Allowing file:// access to: \(url.path)")
                decisionHandler(.allow)
                return
            }
            
            // Allow about: and data: schemes for basic HTML functionality
            if url.scheme == "about" || url.scheme == "data" {
                print("✅ ALLOWED: Basic scheme \(url.scheme ?? "unknown")")
                decisionHandler(.allow)
                return
            }
            
            // SECURITY FIX: Block all network requests (http, https, etc.)
            if url.scheme == "http" || url.scheme == "https" {
                print("🚫 BLOCKED: Network navigation to \(url.absoluteString)")
                decisionHandler(.cancel)
                return
            }
            
            // Block any other schemes
            print("🚫 BLOCKED: Unknown scheme \(url.scheme ?? "nil") for URL: \(url.absoluteString)")
            decisionHandler(.cancel)
        }
        
        // Also block resource loading (images, scripts, etc.)
        func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
            guard let url = navigationResponse.response.url else {
                decisionHandler(.cancel)
                return
            }
            
            // Block network responses
            if url.scheme == "http" || url.scheme == "https" {
                print("🚫 BLOCKED: Network response from \(url.absoluteString)")
                decisionHandler(.cancel)
                return
            }
            
            decisionHandler(.allow)
        }
        
        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            print("✅ WebView finished loading content")
        }
        
        func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
            print("❌ WebView navigation failed: \(error.localizedDescription)")
        }
        
        // Block JavaScript alerts/confirms that might be used for exfiltration
        func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
            print("🚫 BLOCKED: JavaScript alert with message: \(message)")
            completionHandler()
        }
    }
}

struct NoteDetailView: View {
    let note: Note
    var body: some View {
        WebView(htmlString: note.content)
            .navigationTitle(note.title)
            .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    NavigationView {
        NoteDetailView(note: Note(title: "Test", content: "<h1>Test</h1>"))
    }
}

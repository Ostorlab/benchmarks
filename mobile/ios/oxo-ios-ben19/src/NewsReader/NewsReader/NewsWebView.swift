import SwiftUI
import WebKit

struct NewsWebView: UIViewRepresentable {
    let url: String
    @Environment(\.dismiss) var dismiss
    
    func makeUIView(context: Context) -> WKWebView {
        let config = WKWebViewConfiguration()
        let userController = WKUserContentController()
        
        let contentScript = DataService.shared.getContentScript()
        let userScript = WKUserScript(source: contentScript, 
                                    injectionTime: .atDocumentEnd, 
                                    forMainFrameOnly: false)
        userController.addUserScript(userScript)
        
        config.userContentController = userController
        
        let webView = WKWebView(frame: .zero, configuration: config)
        webView.navigationDelegate = context.coordinator
        
        return webView
    }
    
    func updateUIView(_ webView: WKWebView, context: Context) {
        if let webURL = URL(string: url) {
            let request = URLRequest(url: webURL)
            webView.load(request)
        }
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, WKNavigationDelegate {
        let parent: NewsWebView
        
        init(_ parent: NewsWebView) {
            self.parent = parent
        }
        
        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            webView.evaluateJavaScript("document.title") { result, error in
                if let title = result as? String {
                    
                }
            }
        }
    }
}

struct NewsWebViewContainer: View {
    let url: String
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        NavigationView {
            NewsWebView(url: url)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button("Done") {
                            dismiss()
                        }
                    }
                }
        }
    }
}

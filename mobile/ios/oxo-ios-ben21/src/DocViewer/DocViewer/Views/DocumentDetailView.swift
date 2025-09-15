//
//  DocumentDetailView.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import SwiftUI
import WebKit

struct DocumentDetailView: View {
    let document: Document
    @Environment(\.presentationMode) var presentationMode
    @StateObject private var webViewManager = WebViewManager()
    @State private var webView: WKWebView?
    @State private var showingShareSheet = false
    
    var body: some View {
        NavigationView {
            VStack {
                // Document Info Header
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Image(systemName: document.iconName)
                            .font(.title)
                            .foregroundColor(document.iconColor)
                        
                        VStack(alignment: .leading) {
                            Text(document.name)
                                .font(.headline)
                                .lineLimit(2)
                            
                            Text("by \(document.author)")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                        
                        Spacer()
                        
                        VStack(alignment: .trailing) {
                            Text(document.formattedSize)
                                .font(.caption)
                                .foregroundColor(.secondary)
                            
                            Text(document.formattedDate)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    // Tags
                    if !document.tags.isEmpty {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 8) {
                                ForEach(document.tags, id: \.self) { tag in
                                    Text(tag)
                                        .font(.caption)
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 4)
                                        .background(Color.blue.opacity(0.1))
                                        .foregroundColor(.blue)
                                        .cornerRadius(6)
                                }
                            }
                            .padding(.horizontal)
                        }
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                
                // WebView Content
                ZStack {
                    if let webView = webView {
                        WebViewRepresentable(webView: webView, webViewManager: webViewManager)
                    } else {
                        VStack(spacing: 20) {
                            ProgressView()
                            Text("Loading document...")
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    if webViewManager.isLoading {
                        VStack {
                            ProgressView()
                            Text("Loading...")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(Color(.systemBackground).opacity(0.8))
                    }
                }
                
                // Navigation Controls
                if let webView = webView {
                    HStack(spacing: 20) {
                        Button(action: {
                            webView.goBack()
                        }) {
                            Image(systemName: "chevron.left")
                                .font(.title2)
                        }
                        .disabled(!webViewManager.canGoBack)
                        
                        Button(action: {
                            webView.goForward()
                        }) {
                            Image(systemName: "chevron.right")
                                .font(.title2)
                        }
                        .disabled(!webViewManager.canGoForward)
                        
                        Spacer()
                        
                        Button(action: {
                            webView.reload()
                        }) {
                            Image(systemName: "arrow.clockwise")
                                .font(.title2)
                        }
                        
                        Button(action: {
                            showingShareSheet = true
                        }) {
                            Image(systemName: "square.and.arrow.up")
                                .font(.title2)
                        }
                    }
                    .padding()
                    .background(Color(.systemGray6))
                }
            }
            .navigationTitle("Document Viewer")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                leading: Button("Done") {
                    presentationMode.wrappedValue.dismiss()
                },
                trailing: Button(action: {
                    showingShareSheet = true
                }) {
                    Image(systemName: "square.and.arrow.up")
                }
            )
            .onAppear {
                setupWebView()
            }
        }
        .sheet(isPresented: $showingShareSheet) {
            if let url = document.url {
                ActivityView(activityItems: [url])
            }
        }
    }
    
    private func setupWebView() {
        let webView = webViewManager.createWebView()
        self.webView = webView
        
        // Load document content based on type
        switch document.type {
        case .html:
            if let url = document.url {
                loadHTMLFromURL(url: url, in: webView)
            } else {
                loadDocumentPreview(in: webView)
            }
        case .pdf:
            if let url = document.url {
                webViewManager.loadDocument(url: url, in: webView)
            } else {
                loadPDFPreview(in: webView)
            }
        default:
            loadDocumentPreview(in: webView)
        }
    }
    

    
    private func loadPDFPreview(in webView: WKWebView) {
        let htmlContent = """
        <html>
        <head>
            <style>
                body { font-family: -apple-system, BlinkMacSystemFont, sans-serif; margin: 20px; text-align: center; }
                .pdf-icon { font-size: 80px; color: #dc3545; margin: 40px 0; }
                .info { background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; }
            </style>
        </head>
        <body>
            <div class="pdf-icon">📄</div>
            <h1>\(document.name)</h1>
            <div class="info">
                <p><strong>Document Type:</strong> PDF</p>
                <p><strong>Size:</strong> \(document.formattedSize)</p>
                <p><strong>Author:</strong> \(document.author)</p>
                <p><strong>Modified:</strong> \(document.formattedDate)</p>
            </div>
            <p>PDF preview would be displayed here in a production environment.</p>
        </body>
        </html>
        """
        
        webViewManager.loadHTML(content: htmlContent, in: webView)
    }
    
    private func loadDocumentPreview(in webView: WKWebView) {
        let htmlContent = """
        <html>
        <head>
            <style>
                body { font-family: -apple-system, BlinkMacSystemFont, sans-serif; margin: 20px; text-align: center; }
                .doc-icon { font-size: 80px; margin: 40px 0; }
                .info { background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; }
            </style>
        </head>
        <body>
            <div class="doc-icon">\(documentIcon)</div>
            <h1>\(document.name)</h1>
            <div class="info">
                <p><strong>Type:</strong> \(document.type.displayName)</p>
                <p><strong>Size:</strong> \(document.formattedSize)</p>
                <p><strong>Author:</strong> \(document.author)</p>
                <p><strong>Modified:</strong> \(document.formattedDate)</p>
            </div>
            <p>Document preview for \(document.type.displayName) files.</p>
        </body>
        </html>
        """
        
        webViewManager.loadHTML(content: htmlContent, in: webView)
    }
    
    private func loadHTMLFromURL(url: URL, in webView: WKWebView) {
        if url.scheme == "data" {
            // Handle data URLs - extract HTML content and load with JavaScript bridge
            let urlString = url.absoluteString
            if let htmlContent = extractHTMLFromDataURL(urlString) {
                webViewManager.loadHTML(content: htmlContent, in: webView)
            } else {
                // Fallback to loading the data URL directly
                webViewManager.loadDocument(url: url, in: webView)
            }
        } else if url.scheme == "file" {
            // Handle local file URLs - read file content and load with JavaScript bridge
            do {
                let htmlContent = try String(contentsOf: url, encoding: .utf8)
                webViewManager.loadHTML(content: htmlContent, in: webView)
            } catch {
                print("Error reading local HTML file: \(error.localizedDescription)")
                // Fallback to loading URL directly if file reading fails
                webViewManager.loadDocument(url: url, in: webView)
            }
        } else {
            // For any other URL schemes, fallback to direct loading
            webViewManager.loadDocument(url: url, in: webView)
        }
    }
    
    private func extractHTMLFromDataURL(_ dataURL: String) -> String? {
        // Parse data URL format: data:[<mediatype>][;base64],<data>
        guard dataURL.hasPrefix("data:") else { return nil }
        
        let components = dataURL.dropFirst(5) // Remove "data:"
        if let commaIndex = components.firstIndex(of: ",") {
            let dataContent = String(components[components.index(after: commaIndex)...])
            
            // Handle base64 encoded data
            if components.prefix(upTo: commaIndex).contains("base64") {
                guard let decodedData = Data(base64Encoded: dataContent),
                      let htmlContent = String(data: decodedData, encoding: .utf8) else {
                    return nil
                }
                return htmlContent
            } else {
                // Handle URL-encoded data
                return dataContent.removingPercentEncoding
            }
        }
        
        return nil
    }
    
    private var documentIcon: String {
        switch document.type {
        case .word: return "📝"
        case .excel: return "📊"
        case .powerpoint: return "📽️"
        case .image: return "🖼️"
        case .text: return "📄"
        default: return "📄"
        }
    }
}

struct WebViewRepresentable: UIViewRepresentable {
    let webView: WKWebView
    let webViewManager: WebViewManager
    
    func makeUIView(context: Context) -> WKWebView {
        return webView
    }
    
    func updateUIView(_ uiView: WKWebView, context: Context) {
        // Updates handled by WebViewManager
    }
}

struct ActivityView: UIViewControllerRepresentable {
    let activityItems: [Any]
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

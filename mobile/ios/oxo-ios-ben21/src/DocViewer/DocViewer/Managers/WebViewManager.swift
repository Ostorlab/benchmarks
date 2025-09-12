//
//  WebViewManager.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import Foundation
import WebKit
import SwiftUI
import Combine

class WebViewManager: NSObject, ObservableObject {
    @Published var isLoading = false
    @Published var canGoBack = false
    @Published var canGoForward = false
    @Published var currentURL: String = ""
    
    private weak var currentWebView: WKWebView?
    
    func createWebView() -> WKWebView {
        let configuration = WKWebViewConfiguration()
        
        let contentController = WKUserContentController()
        contentController.add(self, name: "documentHandler")
        contentController.add(self, name: "fileManager")
        contentController.add(self, name: "cloudSync")
        contentController.add(self, name: "authBridge")
        contentController.add(self, name: "deviceManager")
        
        configuration.userContentController = contentController
        configuration.allowsInlineMediaPlayback = true
        configuration.mediaTypesRequiringUserActionForPlayback = []
        
        let webView = WKWebView(frame: .zero, configuration: configuration)
        webView.navigationDelegate = self
        webView.allowsBackForwardNavigationGestures = true
        
        self.currentWebView = webView
        
        return webView
    }
    
    func loadDocument(url: URL, in webView: WKWebView) {
        let request = URLRequest(url: url)
        webView.load(request)
    }
    
    func loadHTML(content: String, in webView: WKWebView) {
        webView.loadHTMLString(content, baseURL: nil)
    }
}

extension WebViewManager: WKNavigationDelegate {
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        isLoading = true
        currentURL = webView.url?.absoluteString ?? ""
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        isLoading = false
        canGoBack = webView.canGoBack
        canGoForward = webView.canGoForward
        currentURL = webView.url?.absoluteString ?? ""
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        isLoading = false
    }
}

extension WebViewManager: WKScriptMessageHandler {
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        guard let body = message.body as? [String: Any] else { return }
        
        switch message.name {
        case "documentHandler":
            handleDocumentMessage(body)
        case "fileManager":
            handleFileManagerMessage(body)
        case "cloudSync":
            handleCloudSyncMessage(body)
        case "authBridge":
            handleAuthMessage(body)
        case "deviceManager":
            handleDeviceMessage(body)
        default:
            break
        }
    }
    
    private func handleDocumentMessage(_ body: [String: Any]) {
        guard let action = body["action"] as? String else { return }
        
        switch action {
        case "saveDocument":
            if let name = body["name"] as? String,
               let content = body["content"] as? String {
                saveDocumentToLocal(name: name, content: content)
            }
        case "shareDocument":
            if let documentId = body["documentId"] as? String {
                shareDocument(id: documentId)
            }
        case "getDocumentList":
            sendDocumentList()
        default:
            break
        }
    }
    
    private func handleFileManagerMessage(_ body: [String: Any]) {
        guard let action = body["action"] as? String else { return }
        
        switch action {
        case "readFile":
            if let path = body["path"] as? String {
                readFileFromPath(path)
            }
        case "writeFile":
            if let path = body["path"] as? String,
               let content = body["content"] as? String {
                writeFileToPath(path: path, content: content)
            }
        case "listDirectory":
            if let path = body["path"] as? String {
                listDirectoryContents(path)
            }
        default:
            break
        }
    }
    
    private func handleCloudSyncMessage(_ body: [String: Any]) {
        guard let action = body["action"] as? String else { return }
        
        switch action {
        case "uploadDocument":
            if let documentData = body["data"] as? String {
                uploadToCloud(data: documentData)
            }
        case "downloadDocument":
            if let documentId = body["id"] as? String {
                downloadFromCloud(id: documentId)
            }
        case "syncFolder":
            if let folderId = body["folderId"] as? String {
                syncCloudFolder(id: folderId)
            }
        default:
            break
        }
    }
    
    private func handleAuthMessage(_ body: [String: Any]) {
        guard let action = body["action"] as? String else { return }
        
        switch action {
        case "getCurrentUser":
            sendCurrentUserInfo()
        case "getAuthToken":
            sendAuthToken()
        case "refreshToken":
            refreshAuthToken()
        default:
            break
        }
    }
    
    private func handleDeviceMessage(_ body: [String: Any]) {
        guard let action = body["action"] as? String else { return }
        
        switch action {
        case "getDeviceInfo":
            sendDeviceInfo()
        case "scanDocument":
            initiateDocumentScan()
        case "shareToDevice":
            if let content = body["content"] as? String {
                shareToDevice(content: content)
            }
        default:
            break
        }
    }
    
    private func saveDocumentToLocal(name: String, content: String) {
        let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let fileURL = documentsPath.appendingPathComponent(name)
        
        do {
            try content.write(to: fileURL, atomically: true, encoding: .utf8)
            let document = Document(
                name: name,
                type: .html,
                size: Int64(content.count),
                dateCreated: Date(),
                dateModified: Date(),
                author: AuthenticationManager.shared.currentUser?.fullName ?? "Unknown",
                url: fileURL,
                isShared: false,
                tags: ["saved", "local"],
                isFavorite: false
            )
            DocumentManager.shared.addDocument(document)
        } catch {
            print("Error saving document: \(error)")
        }
    }
    
    private func shareDocument(id: String) {
        print("Sharing document: \(id)")
    }
    
    private func sendDocumentList() {
        let documents = DocumentManager.shared.documents
        let documentData = documents.map { doc in
            [
                "id": doc.id.uuidString,
                "name": doc.name,
                "type": doc.type.rawValue,
                "size": doc.size,
                "author": doc.author
            ]
        }
        
        let script = "if(window.receiveDocumentList) window.receiveDocumentList(\(jsonString(from: documentData)))"
        DispatchQueue.main.async {
            if let webView = self.currentWebView {
                webView.evaluateJavaScript(script, completionHandler: nil)
            }
        }
    }
    
    private func readFileFromPath(_ path: String) {
        let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let fileURL = documentsPath.appendingPathComponent(path)
        
        do {
            let content = try String(contentsOf: fileURL, encoding: .utf8)
            let script = "if(window.receiveFileContent) window.receiveFileContent('\(content.replacingOccurrences(of: "'", with: "\\'"))')"
            DispatchQueue.main.async {
                if let webView = self.currentWebView {
                    webView.evaluateJavaScript(script, completionHandler: nil)
                }
            }
        } catch {
            print("Error reading file: \(error)")
        }
    }
    
    private func writeFileToPath(path: String, content: String) {
        let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let fileURL = documentsPath.appendingPathComponent(path)
        
        do {
            try content.write(to: fileURL, atomically: true, encoding: .utf8)
        } catch {
            print("Error writing file: \(error)")
        }
    }
    
    private func listDirectoryContents(_ path: String) {
        let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let directoryURL = documentsPath.appendingPathComponent(path)
        
        do {
            let contents = try FileManager.default.contentsOfDirectory(at: directoryURL, includingPropertiesForKeys: nil)
            let fileList = contents.map { $0.lastPathComponent }
            let script = "if(window.receiveDirectoryListing) window.receiveDirectoryListing(\(jsonString(from: fileList)))"
            DispatchQueue.main.async {
                if let webView = self.currentWebView {
                    webView.evaluateJavaScript(script, completionHandler: nil)
                }
            }
        } catch {
            print("Error listing directory: \(error)")
        }
    }
    
    private func uploadToCloud(data: String) {
        print("Uploading to cloud: \(data.prefix(50))...")
    }
    
    private func downloadFromCloud(id: String) {
        print("Downloading from cloud: \(id)")
    }
    
    private func syncCloudFolder(id: String) {
        print("Syncing cloud folder: \(id)")
    }
    
    private func sendCurrentUserInfo() {
        guard let user = AuthenticationManager.shared.currentUser else { return }
        
        let userInfo = [
            "id": user.id.uuidString,
            "email": user.email,
            "fullName": user.fullName,
            "company": user.company,
            "plan": user.plan.rawValue
        ]
        
        let script = "if(window.receiveUserInfo) window.receiveUserInfo(\(jsonString(from: userInfo)))"
        DispatchQueue.main.async {
            if let webView = self.currentWebView {
                webView.evaluateJavaScript(script, completionHandler: nil)
            }
        }
    }
    
    private func sendAuthToken() {
        let token = UserDefaults.standard.string(forKey: "auth_token") ?? ""
        let script = "if(window.receiveAuthToken) window.receiveAuthToken('\(token)')"
        DispatchQueue.main.async {
            if let webView = self.currentWebView {
                webView.evaluateJavaScript(script, completionHandler: nil)
            }
        }
    }
    
    private func refreshAuthToken() {
        let newToken = "refreshed_token_\(UUID().uuidString)"
        UserDefaults.standard.set(newToken, forKey: "auth_token")
        let script = "window.receiveAuthToken('\(newToken)')"
        DispatchQueue.main.async {
            // Would execute JavaScript in webview
        }
    }
    
    private func sendDeviceInfo() {
        let deviceInfo = [
            "model": UIDevice.current.model,
            "systemName": UIDevice.current.systemName,
            "systemVersion": UIDevice.current.systemVersion,
            "identifier": UIDevice.current.identifierForVendor?.uuidString ?? ""
        ]
        
        let script = "if(window.receiveDeviceInfo) window.receiveDeviceInfo(\(jsonString(from: deviceInfo)))"
        DispatchQueue.main.async {
            if let webView = self.currentWebView {
                webView.evaluateJavaScript(script, completionHandler: nil)
            }
        }
    }
    
    private func initiateDocumentScan() {
        print("Initiating document scan")
    }
    
    private func shareToDevice(content: String) {
        print("Sharing to device: \(content)")
    }
    
    private func jsonString(from object: Any) -> String {
        guard let data = try? JSONSerialization.data(withJSONObject: object),
              let string = String(data: data, encoding: .utf8) else {
            return "{}"
        }
        return string
    }
}

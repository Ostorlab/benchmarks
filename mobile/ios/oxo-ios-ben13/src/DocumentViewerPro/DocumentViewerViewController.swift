//
//  DocumentViewerViewController.swift
//  DocumentViewerPro
//
//  Created by Ostorlab Ostorlab on 9/9/25.
//

import UIKit
import WebKit

class DocumentViewerViewController: UIViewController {
    @IBOutlet weak var webView: WKWebView!
    @IBOutlet weak var titleLabel: UILabel!
    
    var document: Document?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadDocument()
    }
    
    private func setupUI() {
        guard let document = document else { return }
        
        title = "Document Viewer"
        titleLabel.text = document.title
        titleLabel.font = UIFont.preferredFont(forTextStyle: .headline)
        
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .action,
            target: self,
            action: #selector(shareDocument)
        )
        
        webView.navigationDelegate = self
        webView.scrollView.contentInsetAdjustmentBehavior = .automatic
    }
    
    private func loadDocument() {
        guard let document = document else { return }
        
        switch document.type {
        case .html:
            loadHTMLContent(document.content)
        case .text, .markdown:
            loadTextContent(document.content, type: document.type)
        }
    }
    
    private func loadHTMLContent(_ htmlString: String) {
        webView.loadHTMLString(htmlString, baseURL: nil)
    }
    
    private func loadTextContent(_ content: String, type: DocumentType) {
        let htmlWrapper = """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, sans-serif; 
                    padding: 20px; 
                    line-height: 1.6;
                    color: #333;
                }
                pre { 
                    background-color: #f5f5f5; 
                    padding: 15px; 
                    border-radius: 5px; 
                    overflow-x: auto; 
                }
            </style>
        </head>
        <body>
            <pre>\(content.replacingOccurrences(of: "<", with: "&lt;").replacingOccurrences(of: ">", with: "&gt;"))</pre>
        </body>
        </html>
        """
        
        webView.loadHTMLString(htmlWrapper, baseURL: nil)
    }
    
    @objc private func shareDocument() {
        guard let document = document else { return }
        
        let activityVC = UIActivityViewController(
            activityItems: [document.title, document.content],
            applicationActivities: nil
        )
        
        activityVC.popoverPresentationController?.barButtonItem = navigationItem.rightBarButtonItem
        present(activityVC, animated: true)
    }
}

extension DocumentViewerViewController: WKNavigationDelegate {
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        showError("Failed to load document: \(error.localizedDescription)")
    }
    
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        if navigationAction.navigationType == .linkActivated {
            if let url = navigationAction.request.url {
                UIApplication.shared.open(url)
                decisionHandler(.cancel)
                return
            }
        }
        decisionHandler(.allow)
    }
    
    private func showError(_ message: String) {
        let alert = UIAlertController(title: "Error", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}
package com.documentmanager.pro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class AuthWebViewActivity extends AppCompatActivity {
    
    private WebView webView;
    private static final String TAG = "AuthWebView";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_webview);
        
        webView = findViewById(R.id.auth_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                
                if ("intent".equals(uri.getScheme())) {
                    try {
                        Intent intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME);
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing intent URL: " + e.getMessage());
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        
        String url = getIntent().getStringExtra("url");
        if (url != null && url.startsWith("intent:")) {
            // Process intent scheme URLs directly (vulnerable implementation)
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                Log.d(TAG, "Processing intent scheme URL: " + url);
                startActivity(intent);
                finish();
                return;
            } catch (Exception e) {
                Log.e(TAG, "Error processing intent URL: " + e.getMessage());
            }
        } else if (url != null && url.startsWith("http") && !url.contains("documentmanager.pro")) {
            Map<String, String> headers = getAuthHeaders();
            webView.loadUrl(url, headers);
        } else {
            loadDefaultDocument();
        }
    }
    
    private Map<String, String> getAuthHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer jwt_token_here");
        headers.put("X-Session-ID", "sess_" + System.currentTimeMillis());
        headers.put("User-Agent", "DocumentManager/1.0");
        return headers;
    }
    
    private void loadDefaultDocument() {
        String documentName = getIntent().getStringExtra("document_name");
        String html = generateDocumentContent(documentName);
        webView.loadDataWithBaseURL("https://secure.documentmanager.pro/", html, "text/html", "UTF-8", null);
    }
    
    private String generateDocumentContent(String docName) {
        if (docName == null) docName = "Document";
        
        String content = "";
        if (docName.contains("Annual Report")) {
            content = "<h3>Annual Report 2024</h3>" +
                     "<p><strong>Executive Summary</strong></p>" +
                     "<p>Our company achieved record growth in 2024 with revenue increasing by 23% year-over-year.</p>" +
                     "<p><strong>Financial Highlights:</strong></p>" +
                     "<ul><li>Total Revenue: $12.4M</li><li>Net Profit: $2.8M</li><li>Customer Growth: 145%</li></ul>" +
                     "<p><strong>Market Position:</strong> Leading provider in enterprise document solutions.</p>";
        } else if (docName.contains("Budget")) {
            content = "<h3>Budget Planning Q1 2025</h3>" +
                     "<table border='1' style='border-collapse: collapse; width: 100%;'>" +
                     "<tr><th>Department</th><th>Q1 Budget</th><th>Projected</th></tr>" +
                     "<tr><td>Engineering</td><td>$450,000</td><td>$475,000</td></tr>" +
                     "<tr><td>Marketing</td><td>$280,000</td><td>$290,000</td></tr>" +
                     "<tr><td>Operations</td><td>$120,000</td><td>$115,000</td></tr>" +
                     "</table>";
        } else if (docName.contains("Meeting Minutes")) {
            content = "<h3>Board Meeting Minutes - December 2024</h3>" +
                     "<p><strong>Attendees:</strong> CEO, CTO, CFO, VP Engineering</p>" +
                     "<p><strong>Key Decisions:</strong></p>" +
                     "<ol><li>Approve Q1 2025 budget allocation</li>" +
                     "<li>Launch new security audit program</li>" +
                     "<li>Expand development team by 30%</li></ol>" +
                     "<p><strong>Action Items:</strong> Security review by January 15th</p>";
        } else if (docName.contains("Contract")) {
            content = "<h3>Standard Contract Template</h3>" +
                     "<p><strong>CONFIDENTIAL AGREEMENT</strong></p>" +
                     "<p>This template contains standard terms for client engagements.</p>" +
                     "<p><strong>Key Clauses:</strong></p>" +
                     "<ul><li>Payment Terms: Net 30 days</li>" +
                     "<li>Confidentiality: 5 year restriction</li>" +
                     "<li>Liability Limitation: $500,000 cap</li></ul>";
        } else {
            content = "<h3>" + docName + "</h3>" +
                     "<p>Document content loaded from secure local storage.</p>" +
                     "<p><strong>Document Details:</strong></p>" +
                     "<ul><li>Size: 2.4 MB</li><li>Created: Nov 15, 2024</li>" +
                     "<li>Last Access: Today</li><li>Encryption: AES-256</li></ul>" +
                     "<p>This document contains sensitive business information and is protected by encryption.</p>";
        }
        
        return "<html><head><title>SecureDoc Viewer</title>" +
               "<style>body{font-family:Arial;margin:20px;line-height:1.6;} " +
               "table{margin:10px 0;} th,td{padding:8px;text-align:left;}</style></head>" +
               "<body>" + content +
               "<hr style='margin: 30px 0;'>" +
               "<p style='color: #666; font-size: 12px;'>🔒 Viewed with authenticated session | " +
               "Session ID: " + System.currentTimeMillis() + "</p>" +
               "<script>" +
               "console.log('Document viewer loaded with authentication');" +
               "// Document interaction logging would go here" +
               "</script>" +
               "</body></html>";
    }
}
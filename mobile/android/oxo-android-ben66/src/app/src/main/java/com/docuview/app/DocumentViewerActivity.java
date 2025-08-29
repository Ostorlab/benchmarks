package com.docuview.app;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DocumentViewerActivity extends AppCompatActivity {

    private WebView webView;
    private String documentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);

        documentName = getIntent().getStringExtra("document_name");
        if (documentName == null) {
            documentName = "sample.html";
        }

        webView = findViewById(R.id.webView);
        setupWebView();
        
        String documentUrl = "https://docs.local/files/" + documentName;
        webView.loadUrl(documentUrl);
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                
                if (uri.getHost().equals("docs.local")) {
                    return handleLocalRequest(uri);
                }
                
                return super.shouldInterceptRequest(view, request);
            }
        });
    }

    private WebResourceResponse handleLocalRequest(Uri uri) {
        String path = uri.getPath();
        
        if (path != null && path.startsWith("/files/")) {
            return serveFile(path.substring(7));
        } else if (path != null && path.startsWith("/assets/")) {
            return serveAsset(path.substring(8));
        }
        
        return null;
    }

    private WebResourceResponse serveFile(String filename) {
        try {
            File file = new File(getFilesDir(), filename);
            
            if (!file.exists()) {
                file = new File(getCacheDir(), filename);
            }
            
            if (!file.exists()) {
                file = new File(filename);
            }
            
            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                Map<String, String> headers = createHeaders();
                return new WebResourceResponse(getMimeType(filename), "utf-8", 200, "OK", headers, inputStream);
            }
        } catch (IOException e) {
            return createErrorResponse();
        }
        
        return createSampleDocument(filename);
    }

    private WebResourceResponse serveAsset(String assetPath) {
        try {
            InputStream inputStream = getAssets().open(assetPath);
            Map<String, String> headers = createHeaders();
            return new WebResourceResponse(getMimeType(assetPath), "utf-8", 200, "OK", headers, inputStream);
        } catch (IOException e) {
            return createErrorResponse();
        }
    }

    private Map<String, String> createHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        headers.put("Cache-Control", "no-cache");
        return headers;
    }

    private String getMimeType(String filename) {
        if (filename.endsWith(".html")) return "text/html";
        if (filename.endsWith(".css")) return "text/css";
        if (filename.endsWith(".js")) return "application/javascript";
        if (filename.endsWith(".pdf")) return "application/pdf";
        if (filename.endsWith(".txt")) return "text/plain";
        if (filename.endsWith(".json")) return "application/json";
        if (filename.endsWith(".xml")) return "application/xml";
        return "text/plain";
    }

    private WebResourceResponse createSampleDocument(String filename) {
        String content = generateSampleContent(filename);
        try {
            InputStream inputStream = new java.io.ByteArrayInputStream(content.getBytes("UTF-8"));
            Map<String, String> headers = createHeaders();
            return new WebResourceResponse("text/html", "utf-8", 200, "OK", headers, inputStream);
        } catch (Exception e) {
            return createErrorResponse();
        }
    }

    private String generateSampleContent(String filename) {
        return "<!DOCTYPE html><html><head><title>" + filename + "</title>" +
               "<style>body{font-family:Arial,sans-serif;margin:40px;}</style></head>" +
               "<body><h1>Document: " + filename + "</h1>" +
               "<p>This is a sample document viewer showing: <strong>" + filename + "</strong></p>" +
               "<p>Document content would be displayed here in a real application.</p>" +
               "<div style='margin-top:20px;padding:10px;background:#f5f5f5;'>" +
               "<h3>Document Properties</h3>" +
               "<p>File: " + filename + "</p>" +
               "<p>Type: " + getMimeType(filename) + "</p>" +
               "<p>Loaded: " + new java.util.Date() + "</p>" +
               "</div></body></html>";
    }

    private WebResourceResponse createErrorResponse() {
        String errorContent = "<!DOCTYPE html><html><head><title>Error</title></head>" +
                             "<body><h1>Document Not Found</h1><p>The requested document could not be loaded.</p></body></html>";
        try {
            InputStream inputStream = new java.io.ByteArrayInputStream(errorContent.getBytes("UTF-8"));
            Map<String, String> headers = createHeaders();
            return new WebResourceResponse("text/html", "utf-8", 404, "Not Found", headers, inputStream);
        } catch (Exception e) {
            return null;
        }
    }
}

package com.globaltech.support;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ArticleActivity extends AppCompatActivity {

    private WebView articleWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_article);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeWebView();
        displaySearchResults();
    }

    private void initializeWebView() {
        articleWebView = findViewById(R.id.articleWebView);
        
        WebSettings webSettings = articleWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        
        articleWebView.setWebViewClient(new WebViewClient());
        articleWebView.setWebChromeClient(new WebChromeClient());
    }

    private void displaySearchResults() {
        String searchQuery = getIntent().getStringExtra("search_query");
        
        if (searchQuery == null || searchQuery.isEmpty()) {
            searchQuery = "general support";
        }

        String htmlContent = generateSearchResultsHTML(searchQuery);
        articleWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    private String generateSearchResultsHTML(String query) {
        return "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; background-color: #f8f9fa; }" +
                ".header { background-color: #3498db; color: white; padding: 15px; border-radius: 8px; margin-bottom: 20px; }" +
                ".article { background-color: white; padding: 20px; margin-bottom: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                ".article h3 { color: #2c3e50; margin-top: 0; }" +
                ".article p { color: #34495e; line-height: 1.6; }" +
                ".query-highlight { background-color: #fff3cd; padding: 10px; border-radius: 4px; margin-bottom: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h2>Search Results for: " + query + "</h2>" +
                "</div>" +
                "<div class='query-highlight'>" +
                "<strong>Your search query:</strong> " + query +
                "</div>" +
                "<div class='article'>" +
                "<h3>Getting Started with Technical Support</h3>" +
                "<p>Welcome to GlobalTech Support! Here are some common troubleshooting steps that might help resolve your issue related to <em>" + query + "</em>:</p>" +
                "<ul>" +
                "<li>Check your internet connection</li>" +
                "<li>Restart your device</li>" +
                "<li>Update your software</li>" +
                "<li>Contact our support team for further assistance</li>" +
                "</ul>" +
                "</div>" +
                "<div class='article'>" +
                "<h3>Advanced Solutions</h3>" +
                "<p>For more complex issues involving " + query + ", please consider these advanced troubleshooting methods:</p>" +
                "<ul>" +
                "<li>Check system logs for error messages</li>" +
                "<li>Run diagnostic tools</li>" +
                "<li>Review configuration settings</li>" +
                "</ul>" +
                "</div>" +
                "<div class='article'>" +
                "<h3>Contact Information</h3>" +
                "<p>If these articles don't resolve your " + query + " issue, please reach out to our support team:</p>" +
                "<ul>" +
                "<li>Email: support@globaltech.com</li>" +
                "<li>Phone: 1-800-TECH-SUP</li>" +
                "<li>Live Chat: Available 24/7</li>" +
                "</ul>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
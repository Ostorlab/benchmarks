package com.newsreader.app;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private String articleUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupWebView();
        loadUrl();
    }

    private void setupWebView() {
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isUrlAllowed(url)) {
                    view.loadUrl(url);
                    return true;
                } else {
                    Toast.makeText(WebViewActivity.this, "URL not allowed", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
    }

    private void loadUrl() {
        articleUrl = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        if (title != null) {
            setTitle(title);
        }

        if (articleUrl != null && isUrlAllowed(articleUrl)) {
            webView.loadUrl(articleUrl);
        } else {
            Toast.makeText(this, "Invalid article URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isUrlAllowed(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        url = url.trim().toLowerCase();

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

package com.ostorlab.unzipper;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptEnabled(true);


        webView.setWebViewClient(new WebViewClient());

        File indexFile = new File(getFilesDir(), "webview/index.html");
        if (indexFile.exists()) {
            webView.loadUrl("file://" + indexFile.getAbsolutePath());
        } else {
            webView.loadData("<h2>index.html not found</h2>", "text/html", "UTF-8");
        }
    }
}

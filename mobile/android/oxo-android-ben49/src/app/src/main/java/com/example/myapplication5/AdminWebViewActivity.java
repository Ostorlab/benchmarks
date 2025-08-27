package com.example.myapplication5;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminWebViewActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String url = getIntent().getStringExtra("url");
        boolean enableJsInterface = getIntent().getBooleanExtra("enable_js_interface", false);
        
        WebView webView = new WebView(this);
        
        if (enableJsInterface) {
            webView.addJavascriptInterface(new AdminInterface(), "Admin");
        }
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        setContentView(webView);
    }

    private class AdminInterface {
        
        @JavascriptInterface
        public String getUserData() {
            return "{\"userId\":\"12345\",\"email\":\"user@example.com\",\"token\":\"secret_token_123\",\"role\":\"admin\"}";
        }

        @JavascriptInterface
        public String getSystemInfo() {
            return "{\"version\":\"1.2.3\",\"debug\":true,\"api_key\":\"sk_live_12345\"}";
        }
    }
}

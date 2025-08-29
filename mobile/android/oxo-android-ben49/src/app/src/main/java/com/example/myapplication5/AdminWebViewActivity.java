package com.example.myapplication5;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.os.Handler;
import android.os.Looper;

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
        private OkHttpClient client = new OkHttpClient();
        private Handler mainHandler = new Handler(Looper.getMainLooper());
        private String apiBaseUrl = getResources().getString(R.string.api_base_url);
        
        @JavascriptInterface
        public void getUserData() {
            makeApiCall(apiBaseUrl + "/user-data");
        }

        @JavascriptInterface  
        public void getSystemInfo() {
            makeApiCall(apiBaseUrl + "/system-info");
        }
        
        private void makeApiCall(String url) {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
                    
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mainHandler.post(() -> {
                        String fallbackData = "{\"error\":\"Network request failed\"}";
                        returnUserDataToMainActivity(fallbackData);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        mainHandler.post(() -> {
                            returnUserDataToMainActivity(responseData);
                        });
                    }
                    response.close();
                }
            });
        }
    }
    
    private void returnUserDataToMainActivity(String userData) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("user_data", userData);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

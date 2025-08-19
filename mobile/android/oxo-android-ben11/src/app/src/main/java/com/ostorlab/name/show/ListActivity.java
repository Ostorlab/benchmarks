package com.ostorlab.name.show;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    WebView webView;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = new DatabaseHelper(this);
        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());

        ArrayList<String> names = db.getAllNames();

        StringBuilder html = new StringBuilder("<html><body><h2>Stored Names</h2><ul>");
        for (String name : names) {
            html.append("<li>").append(name).append("</li>");
        }
        html.append("</ul></body></html>");

        webView.loadDataWithBaseURL(null, html.toString(), "text/html", "UTF-8", null);
    }
}

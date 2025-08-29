package com.globaltech.support;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class ContactActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText emailInput;
    private EditText messageInput;
    private Button previewButton;
    private Button sendButton;
    private MaterialCardView previewCard;
    private WebView previewWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupWebView();
        setupClickListeners();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        messageInput = findViewById(R.id.messageInput);
        previewButton = findViewById(R.id.previewButton);
        sendButton = findViewById(R.id.sendButton);
        previewCard = findViewById(R.id.previewCard);
        previewWebView = findViewById(R.id.previewWebView);
    }

    private void setupWebView() {
        WebSettings webSettings = previewWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        
        previewWebView.setWebViewClient(new WebViewClient());
        previewWebView.setWebChromeClient(new WebChromeClient());
    }

    private void setupClickListeners() {
        previewButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String message = messageInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            generateMessagePreview(name, email, message);
            previewCard.setVisibility(View.VISIBLE);
        });

        sendButton.setOnClickListener(v -> {
            Toast.makeText(this, "Message sent successfully! We'll get back to you within 24 hours.", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void generateMessagePreview(String name, String email, String message) {
        String htmlContent = "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 15px; background-color: #f8f9fa; }" +
                ".message-header { background-color: #27ae60; color: white; padding: 15px; border-radius: 8px 8px 0 0; }" +
                ".message-body { background-color: white; padding: 20px; border-radius: 0 0 8px 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                ".field { margin-bottom: 15px; }" +
                ".label { font-weight: bold; color: #2c3e50; }" +
                ".value { color: #34495e; margin-top: 5px; }" +
                ".message-content { background-color: #ecf0f1; padding: 15px; border-radius: 4px; margin-top: 10px; border-left: 4px solid #27ae60; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='message-header'>" +
                "<h3>Support Request Preview</h3>" +
                "</div>" +
                "<div class='message-body'>" +
                "<div class='field'>" +
                "<div class='label'>From:</div>" +
                "<div class='value'>" + name + " (" + email + ")</div>" +
                "</div>" +
                "<div class='field'>" +
                "<div class='label'>Message:</div>" +
                "<div class='message-content'>" + message + "</div>" +
                "</div>" +
                "<div class='field'>" +
                "<div class='label'>Submitted:</div>" +
                "<div class='value'>" + new java.text.SimpleDateFormat("MMM dd, yyyy 'at' HH:mm").format(new java.util.Date()) + "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        previewWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }
}
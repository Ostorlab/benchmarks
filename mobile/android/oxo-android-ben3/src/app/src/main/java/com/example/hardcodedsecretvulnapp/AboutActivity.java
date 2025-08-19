package com.example.hardcodedsecretvulnapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private TextView appNameText;
    private TextView versionText;
    private TextView descriptionText;
    private Button contactButton;
    private Button privacyButton;
    private Button termsButton;

    private static final String SUPPORT_API_KEY = "support_sk_2mK9nQ7vR8sL3pF6tY1uI4eW0zX5cV7bN8mA9sD2fG4hJ6kL3pQ9rT8wE5yU";
    private static final String ANALYTICS_SECRET = "ga_secret_wJalrXUtnFEMI/K7MDENG+bPxRfiCYzK8vN9mQ2sL5pF8tY1uI4eW0zX3cV7bN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeViews();
        setupContent();
        setupClickListeners();
        logAnalytics();
    }

    private void initializeViews() {
        appNameText = findViewById(R.id.appNameText);
        versionText = findViewById(R.id.versionText);
        descriptionText = findViewById(R.id.descriptionText);
        contactButton = findViewById(R.id.contactButton);
        privacyButton = findViewById(R.id.privacyButton);
        termsButton = findViewById(R.id.termsButton);
    }

    private void setupContent() {
        appNameText.setText("MyApp");
        versionText.setText("Version 2.1.3 (Build 157)");
        descriptionText.setText("MyApp is a modern mobile application designed to help you stay organized and productive. With intuitive features and seamless sync across devices, managing your daily tasks has never been easier.");
    }

    private void setupClickListeners() {
        contactButton.setOnClickListener(v -> openContactSupport());
        privacyButton.setOnClickListener(v -> openPrivacyPolicy());
        termsButton.setOnClickListener(v -> openTermsOfService());
    }

    private void openContactSupport() {
        Log.d("About", "Opening contact support");
        Log.d("About", "Support API key: " + SUPPORT_API_KEY);
        
        String supportUrl = "https://support.example.com/contact?api_key=" + SUPPORT_API_KEY;
        Log.d("About", "Support URL: " + supportUrl);
        
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@example.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - MyApp");
        startActivity(Intent.createChooser(emailIntent, "Contact Support"));
    }

    private void openPrivacyPolicy() {
        String privacyUrl = "https://example.com/privacy";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl));
        startActivity(browserIntent);
    }

    private void openTermsOfService() {
        String termsUrl = "https://example.com/terms";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl));
        startActivity(browserIntent);
    }

    private void logAnalytics() {
        Log.d("About", "About page viewed");
        Log.d("About", "Analytics secret: " + ANALYTICS_SECRET);
        
        String analyticsCall = "POST /analytics/event?secret=" + ANALYTICS_SECRET + "&event=about_viewed";
        Log.d("About", "Analytics call: " + analyticsCall);
    }
}
package com.docuview.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    private TextView helpContent;
    private Button contactButton;
    private Button faqButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        helpContent = findViewById(R.id.helpContent);
        contactButton = findViewById(R.id.contactButton);
        faqButton = findViewById(R.id.faqButton);

        setupHelpContent();

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactEmail();
            }
        });

        faqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFaqPage();
            }
        });
    }

    private void setupHelpContent() {
        String content = "DocumentViewer Help\n\n" +
                "Welcome to DocumentViewer - your comprehensive document management solution.\n\n" +
                "Getting Started:\n" +
                "• Login with your credentials or use guest mode\n" +
                "• Browse documents from the main list\n" +
                "• Tap any document to view it\n" +
                "• Use search to find specific documents\n\n" +
                "Features:\n" +
                "• Secure document viewing\n" +
                "• Multiple file format support\n" +
                "• Offline document caching\n" +
                "• Advanced search capabilities\n" +
                "• Customizable settings\n\n" +
                "Support:\n" +
                "For technical support or questions, please contact our support team.\n\n" +
                "Version: 1.0\n" +
                "Build: 2024.1";

        helpContent.setText(content);
    }

    private void openContactEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@docuview.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DocumentViewer Support Request");
        
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }

    private void openFaqPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
            Uri.parse("https://docuview.com/help/faq"));
        
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
        }
    }
}

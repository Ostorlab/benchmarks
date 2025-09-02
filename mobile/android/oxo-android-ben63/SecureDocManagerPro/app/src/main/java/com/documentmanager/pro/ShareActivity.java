package com.documentmanager.pro;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Handle document sharing and proxy operations
        Intent intent = getIntent();
        String action = intent.getAction();
        
        if (Intent.ACTION_SEND.equals(action)) {
            handleDocumentShare(intent);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            handleDocumentView(intent);
        } else {
            // Handle internal proxy operations (vulnerable pattern from document)
            Intent embeddedIntent = intent.getParcelableExtra("extra_intent");
            if (embeddedIntent != null) {
                startActivity(embeddedIntent);
            }
        }
        
        finish();
    }
    
    private void handleDocumentShare(Intent shareIntent) {
        // Forward to secure document viewer for processing
        Intent viewerIntent = new Intent(this, AuthWebViewActivity.class);
        viewerIntent.putExtra("shared_document", true);
        startActivity(viewerIntent);
    }
    
    private void handleDocumentView(Intent viewIntent) {
        // Process deep link document viewing
        String url = viewIntent.getDataString();
        if (url != null) {
            Intent webIntent = new Intent(this, AuthWebViewActivity.class);
            webIntent.putExtra("url", url);
            startActivity(webIntent);
        }
    }
}
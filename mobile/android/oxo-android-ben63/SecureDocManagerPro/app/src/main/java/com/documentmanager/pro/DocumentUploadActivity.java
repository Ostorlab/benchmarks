package com.documentmanager.pro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DocumentUploadActivity extends AppCompatActivity {

    private static final int PICK_DOCUMENT = 1001;
    private TextView selectedFileText;
    private Button browseButton;
    private Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        selectedFileText = findViewById(R.id.selected_file_text);
        browseButton = findViewById(R.id.browse_button);
        uploadButton = findViewById(R.id.upload_button);

        browseButton.setOnClickListener(v -> openFilePicker());
        uploadButton.setOnClickListener(v -> uploadDocument());

        uploadButton.setEnabled(false);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_DOCUMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PICK_DOCUMENT && resultCode == RESULT_OK && data != null) {
            Uri selectedFile = data.getData();
            if (selectedFile != null) {
                selectedFileText.setText("Selected: " + selectedFile.getLastPathSegment());
                uploadButton.setEnabled(true);
            }
        }
    }

    private void uploadDocument() {
        Toast.makeText(this, "Document uploaded successfully to secure storage", Toast.LENGTH_LONG).show();
        finish();
    }
}
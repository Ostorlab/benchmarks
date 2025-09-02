package com.documentmanager.pro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView documentsList;
    private List<String> documents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initializeDocumentsList();
        setupDocumentsListView();
        setupButtons();
    }
    
    private void initializeDocumentsList() {
        documents = Arrays.asList(
            "📄 Annual Report 2024.pdf",
            "📊 Budget Planning Q1.xlsx", 
            "📝 Meeting Minutes - Board.docx",
            "📋 Contract Template.pdf",
            "📖 Employee Handbook.pdf",
            "💼 Financial Statements.xlsx",
            "🎯 Project Proposal.pptx",
            "⚖️ Legal Documents.pdf",
            "🔍 Audit Report.pdf",
            "📈 Strategic Plan.docx"
        );
    }
    
    private void setupDocumentsListView() {
        documentsList = findViewById(R.id.documents_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, documents);
        documentsList.setAdapter(adapter);
        
        documentsList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDoc = documents.get(position);
            openSecureDocument(selectedDoc);
        });
    }
    
    private void openSecureDocument(String documentName) {
        Intent intent = new Intent(this, AuthWebViewActivity.class);
        intent.putExtra("url", "https://docs.documentmanager.pro/view/" + documentName);
        intent.putExtra("document_name", documentName);
        startActivity(intent);
    }
    
    private void setupButtons() {
        Button searchBtn = findViewById(R.id.btn_search);
        Button uploadBtn = findViewById(R.id.btn_upload);
        Button settingsBtn = findViewById(R.id.btn_settings);
        Button adminBtn = findViewById(R.id.btn_admin);
        
        searchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
        
        uploadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocumentUploadActivity.class);
            startActivity(intent);
        });
        
        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        
        adminBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminPanelActivity.class);
            startActivity(intent);
        });
    }
}
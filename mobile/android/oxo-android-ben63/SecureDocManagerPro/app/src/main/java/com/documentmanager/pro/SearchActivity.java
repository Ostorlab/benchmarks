package com.documentmanager.pro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchField;
    private Button searchButton;
    private ListView resultsListView;
    private List<String> allDocuments;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchField = findViewById(R.id.search_field);
        searchButton = findViewById(R.id.search_button);
        resultsListView = findViewById(R.id.search_results);

        initializeDocuments();
        
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        resultsListView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> performSearch());
        
        resultsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDoc = adapter.getItem(position);
            openSecureDocument(selectedDoc);
        });
    }

    private void initializeDocuments() {
        allDocuments = Arrays.asList(
            "📄 Annual Report 2024.pdf",
            "📊 Budget Planning Q1.xlsx", 
            "📝 Meeting Minutes - Board.docx",
            "📋 Contract Template.pdf",
            "📖 Employee Handbook.pdf",
            "💼 Financial Statements.xlsx",
            "🎯 Project Proposal.pptx",
            "⚖️ Legal Documents.pdf",
            "🔍 Audit Report.pdf",
            "📈 Strategic Plan.docx",
            "💰 Invoice Template.docx",
            "🏢 Company Policy.pdf",
            "📊 Sales Data Q4.xlsx",
            "🔐 Security Guidelines.pdf",
            "📑 NDA Template.docx"
        );
    }

    private void performSearch() {
        String query = searchField.getText().toString().toLowerCase().trim();
        
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter search term", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> results = new ArrayList<>();
        for (String doc : allDocuments) {
            if (doc.toLowerCase().contains(query)) {
                results.add(doc);
            }
        }

        adapter.clear();
        adapter.addAll(results);
        adapter.notifyDataSetChanged();

        if (results.isEmpty()) {
            Toast.makeText(this, "No documents found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, results.size() + " documents found", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void openSecureDocument(String documentName) {
        Intent intent = new Intent(this, AuthWebViewActivity.class);
        intent.putExtra("url", "https://docs.documentmanager.pro/view/" + documentName);
        intent.putExtra("document_name", documentName);
        startActivity(intent);
    }
}
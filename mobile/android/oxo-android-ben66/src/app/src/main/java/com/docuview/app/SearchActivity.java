package com.docuview.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEdit;
    private Button searchButton;
    private ListView resultsListView;
    private List<String> allDocuments;
    private List<String> searchResults;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEdit = findViewById(R.id.searchEdit);
        searchButton = findViewById(R.id.searchButton);
        resultsListView = findViewById(R.id.resultsListView);

        allDocuments = Arrays.asList(
            "Project_Report.pdf",
            "Financial_Summary.docx", 
            "Meeting_Notes.txt",
            "User_Manual.pdf",
            "Technical_Spec.md",
            "Budget_Analysis.xlsx",
            "Legal_Contract.pdf",
            "Marketing_Plan.pptx",
            "Quarterly_Review.pdf",
            "Employee_Handbook.docx",
            "Privacy_Policy.txt",
            "Terms_of_Service.html"
        );

        searchResults = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults);
        resultsListView.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String query = searchEdit.getText().toString().toLowerCase().trim();
        
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        searchResults.clear();
        
        for (String document : allDocuments) {
            if (document.toLowerCase().contains(query)) {
                searchResults.add(document);
            }
        }

        adapter.notifyDataSetChanged();
        
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No documents found matching '" + query + "'", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Found " + searchResults.size() + " documents", Toast.LENGTH_SHORT).show();
        }
    }
}

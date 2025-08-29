package com.docuview.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentListActivity extends AppCompatActivity {

    private ListView documentList;
    private List<String> documents;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        
        documentList = findViewById(R.id.documentList);
        
        documents = new ArrayList<>(Arrays.asList(
            "Project_Report.pdf",
            "Financial_Summary.docx", 
            "Meeting_Notes.txt",
            "User_Manual.pdf",
            "Technical_Spec.md",
            "Budget_Analysis.xlsx",
            "Legal_Contract.pdf",
            "Marketing_Plan.pptx"
        ));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, documents);
        documentList.setAdapter(adapter);

        documentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String document = documents.get(position);
                openDocument(document);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void openDocument(String document) {
        Intent intent = new Intent(this, DocumentViewerActivity.class);
        intent.putExtra("document_name", document);
        startActivity(intent);
    }

    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

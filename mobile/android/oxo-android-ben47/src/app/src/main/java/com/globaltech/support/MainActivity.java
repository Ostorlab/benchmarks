package com.globaltech.support;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private Button contactButton;

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

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        contactButton = findViewById(R.id.contactButton);
    }

    private void setupClickListeners() {
        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
            }
        });

        contactButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
            startActivity(intent);
        });
    }
}
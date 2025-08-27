package com.newsreader.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;
    private NewsAdapter searchAdapter;
    private List<NewsItem> searchResults;
    private NewsService newsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Search News");

        setupViews();
        setupSearch();
    }

    private void setupViews() {
        searchEditText = findViewById(R.id.searchEditText);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        searchResults = new ArrayList<>();
        searchAdapter = new NewsAdapter(searchResults, this::onSearchResultClick);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(searchAdapter);

        newsService = new NewsService();
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.length() > 2) {
                    performSearch(query);
                } else {
                    searchResults.clear();
                    searchAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void performSearch(String query) {
        searchResults.clear();

        List<NewsItem> mockResults = getMockSearchResults(query);
        searchResults.addAll(mockResults);
        searchAdapter.notifyDataSetChanged();
    }

    private List<NewsItem> getMockSearchResults(String query) {
        List<NewsItem> results = new ArrayList<>();

        if (query.toLowerCase().contains("tech")) {
            results.add(new NewsItem(
                "AI Revolution in Technology Sector",
                "Artificial intelligence continues to transform various industries with breakthrough innovations.",
                "Tech Reporter",
                "https://example.com/ai-revolution",
                "https://via.placeholder.com/300x200",
                "2024-10-15T16:00:00Z",
                "technology"
            ));
        }

        if (query.toLowerCase().contains("business")) {
            results.add(new NewsItem(
                "Global Markets Show Strong Recovery",
                "International stock markets demonstrate resilience amid economic uncertainties.",
                "Business Analyst",
                "https://example.com/market-recovery",
                "https://via.placeholder.com/300x200",
                "2024-10-15T15:30:00Z",
                "business"
            ));
        }

        return results;
    }

    private void onSearchResultClick(NewsItem newsItem) {
        Toast.makeText(this, "Opening: " + newsItem.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

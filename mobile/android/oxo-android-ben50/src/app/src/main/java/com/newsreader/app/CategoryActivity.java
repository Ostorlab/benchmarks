package com.newsreader.app;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Categories");

        setupViews();
        loadCategories();
    }

    private void setupViews() {
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCategories() {
        categories = new ArrayList<>();
        categories.add(new Category("Technology", "Latest tech news and innovations", 45));
        categories.add(new Category("Business", "Market updates and business news", 32));
        categories.add(new Category("Sports", "Sports scores and updates", 28));
        categories.add(new Category("Health", "Health and medical news", 19));
        categories.add(new Category("Science", "Scientific discoveries and research", 23));
        categories.add(new Category("Entertainment", "Celebrity news and entertainment", 36));
        categories.add(new Category("Politics", "Political news and updates", 41));
        categories.add(new Category("World", "International news coverage", 52));

        categoryAdapter = new CategoryAdapter(categories, this::onCategoryClick);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void onCategoryClick(Category category) {
        Toast.makeText(this, "Loading " + category.getName() + " articles", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

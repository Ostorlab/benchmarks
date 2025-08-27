package com.newsreader.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ArticleDetailActivity extends AppCompatActivity {
    private NewsItem newsItem;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView publishedAtTextView;
    private TextView descriptionTextView;
    private ImageView articleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViews();
        loadArticleData();
    }

    private void setupViews() {
        titleTextView = findViewById(R.id.titleTextView);
        authorTextView = findViewById(R.id.authorTextView);
        publishedAtTextView = findViewById(R.id.publishedAtTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        articleImageView = findViewById(R.id.articleImageView);
    }

    private void loadArticleData() {
        newsItem = (NewsItem) getIntent().getSerializableExtra("news_item");

        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            authorTextView.setText("By " + newsItem.getAuthor());
            publishedAtTextView.setText(formatDate(newsItem.getPublishedAt()));
            descriptionTextView.setText(newsItem.getDescription());

            articleImageView.setImageResource(R.drawable.news_placeholder);

            setTitle(newsItem.getTitle());
        }
    }

    private String formatDate(String publishedAt) {
        if (publishedAt == null || publishedAt.isEmpty()) {
            return "Unknown date";
        }
        try {
            return publishedAt.substring(0, 10);
        } catch (Exception e) {
            return "Unknown date";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_open_full) {
            openFullArticle();
            return true;
        } else if (id == R.id.menu_share) {
            shareArticle();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openFullArticle() {
        if (newsItem != null && newsItem.getUrl() != null) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", newsItem.getUrl());
            intent.putExtra("title", newsItem.getTitle());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Article URL not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareArticle() {
        if (newsItem != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                newsItem.getTitle() + "\n\n" + newsItem.getUrl());
            startActivity(Intent.createChooser(shareIntent, "Share article"));
        }
    }
}

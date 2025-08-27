package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvName, tvEmail, tvAge, tvWeight, tvHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAge = findViewById(R.id.tvAge);
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadUserProfile();
    }

    private void loadUserProfile() {
        String currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId == null) return;

        User user = dbHelper.getUserById(currentUserId);

        if (user != null) {
            if (tvName != null) tvName.setText("Name: " + user.getName());
            if (tvEmail != null) tvEmail.setText("Email: " + user.getEmail());
            if (tvAge != null) tvAge.setText("Age: " + user.getAge());
            if (tvWeight != null) tvWeight.setText("Weight: " + user.getWeight() + " kg");
            if (tvHeight != null) tvHeight.setText("Height: " + user.getHeight() + " cm");
        }

        Intent databaseIntent = new Intent("com.fittracker.DATABASE_READ");
        databaseIntent.putExtra("operation", "get_user_profile");
        databaseIntent.putExtra("table", "users");
        databaseIntent.putExtra("user_id", currentUserId);
        databaseIntent.putExtra("database_path", getDatabasePath("fittracker.db").getAbsolutePath());
        databaseIntent.putExtra("sensitive_query", "SELECT * FROM users WHERE user_id='" + currentUserId + "'");
        databaseIntent.putExtra("pii_accessed", "name,email,phone,insurance,billing");
        sendBroadcast(databaseIntent);

        if (user != null) {
            Intent profileIntent = new Intent("com.fittracker.PROFILE_LOADED");
            profileIntent.putExtra("user_id", user.getUserId());
            profileIntent.putExtra("full_name", user.getName());
            profileIntent.putExtra("email", user.getEmail());
            profileIntent.putExtra("phone", user.getPhone());
            profileIntent.putExtra("age", user.getAge());
            profileIntent.putExtra("weight", user.getWeight());
            profileIntent.putExtra("height", user.getHeight());
            profileIntent.putExtra("account_type", user.getAccountType());
            profileIntent.putExtra("last_login", user.getLastLogin());
            profileIntent.putExtra("created_at", user.getCreatedAt());
            profileIntent.putExtra("database_record_retrieved", true);
            sendBroadcast(profileIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId == null) return;

        Intent sessionIntent = new Intent("com.fittracker.PROFILE_SESSION");
        sessionIntent.putExtra("user_id", currentUserId);
        sessionIntent.putExtra("session_end", System.currentTimeMillis());
        sessionIntent.putExtra("profile_changes", "none");
        sessionIntent.putExtra("time_spent", 120);
        sessionIntent.putExtra("database_access_count", 1);
        sendBroadcast(sessionIntent);
    }
}

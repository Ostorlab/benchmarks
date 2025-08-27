package com.newsreader.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText emailEditText;
    private TextView preferencesTextView;
    private Button saveButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile Settings");

        setupViews();
        loadProfile();
    }

    private void setupViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        preferencesTextView = findViewById(R.id.preferencesTextView);
        saveButton = findViewById(R.id.saveButton);

        sharedPreferences = getSharedPreferences("user_profile", MODE_PRIVATE);

        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        String name = sharedPreferences.getString("user_name", "");
        String email = sharedPreferences.getString("user_email", "");

        nameEditText.setText(name);
        emailEditText.setText(email);

        updatePreferencesText();
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || !isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", name);
        editor.putString("user_email", email);
        editor.apply();

        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
        updatePreferencesText();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void updatePreferencesText() {
        StringBuilder preferences = new StringBuilder();
        preferences.append("Reading Preferences:\n");
        preferences.append("• Technology News\n");
        preferences.append("• Business Updates\n");
        preferences.append("• Daily Notifications Enabled\n");
        preferences.append("• Auto-refresh: Every 30 minutes");

        preferencesTextView.setText(preferences.toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

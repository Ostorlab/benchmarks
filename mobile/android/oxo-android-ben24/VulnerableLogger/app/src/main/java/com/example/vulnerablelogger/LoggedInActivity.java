package com.example.vulnerablelogger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoggedInActivity extends AppCompatActivity {

    Button buttonLogout, buttonSettings;
    TextView textWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        buttonLogout = findViewById(R.id.buttonLogout);
        buttonSettings = findViewById(R.id.buttonSettings);
        textWelcome = findViewById(R.id.textWelcome);

        // Get the username passed from the MainActivity
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null && !username.isEmpty()) {
            textWelcome.setText("Welcome, " + username + "!");
            // --- SCENARIO: LEAK LOGGED-IN USER DATA ON SCREEN LOAD ---
            Log.i("VulnerableApp-Session", "User '" + username + "' has landed on the home screen.");
        }

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(LoggedInActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(v -> {
            finish();
        });
    }
}
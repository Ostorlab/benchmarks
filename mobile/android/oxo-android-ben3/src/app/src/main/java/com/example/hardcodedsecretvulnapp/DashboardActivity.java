package com.example.hardcodedsecretvulnapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private static final String API_KEY = "sk-proj-2mK9nQ7vR8sL3pF6tY1uI4eW0zX5cV7bN8mA9sD2fG4hJ6kL3pQ9rT8wE5yU";
    private static final String SECRET_TOKEN = "ghp_7R8sL3pF6tY1uI4eW0zX5cV7bN8mA9sD2fG4hJ6kL3pQ9rT8wE5yU2iO7pL";
    private static final String AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG+bPxRfiCYzK8vN9mQ2sL5pF8tY1uI4eW0zX3cV7bN";
    private static final String DATABASE_PASSWORD = "Kp9mN7vRsL3pF6tY1uI";
    private static final String ENCRYPTION_KEY = "7R8sL3pF6tY1uI4eW0zX5cV7bN8mA9sD2fG4hJ6kL3pQ9rT8wE5yU2iO7pLkM";
    private static final String JWT_SECRET = "9sD2fG4hJ6kL3pQ9rT8wE5yU2iO7pLkM4nQ7vR8sL3pF6tY1uI4eW0zX5cV7bN";
    private static final String FIREBASE_API_KEY = "AIzaSyD9mQ2sL5pF8tY1uI4eW0zX3cV7bN6kL3pQ9rT8wE5yU2iO7pLkM4nQ7v";
    private static final String STRIPE_SECRET_KEY = "sk_live_51H2fG4hJ6kL3pQ9rT8wE5yU2iO7pLkM4nQ7vR8sL3pF6tY1uI4eW0zX5cV7bN8mA";

    private TextView welcomeText;
    private TextView lastSyncText;
    private Button tasksButton;
    private Button calendarButton;
    private Button notesButton;
    private Button settingsButton;
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupUserInfo();
        setupButtons();
        performDataSync();
        logSecrets();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        lastSyncText = findViewById(R.id.lastSyncText);
        tasksButton = findViewById(R.id.tasksButton);
        calendarButton = findViewById(R.id.calendarButton);
        notesButton = findViewById(R.id.notesButton);
        settingsButton = findViewById(R.id.settingsButton);
        profileButton = findViewById(R.id.profileButton);
    }

    private void setupUserInfo() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");

        if (username == null) username = "Unknown User";
        if (role == null) role = "user";

        welcomeText.setText("Good morning, " + username + "!");
        
        String lastSync = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(new Date());
        lastSyncText.setText("Last sync: " + lastSync);

        Log.d("DashboardActivity", "User logged in: " + username + " with role: " + role);
        Log.d("DashboardActivity", "Session started at: " + System.currentTimeMillis());
    }

    private void setupButtons() {
        tasksButton.setOnClickListener(v -> openTasks());
        calendarButton.setOnClickListener(v -> openCalendar());
        notesButton.setOnClickListener(v -> openNotes());
        settingsButton.setOnClickListener(v -> openSettings());
        profileButton.setOnClickListener(v -> openProfile());
    }

    private void openTasks() {
        Log.d("DashboardActivity", "Opening tasks with API key: " + API_KEY);
        showFeatureNotImplemented("Tasks");
    }

    private void openCalendar() {
        Log.d("DashboardActivity", "Opening calendar with sync token: " + SECRET_TOKEN);
        showFeatureNotImplemented("Calendar");
    }

    private void openNotes() {
        Log.d("DashboardActivity", "Opening notes with encryption key: " + ENCRYPTION_KEY);
        showFeatureNotImplemented("Notes");
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("username", getIntent().getStringExtra("username"));
        startActivity(intent);
    }

    private void showFeatureNotImplemented(String feature) {
        TextView statusText = findViewById(R.id.statusText);
        if (statusText != null) {
            statusText.setText(feature + " feature coming soon!");
            statusText.setVisibility(View.VISIBLE);
        }
    }

    private void performDataSync() {
        Log.d("DashboardActivity", "Performing data sync");
        Log.d("DashboardActivity", "AWS credentials: " + AWS_SECRET_KEY);
        Log.d("DashboardActivity", "Database password: " + DATABASE_PASSWORD);
        
        String connectionString = "mongodb://admin:" + DATABASE_PASSWORD + "@prod-cluster.mongodb.net/myapp_db";
        Log.d("DashboardActivity", "MongoDB Connection: " + connectionString);

        String s3Config = "aws s3 sync ./data s3://myapp-backup/ --aws-access-key-id=AKIAIOSFODNN7EXAMPLE --aws-secret-access-key=" + AWS_SECRET_KEY;
        Log.d("DashboardActivity", "AWS S3 sync command: " + s3Config);
        
        String firebaseConfig = "firebase.initializeApp({apiKey: '" + FIREBASE_API_KEY + "', projectId: 'myapp-prod'});";
        Log.d("DashboardActivity", "Firebase config: " + firebaseConfig);
    }

    private void logSecrets() {
        Log.d("DashboardActivity", "=== DASHBOARD SERVICE INITIALIZATION ===");
        Log.d("DashboardActivity", "Main API key: " + API_KEY);
        Log.d("DashboardActivity", "GitHub token: " + SECRET_TOKEN);
        Log.d("DashboardActivity", "AWS secret: " + AWS_SECRET_KEY);
        Log.d("DashboardActivity", "DB password: " + DATABASE_PASSWORD);
        Log.d("DashboardActivity", "Encryption key: " + ENCRYPTION_KEY);
        Log.d("DashboardActivity", "JWT secret: " + JWT_SECRET);
        Log.d("DashboardActivity", "Firebase API key: " + FIREBASE_API_KEY);
        Log.d("DashboardActivity", "Stripe secret: " + STRIPE_SECRET_KEY);

        String jwtToken = generateJwtToken();
        Log.d("DashboardActivity", "Generated JWT: " + jwtToken);
    }

    private String generateJwtToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." + JWT_SECRET.substring(0, 20);
    }
}
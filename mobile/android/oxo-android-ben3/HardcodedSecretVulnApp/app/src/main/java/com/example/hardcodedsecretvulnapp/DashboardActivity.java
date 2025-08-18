package com.example.hardcodedsecretvulnapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    private TextView userRoleText;
    private TextView secretsText;
    private Button logoutButton;
    private Button viewSecretsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupUserInfo();
        setupButtons();
        logSecrets();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        userRoleText = findViewById(R.id.userRoleText);
        secretsText = findViewById(R.id.secretsText);
        logoutButton = findViewById(R.id.logoutButton);
        viewSecretsButton = findViewById(R.id.viewSecretsButton);
    }

    private void setupUserInfo() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String role = intent.getStringExtra("role");

        if (username == null) username = "Unknown User";
        if (role == null) role = "user";

        welcomeText.setText("Welcome, " + username + "!");
        userRoleText.setText("Role: " + role.toUpperCase());

        Log.d("DashboardActivity", "User logged in: " + username + " with role: " + role);
        Log.d("DashboardActivity", "Session started at: " + System.currentTimeMillis());
    }

    private void setupButtons() {
        logoutButton.setOnClickListener(v -> logout());
        viewSecretsButton.setOnClickListener(v -> displaySecrets());
    }

    private void displaySecrets() {
        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        String username = intent.getStringExtra("username");

        StringBuilder secrets = new StringBuilder();
        secrets.append("=== EXPOSED SECRETS ===\n\n");
        
        secrets.append("⚠️ Limited access for user role\n\n");
        secrets.append("🔑 Basic API Key: ").append(API_KEY.substring(0, 10)).append("...\n");
        secrets.append("🔑 User Token: user_").append(username).append("_token_123\n");
        secrets.append("\n📱 User Info:\n");
        secrets.append("• Username: ").append(username).append("\n");
        secrets.append("• Role: ").append(role).append("\n");
        secrets.append("• Session: active\n");

        secretsText.setText(secrets.toString());
        secretsText.setVisibility(View.VISIBLE);

        Log.d("DashboardActivity", "Secrets displayed for user: " + username + " with role: " + role);
        Log.d("DashboardActivity", "Full secrets exposed in logs: " + secrets.toString());
    }

    private void logSecrets() {
        Log.d("DashboardActivity", "=== HARDCODED SECRETS IN DASHBOARD ===");
        Log.d("DashboardActivity", "API_KEY: " + API_KEY);
        Log.d("DashboardActivity", "SECRET_TOKEN: " + SECRET_TOKEN);
        Log.d("DashboardActivity", "AWS_SECRET_KEY: " + AWS_SECRET_KEY);
        Log.d("DashboardActivity", "DATABASE_PASSWORD: " + DATABASE_PASSWORD);
        Log.d("DashboardActivity", "ENCRYPTION_KEY: " + ENCRYPTION_KEY);
        Log.d("DashboardActivity", "JWT_SECRET: " + JWT_SECRET);
        Log.d("DashboardActivity", "FIREBASE_API_KEY: " + FIREBASE_API_KEY);
        Log.d("DashboardActivity", "STRIPE_SECRET_KEY: " + STRIPE_SECRET_KEY);

        String connectionString = "mongodb://admin:" + DATABASE_PASSWORD + "@prod-cluster.mongodb.net/vulnerable_app";
        Log.d("DashboardActivity", "MongoDB Connection: " + connectionString);

        String s3Config = "aws s3 cp file.txt s3://vulnerable-bucket/ --aws-access-key-id=AKIAIOSFODNN7EXAMPLE --aws-secret-access-key=" + AWS_SECRET_KEY;
        Log.d("DashboardActivity", "AWS S3 Command: " + s3Config);
    }

    private void logout() {
        Log.d("DashboardActivity", "User logging out");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
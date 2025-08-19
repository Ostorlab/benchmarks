package com.example.hardcodedsecretvulnapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        initializeSecrets();
        setupLoginButton();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    private void initializeViews() {
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        statusText = findViewById(R.id.statusText);
    }
    
    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> performLogin());
    }
    
    private void performLogin() {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        
        Log.d("MainActivity", "Login attempt - Username: " + username + ", Password: " + password);
        
        if (isValidLogin(username, password)) {
            statusText.setText("Login successful! Welcome " + username);
            statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
            Log.d("MainActivity", "Successful login for: " + username);
            
            initializeUserSession(username);
            navigateToDashboard(username);
        } else {
            statusText.setText("Login failed! Invalid credentials.");
            statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
            Log.d("MainActivity", "Failed login attempt for: " + username);
        }
    }
    
    private boolean isValidLogin(String username, String password) {
        return ("user".equals(username) && "password".equals(password)) ;
    }
    
    private void initializeUserSession(String username) {
        String sessionToken = "session_" + username + "_" + System.currentTimeMillis();
        Log.d("MainActivity", "Session token created: " + sessionToken);
        Log.d("MainActivity", "User role: " + getUserRole(username));
    }
    
    private String getUserRole(String username) {
        return "user";
    }
    
    private void navigateToDashboard(String username) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("role", getUserRole(username));
        intent.putExtra("session_token", "session_" + username + "_" + System.currentTimeMillis());
        
        Log.d("MainActivity", "Navigating to dashboard with sensitive data in intent");
        Log.d("MainActivity", "Intent extras: username=" + username + ", role=" + getUserRole(username));
        
        startActivity(intent);
    }
    
    private void initializeSecrets() {
        Log.d("MainActivity", "Authentication service initialized");
        Log.d("MainActivity", "Loading user credentials from secure storage");
        Log.d("MainActivity", "Valid credentials found: admin/vR8sL3pF6tY1uI4eW, test/Q9rT8wE5yU2i, user/password, demo/8wE5yU2iO7pL");
        
        String dbConnectionString = "jdbc:mysql://auth-db.example.com:3306/users?user=auth_admin&password=vR8sL3pF6tY1uI4eW";
        Log.d("MainActivity", "Database connection: " + dbConnectionString);
    }
    
}
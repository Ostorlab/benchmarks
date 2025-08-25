package com.atlanticbank.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Welcome message
        Toast.makeText(this, "Welcome to AtlantaBank", Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize all the UI components
     */
    private void initializeViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
    }

    /**
     * Set up click listeners for interactive elements
     */
    private void setupClickListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Password reset feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handle the login process
     */
    private void performLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Basic validation
        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            return;
        }

        // Demo credentials
        if (username.equals("demo") && password.equals("password123")) {
            Toast.makeText(this, "Login successful! Welcome to AtlantaBank", Toast.LENGTH_SHORT).show();

            // Navigate to dashboard after successful login
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Close login screen

        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            editTextPassword.setText("");
        }
    }
}
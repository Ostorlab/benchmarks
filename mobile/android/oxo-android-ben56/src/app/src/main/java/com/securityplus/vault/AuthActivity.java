package com.securityplus.vault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    
    private static final String CORRECT_PIN = "1234";
    private static final String PREFS_NAME = "SecureVaultPrefs";
    private static final String KEY_AUTHENTICATED = "is_authenticated";
    
    // VULNERABILITY: Back button press counter for bypass
    private static int backPressCount = 0;
    private long lastBackPress = 0;
    
    private EditText pinEditText;
    private Button loginButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        initializeViews();
        setupClickListeners();
        setupBackPressHandler();
    }
    
    private void initializeViews() {
        pinEditText = findViewById(R.id.pinEditText);
        loginButton = findViewById(R.id.loginButton);
    }
    
    private void setupClickListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePin();
            }
        });
    }
    
    private void validatePin() {
        String enteredPin = pinEditText.getText().toString();
        
        if (CORRECT_PIN.equals(enteredPin)) {
            // Set authentication status
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean(KEY_AUTHENTICATED, true).apply();
            
            // Navigate to vault
            Intent intent = new Intent(this, VaultActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect PIN. Please try again.", Toast.LENGTH_SHORT).show();
            pinEditText.setText("");
        }
    }
    
    private void setupBackPressHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long currentTime = System.currentTimeMillis();
                
                if (currentTime - lastBackPress < 1000) {
                    backPressCount++;
                } else {
                    backPressCount = 1;
                }
                
                lastBackPress = currentTime;
                
                // Emergency access for accessibility - TODO: Remove before production
                if (backPressCount >= 5) {
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    prefs.edit().putBoolean(KEY_AUTHENTICATED, true).apply();
                    
                    Intent intent = new Intent(AuthActivity.this, VaultActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                
                // Default back behavior for counts 1-4
                // No action needed
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
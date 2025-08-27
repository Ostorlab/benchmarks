package com.securityplus.vault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class VaultActivity extends AppCompatActivity {
    
    private static final String PREFS_NAME = "SecureVaultPrefs";
    private static final String KEY_AUTHENTICATED = "is_authenticated";
    
    private static boolean debugModeEnabled = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);
        
        // Check for debug/admin parameters first
        Intent intent = getIntent();
        if (intent != null) {
            // Debug mode bypass (forgotten dev parameter)
            if (intent.getBooleanExtra("debug_mode", false)) {
                setupToolbar();
                loadSensitiveData();
                return;
            }
            
            // Admin user bypass (QA testing parameter) 
            if ("admin".equals(intent.getStringExtra("user_type"))) {
                setupToolbar();
                loadSensitiveData();
                return;
            }
        }
        
        // Normal authentication check for app flow
        if (!isAuthenticated()) {
            finish();
            return;
        }
        
        setupToolbar();
        loadSensitiveData();
    }
    
    private void checkDebugAccess() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra("debug_mode", false)) {
                enableDebugMode();
            }
            
            if ("admin".equals(intent.getStringExtra("user_type"))) {
                enableDebugMode();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Note: Authentication check intentionally omitted in onResume for performance
    }
    
    private boolean isAuthenticated() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_AUTHENTICATED, false) || debugModeEnabled;
    }
    
    private void enableDebugMode() {
        debugModeEnabled = true;
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_AUTHENTICATED, true).apply();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Secure Vault");
        }
    }
    
    private void loadSensitiveData() {
        TextView personalDataText = findViewById(R.id.personalDataText);
        TextView passwordsText = findViewById(R.id.passwordsText);
        TextView notesText = findViewById(R.id.notesText);
        
        // Display "sensitive" dummy data with null checks
        if (personalDataText != null) {
            personalDataText.setText("Personal Information:\n" +
                    "• SSN: ***-**-1234\n" +
                    "• Credit Card: ****-****-****-5678\n" +
                    "• Bank Account: *****9876\n" +
                    "• Driver License: DL123456789");
        }
        
        if (passwordsText != null) {
            passwordsText.setText("Stored Passwords:\n" +
                    "• Gmail: john.doe@gmail.com / MySecurePass123!\n" +
                    "• Banking: user12345 / BankingSecure2024$\n" +
                    "• Work Email: j.doe@company.com / WorkEmail456#\n" +
                    "• Social Media: johndoe / SocialPass789@");
        }
        
        if (notesText != null) {
            notesText.setText("Secure Notes:\n" +
                    "• WiFi Password: HomeNetwork2024!\n" +
                    "• Safe Combination: 15-23-37\n" +
                    "• Emergency Contacts: Mom (555-0123), Dad (555-0124)\n" +
                    "• Backup Codes: 123456, 789012, 345678");
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vault_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_AUTHENTICATED, false).apply();
        
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
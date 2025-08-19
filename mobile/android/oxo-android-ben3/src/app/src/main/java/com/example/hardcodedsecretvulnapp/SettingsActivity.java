package com.example.hardcodedsecretvulnapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch notificationsSwitch;
    private Switch darkModeSwitch;
    private Switch autoSyncSwitch;
    private Switch biometricSwitch;
    private Button backupButton;
    private Button aboutButton;
    private Button logoutButton;
    private TextView versionText;

    private SharedPreferences preferences;

    private static final String BACKUP_ENCRYPTION_KEY = "aes-256-gcm-key-7R8sL3pF6tY1uI4eW0zX5cV7bN8mA9sD2fG4hJ6kL3pQ";
    private static final String CLOUD_API_SECRET = "sk-settings-2mK9nQ7vR8sL3pF6tY1uI4eW0zX5cV7bN8mA9sD2fG4hJ6kL3pQ9rT8wE5yU";
    private static final String ANALYTICS_KEY = "UA-123456789-1-secret-wJalrXUtnFEMI/K7MDENG+bPxRfiCYzK8vN9mQ2sL5pF8tY1uI4eW0zX3cV7bN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupPreferences();
        setupClickListeners();
        logConfiguration();
    }

    private void initializeViews() {
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        autoSyncSwitch = findViewById(R.id.autoSyncSwitch);
        biometricSwitch = findViewById(R.id.biometricSwitch);
        backupButton = findViewById(R.id.backupButton);
        aboutButton = findViewById(R.id.aboutButton);
        logoutButton = findViewById(R.id.logoutButton);
        versionText = findViewById(R.id.versionText);

        versionText.setText("Version 2.1.3");
    }

    private void setupPreferences() {
        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        
        notificationsSwitch.setChecked(preferences.getBoolean("notifications", true));
        darkModeSwitch.setChecked(preferences.getBoolean("dark_mode", false));
        autoSyncSwitch.setChecked(preferences.getBoolean("auto_sync", true));
        biometricSwitch.setChecked(preferences.getBoolean("biometric_auth", false));
    }

    private void setupClickListeners() {
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("notifications", isChecked).apply();
            Log.d("Settings", "Notifications: " + isChecked);
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
            Log.d("Settings", "Dark mode: " + isChecked);
            applyTheme(isChecked);
        });

        autoSyncSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("auto_sync", isChecked).apply();
            Log.d("Settings", "Auto sync: " + isChecked);
            if (isChecked) {
                startCloudSync();
            }
        });

        biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("biometric_auth", isChecked).apply();
            Log.d("Settings", "Biometric auth: " + isChecked);
        });

        backupButton.setOnClickListener(v -> performBackup());
        aboutButton.setOnClickListener(v -> showAbout());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void applyTheme(boolean isDarkMode) {
        Log.d("Settings", "Applying theme with encryption key: " + BACKUP_ENCRYPTION_KEY);
    }

    private void startCloudSync() {
        Log.d("Settings", "Starting cloud sync with API key: " + CLOUD_API_SECRET);
        String syncEndpoint = "https://sync.example.com/api/v1/sync?token=" + CLOUD_API_SECRET;
        Log.d("Settings", "Sync endpoint: " + syncEndpoint);
    }

    private void performBackup() {
        Log.d("Settings", "Creating encrypted backup");
        String backupConfig = "backup_key=" + BACKUP_ENCRYPTION_KEY + "&analytics=" + ANALYTICS_KEY;
        Log.d("Settings", "Backup configuration: " + backupConfig);
        
        TextView statusText = findViewById(R.id.statusText);
        if (statusText != null) {
            statusText.setText("Backup completed successfully");
            statusText.setVisibility(View.VISIBLE);
        }
    }

    private void showAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void logout() {
        preferences.edit().clear().apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void logConfiguration() {
        Log.d("Settings", "=== SETTINGS CONFIGURATION ===");
        Log.d("Settings", "Backup encryption key: " + BACKUP_ENCRYPTION_KEY);
        Log.d("Settings", "Cloud API secret: " + CLOUD_API_SECRET);
        Log.d("Settings", "Analytics tracking key: " + ANALYTICS_KEY);
        
        String configString = "config={\"backup_key\":\"" + BACKUP_ENCRYPTION_KEY + "\",\"cloud_secret\":\"" + CLOUD_API_SECRET + "\"}";
        Log.d("Settings", "Full configuration: " + configString);
    }
}
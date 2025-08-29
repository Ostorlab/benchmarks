package com.securityplus.vault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    
    private static final String PREFS_NAME = "SecureVaultPrefs";
    
    private Switch biometricSwitch;
    private Switch autoLockSwitch;
    private Switch cloudBackupSwitch;
    private Button changePinButton;
    private Button exportDataButton;
    private Button resetAppButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        setupToolbar();
        initializeViews();
        setupClickListeners();
        loadSettings();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Security Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeViews() {
        biometricSwitch = findViewById(R.id.biometricSwitch);
        autoLockSwitch = findViewById(R.id.autoLockSwitch);
        cloudBackupSwitch = findViewById(R.id.cloudBackupSwitch);
        changePinButton = findViewById(R.id.changePinButton);
        exportDataButton = findViewById(R.id.exportDataButton);
        resetAppButton = findViewById(R.id.resetAppButton);
    }
    
    private void setupClickListeners() {
        changePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Change PIN functionality would be implemented here", Toast.LENGTH_SHORT).show();
            }
        });
        
        exportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Exporting vault data... (Demo)", Toast.LENGTH_SHORT).show();
            }
        });
        
        resetAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetApp();
            }
        });
    }
    
    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        biometricSwitch.setChecked(prefs.getBoolean("biometric_enabled", false));
        autoLockSwitch.setChecked(prefs.getBoolean("auto_lock_enabled", true));
        cloudBackupSwitch.setChecked(prefs.getBoolean("cloud_backup_enabled", false));
    }
    
    private void resetApp() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Toast.makeText(this, "App reset successfully", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();
    }
    
    private void saveSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putBoolean("biometric_enabled", biometricSwitch.isChecked());
        editor.putBoolean("auto_lock_enabled", autoLockSwitch.isChecked());
        editor.putBoolean("cloud_backup_enabled", cloudBackupSwitch.isChecked());
        
        editor.apply();
    }
}
package com.securevault.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private StorageManager storageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageManager = new StorageManager(this);

        Switch switchBiometric = findViewById(R.id.switchBiometric);
        Switch switchAutoBackup = findViewById(R.id.switchAutoBackup);
        Button btnLogout = findViewById(R.id.btnLogout);

        switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // In a real app, this would configure biometric authentication
        });

        switchAutoBackup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // In a real app, this would configure auto backup
        });

        btnLogout.setOnClickListener(v -> {
            storageManager.clearAllData();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

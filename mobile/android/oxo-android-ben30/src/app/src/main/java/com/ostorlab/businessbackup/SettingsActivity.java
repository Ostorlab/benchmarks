package com.ostorlab.businessbackup;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.ostorlab.businessbackup.util.DataManager;

/**
 * Settings activity for application configuration
 * This activity doesn't require special permissions and serves as a control
 */
public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private DataManager dataManager;
    private SwitchMaterial switchAutoBackup;
    private MaterialAutoCompleteTextView spinnerBackupFrequency;
    private MaterialAutoCompleteTextView spinnerDataRetention;
    private MaterialButton btnExportData;
    private MaterialButton btnImportData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.d(TAG, "SettingsActivity onCreate");

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.settings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize components
        initializeComponents();
        setupDropdowns();
        setupClickListeners();
        loadSettings();
    }

    private void initializeComponents() {
        dataManager = new DataManager(this);
        switchAutoBackup = findViewById(R.id.switchAutoBackup);
        spinnerBackupFrequency = findViewById(R.id.spinnerBackupFrequency);
        spinnerDataRetention = findViewById(R.id.spinnerDataRetention);
        btnExportData = findViewById(R.id.btnExportData);
        btnImportData = findViewById(R.id.btnImportData);
    }

    private void setupDropdowns() {
        // Setup backup frequency dropdown
        String[] frequencies = {"Daily", "Weekly", "Monthly"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, frequencies);
        spinnerBackupFrequency.setAdapter(frequencyAdapter);

        // Setup data retention dropdown
        String[] retentions = {"7 days", "30 days", "90 days", "1 year", "Forever"};
        ArrayAdapter<String> retentionAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, retentions);
        spinnerDataRetention.setAdapter(retentionAdapter);
    }

    private void setupClickListeners() {
        switchAutoBackup.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Auto backup setting changed: " + isChecked);
            saveAutoBackupSetting(isChecked);
        });

        spinnerBackupFrequency.setOnItemClickListener((parent, view, position, id) -> {
            String frequency = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "Backup frequency changed: " + frequency);
            saveBackupFrequency(frequency);
        });

        spinnerDataRetention.setOnItemClickListener((parent, view, position, id) -> {
            String retention = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "Data retention changed: " + retention);
            saveDataRetention(retention);
        });

        btnExportData.setOnClickListener(v -> {
            Log.d(TAG, "Export data button clicked");
            exportData();
        });

        btnImportData.setOnClickListener(v -> {
            Log.d(TAG, "Import data button clicked");
            importData();
        });
    }

    private void loadSettings() {
        // Load settings from preferences
        // For this demo, we'll use default values
        switchAutoBackup.setChecked(true);
        spinnerBackupFrequency.setText("Daily", false);
        spinnerDataRetention.setText("30 days", false);
        
        Log.d(TAG, "Settings loaded");
    }

    private void saveAutoBackupSetting(boolean enabled) {
        // In a real app, this would save to SharedPreferences
        Log.d(TAG, "Auto backup setting saved: " + enabled);
    }

    private void saveBackupFrequency(String frequency) {
        // In a real app, this would save to SharedPreferences
        Log.d(TAG, "Backup frequency saved: " + frequency);
    }

    private void saveDataRetention(String retention) {
        // In a real app, this would save to SharedPreferences
        Log.d(TAG, "Data retention saved: " + retention);
    }

    private void exportData() {
        Log.d(TAG, "Starting data export");
        
        // Simulate data export process
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate export time
                
                runOnUiThread(() -> {
                    Log.d(TAG, "Data export completed successfully");
                    // In a real app, would show success message
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e(TAG, "Data export interrupted", e);
            }
        }).start();
    }

    private void importData() {
        Log.d(TAG, "Starting data import");
        
        // Simulate data import process
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate import time
                
                runOnUiThread(() -> {
                    Log.d(TAG, "Data import completed successfully");
                    // In a real app, would show success message and refresh data
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e(TAG, "Data import interrupted", e);
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.securevault.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class BackupActivity extends AppCompatActivity {
    private StorageManager storageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        storageManager = new StorageManager(this);

        Button btnExportData = findViewById(R.id.btnExportData);
        Button btnImportData = findViewById(R.id.btnImportData);

        btnExportData.setOnClickListener(v -> {
            exportData();
        });

        btnImportData.setOnClickListener(v -> {
            importData();
        });
    }

    private void exportData() {
        try {
            // Generate backup key using insecure PRNG
            String backupKey = CryptoManager.generateBackupKey();

            List<PasswordEntry> passwords = storageManager.getPasswordEntries();
            List<SecureNote> notes = storageManager.getSecureNotes();

            // In a real app, this would create an encrypted backup file
            // For this demo, we just show a success message
            Toast.makeText(this, "Backup created successfully with key: " + backupKey.substring(0, 8) + "...", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void importData() {
        // In a real app, this would import from a backup file
        Toast.makeText(this, "Import functionality not implemented in demo", Toast.LENGTH_SHORT).show();
    }
}

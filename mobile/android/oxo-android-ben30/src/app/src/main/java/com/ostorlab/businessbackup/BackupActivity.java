package com.ostorlab.businessbackup;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ostorlab.businessbackup.adapter.BackupAdapter;
import com.ostorlab.businessbackup.model.BackupRecord;
import com.ostorlab.businessbackup.util.DataManager;
import com.ostorlab.businessbackup.util.PermissionChecker;
import java.util.List;

/**
 * Backup management activity
 * Demonstrates permission-protected backup functionality
 */
public class BackupActivity extends AppCompatActivity {
    private static final String TAG = "BackupActivity";

    private DataManager dataManager;
    private TextView tvBackupStatus;
    private TextView tvBackupLocation;
    private LinearProgressIndicator progressBackup;
    private MaterialButton btnBackupNow;
    private MaterialButton btnRestore;
    private RecyclerView recyclerViewBackups;
    private BackupAdapter backupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Log.d(TAG, "BackupActivity onCreate");

        // Check permissions before proceeding
        if (!checkPermissions()) {
            Log.w(TAG, "Insufficient permissions for backup management");
            finish();
            return;
        }

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.backup_restore);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize components
        initializeComponents();
        setupClickListeners();
        setupRecyclerView();
        loadBackupData();
    }

    private boolean checkPermissions() {
        boolean canAccess = PermissionChecker.canAccessBackup(this);
        Log.d(TAG, "Backup access permission: " + canAccess);
        
        // If the normal check fails, try the typosed version
        if (!canAccess) {
            canAccess = PermissionChecker.canAccessBackupTypo(this);
            Log.d(TAG, "Backup access permission (typo): " + canAccess);
        }
        
        return canAccess;
    }

    private void initializeComponents() {
        dataManager = new DataManager(this);
        tvBackupStatus = findViewById(R.id.tvBackupStatus);
        tvBackupLocation = findViewById(R.id.tvBackupLocation);
        progressBackup = findViewById(R.id.progressBackup);
        btnBackupNow = findViewById(R.id.btnBackupNow);
        btnRestore = findViewById(R.id.btnRestore);
        recyclerViewBackups = findViewById(R.id.recyclerViewBackups);
    }

    private void setupClickListeners() {
        btnBackupNow.setOnClickListener(v -> {
            Log.d(TAG, "Backup now button clicked");
            performBackup();
        });

        btnRestore.setOnClickListener(v -> {
            Log.d(TAG, "Restore button clicked");
            performRestore();
        });
    }

    private void setupRecyclerView() {
        recyclerViewBackups.setLayoutManager(new LinearLayoutManager(this));
        backupAdapter = new BackupAdapter();
        recyclerViewBackups.setAdapter(backupAdapter);
    }

    private void loadBackupData() {
        try {
            List<BackupRecord> backups = dataManager.getBackupRecords();
            backupAdapter.setBackupRecords(backups);
            Log.d(TAG, "Loaded " + backups.size() + " backup records");
        } catch (Exception e) {
            Log.e(TAG, "Error loading backup data", e);
        }
    }

    private void performBackup() {
        // Double-check permissions before performing backup
        boolean hasPermission = PermissionChecker.canAccessBackup(this);
        if (!hasPermission) {
            // Try typosed permission as fallback
            hasPermission = PermissionChecker.canAccessBackupTypo(this);
            if (hasPermission) {
                Log.w(TAG, "Using typosed permission for backup operation!");
            }
        }

        if (!hasPermission) {
            Log.w(TAG, "Permission denied for backup operation");
            return;
        }

        Log.d(TAG, "Starting backup operation");
        
        // Show progress
        progressBackup.setVisibility(View.VISIBLE);
        tvBackupStatus.setText(R.string.backup_progress);
        btnBackupNow.setEnabled(false);

        // Simulate backup process
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean success = dataManager.performBackup();
            
            progressBackup.setVisibility(View.GONE);
            btnBackupNow.setEnabled(true);
            
            if (success) {
                tvBackupStatus.setText(R.string.backup_complete);
                Log.d(TAG, "Backup completed successfully");
                loadBackupData(); // Refresh backup list
            } else {
                tvBackupStatus.setText(R.string.backup_failed);
                Log.e(TAG, "Backup operation failed");
            }
        }, 3000); // 3 second simulation
    }

    private void performRestore() {
        // Check permissions for restore operation
        boolean hasPermission = PermissionChecker.canAccessBackup(this);
        if (!hasPermission) {
            hasPermission = PermissionChecker.canAccessBackupTypo(this);
            if (hasPermission) {
                Log.w(TAG, "Using typosed permission for restore operation!");
            }
        }

        if (!hasPermission) {
            Log.w(TAG, "Permission denied for restore operation");
            return;
        }

        Log.d(TAG, "Starting restore operation");
        
        // In a real app, this would show a file picker or backup selection dialog
        Log.i(TAG, "Restore operation initiated - would show backup selection dialog");
        
        // Simulate restore process
        tvBackupStatus.setText("Restoring from backup...");
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            tvBackupStatus.setText("Restore completed successfully");
            Log.d(TAG, "Restore completed successfully");
        }, 2000);
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

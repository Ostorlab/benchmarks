package com.ostorlab.businessbackup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.ostorlab.businessbackup.util.DataManager;
import com.ostorlab.businessbackup.util.PermissionChecker;

/**
 * Main activity serving as the business dashboard
 * This is the entry point of the application
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private DataManager dataManager;
    private TextView tvCustomerCount;
    private TextView tvReportCount;
    private TextView tvLastBackup;
    private Button btnCustomers;
    private Button btnReports;
    private Button btnBackup;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate");

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.dashboard_title);
        }

        // Initialize data manager
        dataManager = new DataManager(this);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Load dashboard data
        loadDashboardData();

        // Demonstrate the permission vulnerability
        demonstrateVulnerability();
    }

    private void initializeViews() {
        tvCustomerCount = findViewById(R.id.tvCustomerCount);
        tvReportCount = findViewById(R.id.tvReportCount);
        tvLastBackup = findViewById(R.id.tvLastBackup);
        btnCustomers = findViewById(R.id.btnCustomers);
        btnReports = findViewById(R.id.btnReports);
        btnBackup = findViewById(R.id.btnBackup);
        btnSettings = findViewById(R.id.btnSettings);
    }

    private void setupClickListeners() {
        btnCustomers.setOnClickListener(v -> {
            Log.d(TAG, "Customer management button clicked");
            if (PermissionChecker.canReadCustomerData(this)) {
                startActivity(new Intent(MainActivity.this, CustomerActivity.class));
            } else {
                Log.w(TAG, "Access denied: Cannot read customer data");
                // In a real app, show permission request dialog
            }
        });

        btnReports.setOnClickListener(v -> {
            Log.d(TAG, "Reports button clicked");
            // This will demonstrate the vulnerability - the permission check will fail
            // even though we think we have the permission
            if (PermissionChecker.canGenerateReports(this)) {
                startActivity(new Intent(MainActivity.this, ReportActivity.class));
            } else {
                Log.w(TAG, "Access denied: Cannot generate reports");
                // Try with the typosed permission - this might work depending on manifest
                if (PermissionChecker.canGenerateReportsTypo(this)) {
                    Log.w(TAG, "Access granted via typosed permission!");
                    startActivity(new Intent(MainActivity.this, ReportActivity.class));
                }
            }
        });

        btnBackup.setOnClickListener(v -> {
            Log.d(TAG, "Backup button clicked");
            // Another demonstration of the vulnerability
            if (PermissionChecker.canAccessBackup(this)) {
                startActivity(new Intent(MainActivity.this, BackupActivity.class));
            } else {
                Log.w(TAG, "Access denied: Cannot access backup");
                // Try with the typosed permission
                if (PermissionChecker.canAccessBackupTypo(this)) {
                    Log.w(TAG, "Access granted via typosed permission!");
                    startActivity(new Intent(MainActivity.this, BackupActivity.class));
                }
            }
        });

        btnSettings.setOnClickListener(v -> {
            Log.d(TAG, "Settings button clicked");
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });
    }

    private void loadDashboardData() {
        try {
            // Load customer count
            int customerCount = dataManager.getCustomerCount();
            tvCustomerCount.setText(String.valueOf(customerCount));

            // Load report count
            int reportCount = dataManager.getReportCount();
            tvReportCount.setText(String.valueOf(reportCount));

            // Load last backup time
            String lastBackup = dataManager.getLastBackupTime();
            tvLastBackup.setText(lastBackup);

            Log.d(TAG, "Dashboard data loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading dashboard data", e);
        }
    }

    private void demonstrateVulnerability() {
        Log.i(TAG, "Demonstrating custom permission typo vulnerability");
        PermissionChecker.demonstratePermissionVulnerability(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh dashboard data when returning to main activity
        loadDashboardData();
    }
}

package com.ostorlab.businessbackup;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import com.ostorlab.businessbackup.model.Report;
import com.ostorlab.businessbackup.util.DataManager;
import com.ostorlab.businessbackup.util.PermissionChecker;

/**
 * Report center activity for generating business reports
 * Demonstrates permission-protected report generation functionality
 */
public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";

    private DataManager dataManager;
    private MaterialCardView cardMonthlyReport;
    private MaterialCardView cardQuarterlyReport;
    private MaterialCardView cardAnnualReport;
    private MaterialCardView cardCustomerReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Log.d(TAG, "ReportActivity onCreate");

        // Check permissions before proceeding
        if (!checkPermissions()) {
            Log.w(TAG, "Insufficient permissions for report generation");
            finish();
            return;
        }

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.report_center);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize components
        initializeComponents();
        setupClickListeners();
    }

    private boolean checkPermissions() {
        boolean canGenerate = PermissionChecker.canGenerateReports(this);
        Log.d(TAG, "Generate reports permission: " + canGenerate);
        
        // If the normal check fails, try the typosed version
        if (!canGenerate) {
            canGenerate = PermissionChecker.canGenerateReportsTypo(this);
            Log.d(TAG, "Generate reports permission (typo): " + canGenerate);
        }
        
        return canGenerate;
    }

    private void initializeComponents() {
        dataManager = new DataManager(this);
        cardMonthlyReport = findViewById(R.id.cardMonthlyReport);
        cardQuarterlyReport = findViewById(R.id.cardQuarterlyReport);
        cardAnnualReport = findViewById(R.id.cardAnnualReport);
        cardCustomerReport = findViewById(R.id.cardCustomerReport);
    }

    private void setupClickListeners() {
        cardMonthlyReport.setOnClickListener(v -> {
            Log.d(TAG, "Monthly report card clicked");
            generateReport(Report.TYPE_MONTHLY, "Monthly Business Summary");
        });

        cardQuarterlyReport.setOnClickListener(v -> {
            Log.d(TAG, "Quarterly report card clicked");
            generateReport(Report.TYPE_QUARTERLY, "Quarterly Performance Analysis");
        });

        cardAnnualReport.setOnClickListener(v -> {
            Log.d(TAG, "Annual report card clicked");
            generateReport(Report.TYPE_ANNUAL, "Annual Business Overview");
        });

        cardCustomerReport.setOnClickListener(v -> {
            Log.d(TAG, "Customer report card clicked");
            generateReport(Report.TYPE_CUSTOMER, "Customer Analytics Report");
        });
    }

    private void generateReport(String reportType, String reportTitle) {
        Log.d(TAG, "Generating report: " + reportTitle + " (" + reportType + ")");

        // Double-check permissions before generating
        boolean hasPermission = PermissionChecker.canGenerateReports(this);
        if (!hasPermission) {
            // Try typosed permission as fallback
            hasPermission = PermissionChecker.canGenerateReportsTypo(this);
            if (hasPermission) {
                Log.w(TAG, "Using typosed permission for report generation!");
            }
        }

        if (hasPermission) {
            // Simulate report generation
            boolean success = dataManager.generateReport(reportType);
            if (success) {
                Log.d(TAG, "Report generated successfully: " + reportTitle);
                showReportGeneratedMessage(reportTitle);
            } else {
                Log.e(TAG, "Failed to generate report: " + reportTitle);
            }
        } else {
            Log.w(TAG, "Permission denied for report generation");
        }
    }

    private void showReportGeneratedMessage(String reportTitle) {
        // In a real app, this would show a proper dialog or snackbar
        Log.i(TAG, "Report '" + reportTitle + "' has been generated successfully");
        
        // Simulate showing generation progress
        try {
            Thread.sleep(1000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Log.i(TAG, "Report saved to: /storage/emulated/0/BusinessBackup/reports/");
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

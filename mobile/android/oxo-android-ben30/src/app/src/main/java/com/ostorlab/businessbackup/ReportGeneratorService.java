package com.ostorlab.businessbackup;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.ostorlab.businessbackup.util.PermissionChecker;

/**
 * Service for generating business reports
 * This service demonstrates the permission checking vulnerability
 */
public class ReportGeneratorService extends Service {
    private static final String TAG = "ReportGeneratorService";

    public static final String ACTION_GENERATE_REPORT = "com.ostorlab.businessbackup.action.GENERATE_REPORT";
    public static final String EXTRA_REPORT_TYPE = "report_type";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ReportGeneratorService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ReportGeneratorService started");
        
        // Check permissions before processing
        if (PermissionChecker.canGenerateReports(this)) {
            Log.d(TAG, "Service has report generation permission");
            generateReport(intent);
        } else {
            Log.w(TAG, "Service does not have report generation permission");
            
            // Try with the typosed permission as fallback
            if (PermissionChecker.canGenerateReportsTypo(this)) {
                Log.w(TAG, "Service has typosed report permission - proceeding with caution");
                generateReport(intent);
            } else {
                Log.e(TAG, "Service has no valid report permissions");
            }
        }
        
        return START_NOT_STICKY;
    }

    private void generateReport(Intent intent) {
        if (intent == null) {
            return;
        }
        
        String reportType = intent.getStringExtra(EXTRA_REPORT_TYPE);
        if (reportType == null) {
            reportType = "default";
        }
        
        Log.d(TAG, "Generating report of type: " + reportType);
        
        final String finalReportType = reportType; // Make it final for lambda
        
        // Simulate report generation
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Simulate generation time
                Log.d(TAG, "Report generation completed for type: " + finalReportType);
                
                // Send broadcast when complete
                Intent broadcastIntent = new Intent("com.ostorlab.businessbackup.action.REPORT_COMPLETE");
                broadcastIntent.putExtra("report_type", finalReportType);
                broadcastIntent.putExtra("success", true);
                sendBroadcast(broadcastIntent);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e(TAG, "Report generation interrupted", e);
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ReportGeneratorService destroyed");
    }
}

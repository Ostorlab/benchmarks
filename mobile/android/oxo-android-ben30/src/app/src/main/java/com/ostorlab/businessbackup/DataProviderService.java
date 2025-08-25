package com.ostorlab.businessbackup;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.ostorlab.businessbackup.util.PermissionChecker;

/**
 * Service for providing customer data operations
 * This service is protected by a custom permission with a typo in the manifest
 */
public class DataProviderService extends Service {
    private static final String TAG = "DataProviderService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "DataProviderService created");
        
        // Demonstrate the permission vulnerability
        demonstratePermissionIssue();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "DataProviderService started");
        
        // Check if we have the correct permission
        if (PermissionChecker.canWriteCustomerData(this)) {
            Log.d(TAG, "Service has write customer data permission");
            processDataRequest(intent);
        } else {
            Log.w(TAG, "Service does not have write customer data permission");
            
            // Try with the typosed permission
            if (PermissionChecker.canWriteCustomerDataTypo(this)) {
                Log.w(TAG, "Service has typosed write permission - potential security issue!");
                processDataRequest(intent);
            } else {
                Log.e(TAG, "Service has no valid permissions");
            }
        }
        
        return START_NOT_STICKY;
    }

    private void demonstratePermissionIssue() {
        Log.w(TAG, "=== DATA PROVIDER SERVICE PERMISSION ISSUE ===");
        Log.w(TAG, "This service is declared in manifest with permission:");
        Log.w(TAG, "com.ostorlab.businessbackup.permission.WRIT_CUSTOMER_DATA (TYPO!)");
        Log.w(TAG, "But the correct permission is:");
        Log.w(TAG, "com.ostorlab.businessbackup.permission.WRITE_CUSTOMER_DATA");
        Log.w(TAG, "This mismatch can lead to security vulnerabilities");
        Log.w(TAG, "===============================================");
    }

    private void processDataRequest(Intent intent) {
        if (intent == null) {
            return;
        }
        
        String action = intent.getAction();
        Log.d(TAG, "Processing data request with action: " + action);
        
        // Simulate data processing
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate processing time
                Log.d(TAG, "Data processing completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e(TAG, "Data processing interrupted", e);
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "DataProviderService bind requested");
        return null; // Not a bound service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "DataProviderService destroyed");
    }
}

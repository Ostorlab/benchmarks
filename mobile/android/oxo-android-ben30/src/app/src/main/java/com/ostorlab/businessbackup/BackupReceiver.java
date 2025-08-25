package com.ostorlab.businessbackup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ostorlab.businessbackup.util.PermissionChecker;

/**
 * Broadcast receiver for handling backup completion events
 * This receiver demonstrates the permission mismatch vulnerability
 */
public class BackupReceiver extends BroadcastReceiver {
    private static final String TAG = "BackupReceiver";

    public static final String ACTION_BACKUP_COMPLETE = "com.ostorlab.businessbackup.action.BACKUP_COMPLETE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "BackupReceiver received broadcast");
        
        if (intent == null) {
            Log.w(TAG, "Received null intent");
            return;
        }
        
        String action = intent.getAction();
        Log.d(TAG, "Received action: " + action);
        
        // Demonstrate the permission vulnerability
        demonstratePermissionMismatch(context);
        
        if (ACTION_BACKUP_COMPLETE.equals(action)) {
            handleBackupComplete(context, intent);
        }
    }

    private void demonstratePermissionMismatch(Context context) {
        Log.w(TAG, "=== BACKUP RECEIVER PERMISSION MISMATCH ===");
        Log.w(TAG, "Receiver declared in manifest with permission:");
        Log.w(TAG, "com.ostorlab.businessbackup.permission.BACKUP_ACCESS (CORRECT)");
        Log.w(TAG, "But app requests permission:");
        Log.w(TAG, "com.ostorlab.businessbackup.permission.BACKUP_ACCES (TYPO!)");
        
        boolean correctPermission = PermissionChecker.canAccessBackup(context);
        boolean typoPermission = PermissionChecker.canAccessBackupTypo(context);
        
        Log.w(TAG, "Correct permission check: " + correctPermission);
        Log.w(TAG, "Typo permission check: " + typoPermission);
        Log.w(TAG, "This mismatch can prevent proper broadcast delivery");
        Log.w(TAG, "==========================================");
    }

    private void handleBackupComplete(Context context, Intent intent) {
        boolean success = intent.getBooleanExtra("success", false);
        String backupPath = intent.getStringExtra("backup_path");
        long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
        
        Log.d(TAG, "Backup completed - Success: " + success);
        Log.d(TAG, "Backup path: " + backupPath);
        Log.d(TAG, "Timestamp: " + timestamp);
        
        if (success) {
            // Process successful backup
            processSuccessfulBackup(context, backupPath, timestamp);
        } else {
            // Handle backup failure
            processFailedBackup(context);
        }
    }

    private void processSuccessfulBackup(Context context, String backupPath, long timestamp) {
        Log.d(TAG, "Processing successful backup");
        
        // In a real app, this might:
        // - Update backup database
        // - Send notification to user
        // - Update backup statistics
        // - Schedule next backup
        
        Log.d(TAG, "Backup processing completed successfully");
    }

    private void processFailedBackup(Context context) {
        Log.w(TAG, "Processing failed backup");
        
        // In a real app, this might:
        // - Log error details
        // - Retry backup
        // - Notify user of failure
        // - Clean up incomplete backup files
        
        Log.w(TAG, "Backup failure processing completed");
    }
}

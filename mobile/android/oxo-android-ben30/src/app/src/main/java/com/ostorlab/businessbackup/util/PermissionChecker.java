package com.ostorlab.businessbackup.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Utility class for checking custom permissions
 * This demonstrates the vulnerability where permission checks may fail due to typos
 */
public class PermissionChecker {
    private static final String TAG = "PermissionChecker";

    // Correct permission names (as declared in manifest)
    public static final String PERMISSION_READ_CUSTOMER_DATA = "com.ostorlab.businessbackup.permission.READ_CUSTOMER_DATA";
    public static final String PERMISSION_WRITE_CUSTOMER_DATA = "com.ostorlab.businessbackup.permission.WRITE_CUSTOMER_DATA";
    public static final String PERMISSION_GENERATE_REPORTS = "com.ostorlab.businessbackup.permission.GENERATE_REPORTS";
    public static final String PERMISSION_BACKUP_ACCESS = "com.ostorlab.businessbackup.permission.BACKUP_ACCESS";

    // Typosed permission names (as used in manifest uses-permission tags)
    private static final String PERMISSION_WRIT_CUSTOMER_DATA = "com.ostorlab.businessbackup.permission.WRIT_CUSTOMER_DATA";
    private static final String PERMISSION_GENERAT_REPORTS = "com.ostorlab.businessbackup.permission.GENERAT_REPORTS";
    private static final String PERMISSION_BACKUP_ACCES = "com.ostorlab.businessbackup.permission.BACKUP_ACCES";

    /**
     * Check if the app has permission to read customer data
     * This will work correctly since the READ permission doesn't have a typo
     */
    public static boolean canReadCustomerData(Context context) {
        int result = context.checkSelfPermission(PERMISSION_READ_CUSTOMER_DATA);
        Log.d(TAG, "Checking READ_CUSTOMER_DATA permission: " + (result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if the app has permission to write customer data
     * VULNERABILITY: This check uses the correct permission name, but the manifest
     * declares it with a typo, so this will fail even though we think we have the permission
     */
    public static boolean canWriteCustomerData(Context context) {
        int result = context.checkSelfPermission(PERMISSION_WRITE_CUSTOMER_DATA);
        Log.d(TAG, "Checking WRITE_CUSTOMER_DATA permission: " + (result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        
        // VULNERABILITY: This will return false because the manifest uses the typosed version
        // but the app components are protected with the correct version
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Alternative write permission check using the typosed version
     * This demonstrates how an attacker might exploit the typo
     */
    public static boolean canWriteCustomerDataTypo(Context context) {
        int result = context.checkSelfPermission(PERMISSION_WRIT_CUSTOMER_DATA);
        Log.d(TAG, "Checking WRIT_CUSTOMER_DATA permission (typo): " + (result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if the app has permission to generate reports
     * VULNERABILITY: Same issue - correct permission check but typosed declaration
     */
    public static boolean canGenerateReports(Context context) {
        int result = context.checkSelfPermission(PERMISSION_GENERATE_REPORTS);
        Log.d(TAG, "Checking GENERATE_REPORTS permission: " + (result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check reports permission using the typosed version
     */
    public static boolean canGenerateReportsTypo(Context context) {
        int result = context.checkSelfPermission(PERMISSION_GENERAT_REPORTS);
        Log.d(TAG, "Checking GENERAT_REPORTS permission (typo): " + (result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if the app has permission to access backup functionality
     * VULNERABILITY: Same issue - correct permission check but typosed declaration
     */
    public static boolean canAccessBackup(Context context) {
        int result = context.checkSelfPermission(PERMISSION_BACKUP_ACCESS);
        Log.d(TAG, "Checking BACKUP_ACCESS permission: " + (result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check backup permission using the typosed version
     */
    public static boolean canAccessBackupTypo(Context context) {
        int result = context.checkSelfPermission(PERMISSION_BACKUP_ACCES);
        Log.d(TAG, "Checking BACKUP_ACCES permission (typo): " + (result == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Demonstrate the permission vulnerability by showing both checks
     */
    public static void demonstratePermissionVulnerability(Context context) {
        Log.w(TAG, "=== PERMISSION VULNERABILITY DEMONSTRATION ===");
        
        Log.w(TAG, "Write Customer Data - Correct check: " + canWriteCustomerData(context));
        Log.w(TAG, "Write Customer Data - Typo check: " + canWriteCustomerDataTypo(context));
        
        Log.w(TAG, "Generate Reports - Correct check: " + canGenerateReports(context));
        Log.w(TAG, "Generate Reports - Typo check: " + canGenerateReportsTypo(context));
        
        Log.w(TAG, "Backup Access - Correct check: " + canAccessBackup(context));
        Log.w(TAG, "Backup Access - Typo check: " + canAccessBackupTypo(context));
        
        Log.w(TAG, "=== END VULNERABILITY DEMONSTRATION ===");
    }
}

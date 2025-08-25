package com.ostorlab.businessbackup.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.ostorlab.businessbackup.model.Customer;
import com.ostorlab.businessbackup.model.BackupRecord;
import com.ostorlab.businessbackup.model.Report;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Data manager for handling local storage and mock data generation
 * This provides realistic business data for the application
 */
public class DataManager {
    private static final String PREFS_NAME = "BusinessBackupPrefs";
    private static final String KEY_CUSTOMER_COUNT = "customer_count";
    private static final String KEY_REPORT_COUNT = "report_count";
    private static final String KEY_LAST_BACKUP = "last_backup";

    private Context context;
    private SharedPreferences preferences;

    public DataManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get sample customer data
     */
    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        
        Customer customer1 = new Customer("John Smith", "john.smith@abc.com", "+1 (555) 123-4567", "ABC Corporation");
        customer1.setId(1);
        
        Customer customer2 = new Customer("Sarah Johnson", "sarah.johnson@xyz.com", "+1 (555) 234-5678", "XYZ Industries");
        customer2.setId(2);
        
        Customer customer3 = new Customer("Michael Brown", "m.brown@techsol.com", "+1 (555) 345-6789", "TechSolutions Ltd");
        customer3.setId(3);
        
        Customer customer4 = new Customer("Emily Davis", "emily.davis@innovate.com", "+1 (555) 456-7890", "Innovate Inc");
        customer4.setId(4);
        
        Customer customer5 = new Customer("David Wilson", "david.wilson@globalcorp.com", "+1 (555) 567-8901", "Global Corp");
        customer5.setId(5);

        customers.add(customer1);
        customers.add(customer2);
        customers.add(customer3);
        customers.add(customer4);
        customers.add(customer5);
        
        return customers;
    }

    /**
     * Get sample backup records
     */
    public List<BackupRecord> getBackupRecords() {
        List<BackupRecord> backups = new ArrayList<>();
        
        long now = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000L;
        
        BackupRecord backup1 = new BackupRecord("Backup_2024_01_15_15_45", "/storage/emulated/0/BusinessBackup/backup_20240115_1545.bak", 2560000);
        backup1.setId(1);
        backup1.setTimestamp(now - oneDay);
        backup1.setStatus(BackupRecord.STATUS_COMPLETED);
        backup1.setDescription("Daily automatic backup");
        
        BackupRecord backup2 = new BackupRecord("Backup_2024_01_14_15_45", "/storage/emulated/0/BusinessBackup/backup_20240114_1545.bak", 2450000);
        backup2.setId(2);
        backup2.setTimestamp(now - (2 * oneDay));
        backup2.setStatus(BackupRecord.STATUS_COMPLETED);
        backup2.setDescription("Daily automatic backup");
        
        BackupRecord backup3 = new BackupRecord("Backup_2024_01_13_15_45", "/storage/emulated/0/BusinessBackup/backup_20240113_1545.bak", 2340000);
        backup3.setId(3);
        backup3.setTimestamp(now - (3 * oneDay));
        backup3.setStatus(BackupRecord.STATUS_COMPLETED);
        backup3.setDescription("Daily automatic backup");

        backups.add(backup1);
        backups.add(backup2);
        backups.add(backup3);
        
        return backups;
    }

    /**
     * Get sample reports
     */
    public List<Report> getReports() {
        List<Report> reports = new ArrayList<>();
        
        long now = System.currentTimeMillis();
        long oneWeek = 7 * 24 * 60 * 60 * 1000L;
        
        Report report1 = new Report("Monthly Customer Analysis", Report.TYPE_MONTHLY);
        report1.setId(1);
        report1.setGeneratedAt(now - oneWeek);
        report1.setStatus(Report.STATUS_GENERATED);
        report1.setCustomerCount(125);
        report1.setFilePath("/storage/emulated/0/BusinessBackup/reports/monthly_202401.pdf");
        
        Report report2 = new Report("Q4 2023 Business Summary", Report.TYPE_QUARTERLY);
        report2.setId(2);
        report2.setGeneratedAt(now - (2 * oneWeek));
        report2.setStatus(Report.STATUS_GENERATED);
        report2.setCustomerCount(118);
        report2.setFilePath("/storage/emulated/0/BusinessBackup/reports/quarterly_q4_2023.pdf");
        
        Report report3 = new Report("Annual Performance Review", Report.TYPE_ANNUAL);
        report3.setId(3);
        report3.setGeneratedAt(now - (4 * oneWeek));
        report3.setStatus(Report.STATUS_GENERATED);
        report3.setCustomerCount(95);
        report3.setFilePath("/storage/emulated/0/BusinessBackup/reports/annual_2023.pdf");

        reports.add(report1);
        reports.add(report2);
        reports.add(report3);
        
        return reports;
    }

    /**
     * Get customer count for dashboard
     */
    public int getCustomerCount() {
        return preferences.getInt(KEY_CUSTOMER_COUNT, 125);
    }

    /**
     * Set customer count
     */
    public void setCustomerCount(int count) {
        preferences.edit().putInt(KEY_CUSTOMER_COUNT, count).apply();
    }

    /**
     * Get report count for dashboard
     */
    public int getReportCount() {
        return preferences.getInt(KEY_REPORT_COUNT, 42);
    }

    /**
     * Set report count
     */
    public void setReportCount(int count) {
        preferences.edit().putInt(KEY_REPORT_COUNT, count).apply();
    }

    /**
     * Get last backup timestamp
     */
    public String getLastBackupTime() {
        long timestamp = preferences.getLong(KEY_LAST_BACKUP, System.currentTimeMillis() - (24 * 60 * 60 * 1000L));
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - h:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Update last backup time
     */
    public void updateLastBackupTime() {
        preferences.edit().putLong(KEY_LAST_BACKUP, System.currentTimeMillis()).apply();
    }

    /**
     * Add a new customer (simulation)
     */
    public boolean addCustomer(Customer customer) {
        // In a real app, this would save to database
        // For this demo, we just increment the counter
        setCustomerCount(getCustomerCount() + 1);
        return true;
    }

    /**
     * Generate a new report (simulation)
     */
    public boolean generateReport(String type) {
        // In a real app, this would create the actual report
        // For this demo, we just increment the counter
        setReportCount(getReportCount() + 1);
        return true;
    }

    /**
     * Perform backup operation (simulation)
     */
    public boolean performBackup() {
        // In a real app, this would perform the actual backup
        // For this demo, we just update the timestamp
        updateLastBackupTime();
        return true;
    }

    /**
     * Clear all data (for testing)
     */
    public void clearAllData() {
        preferences.edit().clear().apply();
    }
}

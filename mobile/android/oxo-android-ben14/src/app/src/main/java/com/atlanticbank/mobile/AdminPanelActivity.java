package com.atlanticbank.mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    private Button buttonViewUsers;
    private Button buttonDeleteUser;
    private Button buttonServerConfig;
    private Button buttonDatabaseAccess;
    private Button buttonSystemLogs;
    private Button buttonEmergencyShutdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Initialize all the UI components
     */
    private void initializeViews() {
        buttonViewUsers = findViewById(R.id.buttonViewUsers);
        buttonDeleteUser = findViewById(R.id.buttonDeleteUser);
        buttonServerConfig = findViewById(R.id.buttonServerConfig);
        buttonDatabaseAccess = findViewById(R.id.buttonDatabaseAccess);
        buttonSystemLogs = findViewById(R.id.buttonSystemLogs);
        buttonEmergencyShutdown = findViewById(R.id.buttonEmergencyShutdown);
    }

    /**
     * Set up click listeners for all admin functions
     */
    private void setupClickListeners() {
        buttonViewUsers.setOnClickListener(v -> viewAllUsers());
        buttonDeleteUser.setOnClickListener(v -> deleteUser());
        buttonServerConfig.setOnClickListener(v -> accessServerConfig());
        buttonDatabaseAccess.setOnClickListener(v -> accessDatabase());
        buttonSystemLogs.setOnClickListener(v -> viewSystemLogs());
        buttonEmergencyShutdown.setOnClickListener(v -> emergencyShutdown());
    }

    /**
     * THE VULNERABILITY: Admin functions accessible without authentication
     */
    private void viewAllUsers() {
        String userData = "User Database\n\n" +
                "1. john.doe@atlantabank.com (Admin)\n" +
                "   Account: ****1234\n" +
                "   Balance: $125,450.67\n\n" +
                "2. jane.smith@atlantabank.com (User)\n" +
                "   Account: ****5678\n" +
                "   Balance: $75,230.45\n\n" +
                "3. admin@atlantabank.com (Super Admin)\n" +
                "   Account: ****9012\n" +
                "   Balance: $1,500,000.00";

        new AlertDialog.Builder(this)
                .setTitle("User Management")
                .setMessage(userData)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Delete user functionality without proper authorization
     */
    private void deleteUser() {
        new AlertDialog.Builder(this)
                .setTitle("Delete User Account")
                .setMessage("Select user account to delete:\n\n" +
                        "Warning: This action is irreversible and will:\n" +
                        "• Delete all user data\n" +
                        "• Close all accounts\n" +
                        "• Transfer remaining funds to system account")
                .setPositiveButton("DELETE JOHN DOE", (dialog, which) -> {
                    Toast.makeText(this, "User 'john.doe@atlantabank.com' has been deleted", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("DELETE JANE SMITH", (dialog, which) -> {
                    Toast.makeText(this, "User 'jane.smith@atlantabank.com' has been deleted", Toast.LENGTH_LONG).show();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    /**
     * Server configuration access
     */
    private void accessServerConfig() {
        String configData = "Server Configuration\n\n" +
                "Database Host: prod-db-01.atlantabank.internal\n" +
                "Admin Password: SuperSecret123!\n" +
                "API Keys: sk_live_abc123xyz789\n" +
                "Encryption Key: VB_2024_SECRET_KEY\n" +
                "Debug Mode: ENABLED\n" +
                "Logging Level: ALL";

        new AlertDialog.Builder(this)
                .setTitle("Server Configuration")
                .setMessage(configData)
                .setPositiveButton("MODIFY CONFIG", (dialog, which) -> {
                    Toast.makeText(this, "Server configuration has been updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Database access functionality
     */
    private void accessDatabase() {
        String dbInfo = "Database Access\n\n" +
                "Production Database: CONNECTED\n" +
                "Tables: 47 active tables\n" +
                "Records: 2,847,293 customer records\n" +
                "Permissions: FULL READ/WRITE/DELETE\n\n" +
                "Recent Queries:\n" +
                "• SELECT * FROM customer_accounts\n" +
                "• SELECT * FROM transaction_history\n" +
                "• UPDATE user_balances SET balance = 0";

        new AlertDialog.Builder(this)
                .setTitle("Database Management")
                .setMessage(dbInfo)
                .setPositiveButton("EXPORT DATA", (dialog, which) -> {
                    Toast.makeText(this, "Exporting customer database...", Toast.LENGTH_SHORT).show();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Toast.makeText(this, "Database export completed successfully", Toast.LENGTH_LONG).show();
                    }, 2000);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * System logs access
     */
    private void viewSystemLogs() {
        String logs = "System Logs (Last 24 Hours)\n\n" +
                "[INFO] User login: john.doe@atlantabank.com\n" +
                "[WARNING] Failed login attempt detected\n" +
                "[INFO] Large transfer processed: $45,000\n" +
                "[ERROR] Database connection timeout\n" +
                "[INFO] API key accessed: sk_live_abc123xyz789\n" +
                "[WARNING] Admin panel accessed\n" +
                "[INFO] Backup completed successfully";

        new AlertDialog.Builder(this)
                .setTitle("System Logs")
                .setMessage(logs)
                .setPositiveButton("CLEAR LOGS", (dialog, which) -> {
                    Toast.makeText(this, "All system logs have been cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Emergency system shutdown
     */
    private void emergencyShutdown() {
        new AlertDialog.Builder(this)
                .setTitle("Emergency System Shutdown")
                .setMessage("WARNING: This will immediately:\n\n" +
                        "• Shutdown all banking services\n" +
                        "• Disconnect all active users\n" +
                        "• Stop all pending transactions\n" +
                        "• Lock all customer accounts\n" +
                        "• Disable ATM network\n\n" +
                        "This should only be used in extreme emergencies!")
                .setPositiveButton("SHUTDOWN SYSTEM", (dialog, which) -> {
                    showSystemShutdown();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Simulate system shutdown
     */
    private void showSystemShutdown() {
        Toast.makeText(this, "Initiating emergency shutdown...", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("System Shutdown Complete")
                    .setMessage("AtlantaBank System Status: OFFLINE\n\n" +
                            "• Banking services: DISABLED\n" +
                            "• Customer access: BLOCKED\n" +
                            "• ATM network: DISCONNECTED\n" +
                            "• Mobile app: OFFLINE\n\n" +
                            "System restart required for service restoration.")
                    .setPositiveButton("OK", null)
                    .setCancelable(false)
                    .show();
        }, 2000);
    }
}
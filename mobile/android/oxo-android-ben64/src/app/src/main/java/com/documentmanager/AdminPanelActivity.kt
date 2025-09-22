package com.documentmanager

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

/**
 * Administrative panel for document management system.
 * This activity is non-exported and should only be accessible to internal components.
 * Contains sensitive administrative functions.
 */
class AdminPanelActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("AdminPanel", "Admin panel accessed")
        
        // Show sensitive admin functionality
        showAdminPanel()
        
        // Process any admin commands from intent
        handleAdminIntent()
    }
    
    /**
     * Displays the administrative interface
     */
    private fun showAdminPanel() {
        // In a real app, this would show sensitive admin controls
        Toast.makeText(this, "🔓 ADMIN PANEL ACCESSED! Sensitive administrative functions available.", Toast.LENGTH_LONG).show()
        
        Log.i("AdminPanel", "Administrative panel initialized")
        Log.i("AdminPanel", "Available functions: User management, System configuration, Data export")
    }
    
    /**
     * Processes administrative commands from the intent
     */
    private fun handleAdminIntent() {
        val command = intent.getStringExtra("admin_command")
        val target = intent.getStringExtra("admin_target")
        
        if (command != null) {
            Log.w("AdminPanel", "Executing admin command: $command on target: $target")
            
            when (command) {
                "export_data" -> {
                    Log.w("AdminPanel", "⚠️ SENSITIVE: Exporting all user data to: $target")
                    Toast.makeText(this, "Exporting sensitive data...", Toast.LENGTH_SHORT).show()
                }
                "reset_passwords" -> {
                    Log.w("AdminPanel", "⚠️ SENSITIVE: Resetting all user passwords")
                    Toast.makeText(this, "Resetting user passwords...", Toast.LENGTH_SHORT).show()
                }
                "delete_logs" -> {
                    Log.w("AdminPanel", "⚠️ SENSITIVE: Deleting security logs")
                    Toast.makeText(this, "Clearing audit logs...", Toast.LENGTH_SHORT).show()
                }
                "grant_permissions" -> {
                    Log.w("AdminPanel", "⚠️ SENSITIVE: Granting admin permissions to: $target")
                    Toast.makeText(this, "Granting administrative access...", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.i("AdminPanel", "Unknown admin command: $command")
                    Toast.makeText(this, "Admin panel loaded", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Auto-finish after showing the admin panel
        finish()
    }
}

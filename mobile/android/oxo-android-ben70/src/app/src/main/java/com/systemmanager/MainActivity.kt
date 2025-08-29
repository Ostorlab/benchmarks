package com.systemmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupNavigation()
        updateDashboard()
    }
    
    private fun setupNavigation() {
        findViewById<Button>(R.id.btn_configuration).setOnClickListener {
            startActivity(Intent(this, ConfigurationActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_monitoring).setOnClickListener {
            startActivity(Intent(this, MonitoringActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_tools).setOnClickListener {
            startActivity(Intent(this, ToolsActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_reports).setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_preferences).setOnClickListener {
            startActivity(Intent(this, PreferencesActivity::class.java))
        }
    }
    
    private fun updateDashboard() {
        // Create system data files for the vulnerability
        val dataManager = SystemDataManager(this)
        dataManager.createSystemFiles()
    }
}

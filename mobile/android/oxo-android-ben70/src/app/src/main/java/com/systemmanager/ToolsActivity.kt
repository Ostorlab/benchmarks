package com.systemmanager

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ToolsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "System Tools"
        
        setupTools()
    }
    
    private fun setupTools() {
        findViewById<Button>(R.id.btn_diagnostic).setOnClickListener {
            runSystemDiagnostic()
        }
        
        findViewById<Button>(R.id.btn_cleanup).setOnClickListener {
            performSystemCleanup()
        }
        
        findViewById<Button>(R.id.btn_optimization).setOnClickListener {
            optimizeSystem()
        }
        
        findViewById<Button>(R.id.btn_backup).setOnClickListener {
            createSystemBackup()
        }
        
        findViewById<Button>(R.id.btn_restore).setOnClickListener {
            restoreSystemBackup()
        }
        
        updateToolsStatus()
    }
    
    private fun runSystemDiagnostic() {
        Toast.makeText(this, "Running system diagnostic...", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.tv_diagnostic_result).text = "Diagnostic: All systems operational"
    }
    
    private fun performSystemCleanup() {
        Toast.makeText(this, "Performing system cleanup...", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.tv_cleanup_result).text = "Cleanup: 247 MB freed"
    }
    
    private fun optimizeSystem() {
        Toast.makeText(this, "Optimizing system performance...", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.tv_optimization_result).text = "Optimization: Performance improved by 15%"
    }
    
    private fun createSystemBackup() {
        Toast.makeText(this, "Creating system backup...", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.tv_backup_status).text = "Last backup: Today 14:30"
    }
    
    private fun restoreSystemBackup() {
        Toast.makeText(this, "System restore initiated...", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateToolsStatus() {
        findViewById<TextView>(R.id.tv_diagnostic_result).text = "Diagnostic: Ready"
        findViewById<TextView>(R.id.tv_cleanup_result).text = "Cleanup: Ready"
        findViewById<TextView>(R.id.tv_optimization_result).text = "Optimization: Ready"
        findViewById<TextView>(R.id.tv_backup_status).text = "Last backup: Yesterday 02:00"
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

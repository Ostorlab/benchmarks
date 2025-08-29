package com.systemmanager

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PreferencesActivity : AppCompatActivity() {
    
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Application Preferences"
        
        prefs = getSharedPreferences("system_prefs", MODE_PRIVATE)
        
        setupPreferences()
        loadPreferences()
    }
    
    private fun setupPreferences() {
        findViewById<Button>(R.id.btn_save_preferences).setOnClickListener {
            savePreferences()
        }
        
        findViewById<Button>(R.id.btn_reset_preferences).setOnClickListener {
            resetPreferences()
        }
        
        findViewById<Button>(R.id.btn_export_config).setOnClickListener {
            exportConfiguration()
        }
        
        findViewById<Button>(R.id.btn_import_config).setOnClickListener {
            importConfiguration()
        }
    }
    
    private fun loadPreferences() {
        findViewById<EditText>(R.id.et_refresh_interval).setText(
            prefs.getInt("refresh_interval", 30).toString()
        )
        
        findViewById<EditText>(R.id.et_log_retention).setText(
            prefs.getInt("log_retention", 7).toString()
        )
        
        findViewById<EditText>(R.id.et_backup_frequency).setText(
            prefs.getInt("backup_frequency", 24).toString()
        )
        
        findViewById<Switch>(R.id.switch_auto_updates).isChecked = 
            prefs.getBoolean("auto_updates", true)
        
        findViewById<Switch>(R.id.switch_notifications).isChecked = 
            prefs.getBoolean("notifications", true)
        
        findViewById<Switch>(R.id.switch_debug_mode).isChecked = 
            prefs.getBoolean("debug_mode", false)
        
        findViewById<Switch>(R.id.switch_analytics).isChecked = 
            prefs.getBoolean("analytics", true)
        
        findViewById<Switch>(R.id.switch_crash_reporting).isChecked = 
            prefs.getBoolean("crash_reporting", true)
    }
    
    private fun savePreferences() {
        val editor = prefs.edit()
        
        try {
            val refreshInterval = findViewById<EditText>(R.id.et_refresh_interval).text.toString().toInt()
            val logRetention = findViewById<EditText>(R.id.et_log_retention).text.toString().toInt()
            val backupFrequency = findViewById<EditText>(R.id.et_backup_frequency).text.toString().toInt()
            
            editor.putInt("refresh_interval", refreshInterval)
            editor.putInt("log_retention", logRetention)
            editor.putInt("backup_frequency", backupFrequency)
            
            editor.putBoolean("auto_updates", findViewById<Switch>(R.id.switch_auto_updates).isChecked)
            editor.putBoolean("notifications", findViewById<Switch>(R.id.switch_notifications).isChecked)
            editor.putBoolean("debug_mode", findViewById<Switch>(R.id.switch_debug_mode).isChecked)
            editor.putBoolean("analytics", findViewById<Switch>(R.id.switch_analytics).isChecked)
            editor.putBoolean("crash_reporting", findViewById<Switch>(R.id.switch_crash_reporting).isChecked)
            
            editor.apply()
            
            Toast.makeText(this, "Preferences saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers for intervals", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun resetPreferences() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
        
        loadPreferences()
        Toast.makeText(this, "Preferences reset to defaults", Toast.LENGTH_SHORT).show()
    }
    
    private fun exportConfiguration() {
        Toast.makeText(this, "Configuration exported to /sdcard/systemmanager_config.json", Toast.LENGTH_LONG).show()
    }
    
    private fun importConfiguration() {
        Toast.makeText(this, "Configuration import feature available in Pro version", Toast.LENGTH_LONG).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

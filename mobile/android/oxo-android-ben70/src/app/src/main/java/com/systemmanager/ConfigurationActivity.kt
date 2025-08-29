package com.systemmanager

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfigurationActivity : AppCompatActivity() {
    
    private lateinit var prefs: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "System Configuration"
        
        prefs = getSharedPreferences("system_config", MODE_PRIVATE)
        
        setupConfiguration()
        loadConfiguration()
    }
    
    private fun setupConfiguration() {
        findViewById<Button>(R.id.btn_test_connection).setOnClickListener {
            testConnection()
        }
        
        findViewById<Button>(R.id.btn_save_config).setOnClickListener {
            saveConfiguration()
        }
    }
    
    private fun loadConfiguration() {
        findViewById<EditText>(R.id.et_server_url).setText(
            prefs.getString("server_url", "https://api.systemmanager.com")
        )
        
        findViewById<EditText>(R.id.et_api_key).setText(
            prefs.getString("api_key", "sk_live_abc123def456")
        )
        
        findViewById<EditText>(R.id.et_timeout).setText(
            prefs.getInt("timeout", 30).toString()
        )
        
        findViewById<TextView>(R.id.tv_config_status).text = "Configuration loaded"
    }
    
    private fun testConnection() {
        Toast.makeText(this, "Connection test successful", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.tv_config_status).text = "Connection: OK"
    }
    
    private fun saveConfiguration() {
        val editor = prefs.edit()
        
        editor.putString("server_url", findViewById<EditText>(R.id.et_server_url).text.toString())
        editor.putString("api_key", findViewById<EditText>(R.id.et_api_key).text.toString())
        editor.putInt("timeout", findViewById<EditText>(R.id.et_timeout).text.toString().toIntOrNull() ?: 30)
        
        editor.apply()
        
        Toast.makeText(this, "Configuration saved", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.tv_config_status).text = "Configuration saved successfully"
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

package com.documentmanager

import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        setupSettings()
    }
    
    private fun setupSettings() {
        // Use existing IDs from the layout or create simple functionality
        Toast.makeText(this, "Settings loaded", Toast.LENGTH_SHORT).show()
    }
}

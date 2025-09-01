package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        setupSettingsOptions()
    }
    
    private fun setupSettingsOptions() {
        findViewById<TextView>(R.id.tvSettingsTitle).text = "App Settings"
        
        val notificationSwitch = findViewById<Switch>(R.id.switchNotifications)
        val darkModeSwitch = findViewById<Switch>(R.id.switchDarkMode)
        val autoRefreshSwitch = findViewById<Switch>(R.id.switchAutoRefresh)
        
        // Set default values
        notificationSwitch.isChecked = true
        darkModeSwitch.isChecked = false
        autoRefreshSwitch.isChecked = true
    }
}

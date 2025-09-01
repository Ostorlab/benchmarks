package co.ostorlab.cloudshare

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecurityActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Security & Privacy"
        
        setupUI()
    }
    
    private fun setupUI() {
        // Security settings
        findViewById<Switch>(R.id.switch_two_factor).isChecked = true
        findViewById<Switch>(R.id.switch_auto_lock).isChecked = false
        findViewById<Switch>(R.id.switch_share_analytics).isChecked = true
        findViewById<Switch>(R.id.switch_sync_enabled).isChecked = true
        
        // Info text
        findViewById<TextView>(R.id.tv_encryption_info).text = 
            "All your files are encrypted with AES-256 encryption. Share passwords are hashed using bcrypt."
            
        findViewById<TextView>(R.id.tv_sync_info).text = 
            "Automatic synchronization keeps your shares updated across all devices."
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

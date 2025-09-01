package co.ostorlab.cloudshare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Account Settings"
        
        setupUI()
    }
    
    private fun setupUI() {
        // User info
        findViewById<TextView>(R.id.tv_user_name).text = "John Smith"
        findViewById<TextView>(R.id.tv_user_email).text = "john.smith@company.com"
        findViewById<TextView>(R.id.tv_plan_type).text = "CloudShare Pro - Business"
        findViewById<TextView>(R.id.tv_member_since).text = "Member since March 2023"
        
        // Buttons
        findViewById<Button>(R.id.btn_edit_profile).setOnClickListener {
            // Edit profile functionality
        }
        
        findViewById<Button>(R.id.btn_change_password).setOnClickListener {
            // Change password functionality
        }
        
        findViewById<Button>(R.id.btn_security_settings).setOnClickListener {
            startActivity(Intent(this, SecurityActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            // Logout functionality
            finish()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

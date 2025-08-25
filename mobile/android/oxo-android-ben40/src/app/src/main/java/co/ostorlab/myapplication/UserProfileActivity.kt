package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserProfileActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        
        setupProfileInfo()
    }
    
    private fun setupProfileInfo() {
        findViewById<TextView>(R.id.tvUsername).text = "John Doe"
        findViewById<TextView>(R.id.tvEmail).text = "john.doe@example.com"
        findViewById<TextView>(R.id.tvMemberSince).text = "Member since: January 2023"
        findViewById<TextView>(R.id.tvReadingStats).text = "Articles read this month: 45"
    }
}

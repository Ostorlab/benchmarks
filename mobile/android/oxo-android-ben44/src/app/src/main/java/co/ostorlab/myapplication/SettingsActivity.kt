package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Title
        val titleText = TextView(this).apply {
            text = "Settings"
            textSize = 24f
            setPadding(0, 0, 0, 40)
        }
        mainLayout.addView(titleText)
        
        // Notification settings section
        val notificationTitle = TextView(this).apply {
            text = "Notification Preferences"
            textSize = 18f
            setPadding(0, 0, 0, 16)
        }
        mainLayout.addView(notificationTitle)
        
        val pushNotifications = CheckBox(this).apply {
            text = "Push notifications for breaking news"
            isChecked = true
        }
        mainLayout.addView(pushNotifications)
        
        val dailyDigest = CheckBox(this).apply {
            text = "Daily news digest email"
            isChecked = false
        }
        mainLayout.addView(dailyDigest)
        
        val weeklyNewsletter = CheckBox(this).apply {
            text = "Weekly newsletter"
            isChecked = true
        }
        mainLayout.addView(weeklyNewsletter)
        
        // Display settings section
        val displayTitle = TextView(this).apply {
            text = "Display Preferences"
            textSize = 18f
            setPadding(0, 40, 0, 16)
        }
        mainLayout.addView(displayTitle)
        
        val darkMode = CheckBox(this).apply {
            text = "Dark mode"
            isChecked = false
        }
        mainLayout.addView(darkMode)
        
        val largerText = CheckBox(this).apply {
            text = "Larger text size"
            isChecked = false
        }
        mainLayout.addView(largerText)
        
        val autoplay = CheckBox(this).apply {
            text = "Autoplay videos"
            isChecked = true
        }
        mainLayout.addView(autoplay)
        
        // Privacy settings section
        val privacyTitle = TextView(this).apply {
            text = "Privacy Settings"
            textSize = 18f
            setPadding(0, 40, 0, 16)
        }
        mainLayout.addView(privacyTitle)
        
        val analytics = CheckBox(this).apply {
            text = "Share usage analytics"
            isChecked = true
        }
        mainLayout.addView(analytics)
        
        val personalized = CheckBox(this).apply {
            text = "Personalized content recommendations"
            isChecked = true
        }
        mainLayout.addView(personalized)
        
        val locationBased = CheckBox(this).apply {
            text = "Location-based news"
            isChecked = false
        }
        mainLayout.addView(locationBased)
        
        // App info section
        val infoTitle = TextView(this).apply {
            text = "App Information"
            textSize = 18f
            setPadding(0, 40, 0, 16)
        }
        mainLayout.addView(infoTitle)
        
        val versionText = TextView(this).apply {
            text = "Version: 2.1.0"
            textSize = 14f
            setPadding(0, 8, 0, 8)
        }
        mainLayout.addView(versionText)
        
        val aboutText = TextView(this).apply {
            text = "© 2025 Daily News Reader. All rights reserved."
            textSize = 12f
            setPadding(0, 8, 0, 8)
        }
        mainLayout.addView(aboutText)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
}

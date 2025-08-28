package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(ContextCompat.getColor(this@SettingsActivity, android.R.color.background_light))
        }
        
        // Header
        val headerText = TextView(this).apply {
            text = "⚙️ Settings"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 32)
        }
        mainLayout.addView(headerText)
        
        // Account Settings
        val accountTitle = TextView(this).apply {
            text = "👤 Account"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }
        mainLayout.addView(accountTitle)
        
        val accountSettings = listOf(
            "✏️ Edit Profile" to "Update personal information",
            "🔐 Change Password" to "Security settings",
            "📧 Email Preferences" to "Notification settings",
            "💳 Subscription" to "Manage premium features"
        )
        
        accountSettings.forEach { (title, description) ->
            val settingItem = createSettingItem(title, description)
            mainLayout.addView(settingItem)
        }
        
        // Privacy Settings
        val privacyTitle = TextView(this).apply {
            text = "🔒 Privacy & Security"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }
        mainLayout.addView(privacyTitle)
        
        val privacySettings = listOf(
            "📊 Data Analytics" to "Share usage data for improvements",
            "📍 Location Services" to "Allow location-based features",
            "☁️ Cloud Sync" to "Automatically backup your data",
            "🔄 Auto-sync" to "Keep data synchronized across devices"
        )
        
        privacySettings.forEach { (title, description) ->
            val settingItem = createToggleSettingItem(title, description, true)
            mainLayout.addView(settingItem)
        }
        
        // App Preferences
        val appTitle = TextView(this).apply {
            text = "📱 App Preferences"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }
        mainLayout.addView(appTitle)
        
        val appSettings = listOf(
            "🔔 Notifications" to "Manage workout reminders",
            "🌙 Dark Mode" to "Enable dark theme",
            "📐 Units" to "Metric or Imperial units",
            "🔊 Sound Effects" to "App sound preferences"
        )
        
        appSettings.forEach { (title, description) ->
            val settingItem = createSettingItem(title, description)
            mainLayout.addView(settingItem)
        }
        
        // Support
        val supportTitle = TextView(this).apply {
            text = "🆘 Support"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }
        mainLayout.addView(supportTitle)
        
        val supportSettings = listOf(
            "❓ Help Center" to "Get answers to common questions",
            "💬 Contact Support" to "Reach our support team",
            "⭐ Rate App" to "Rate us on the App Store",
            "📄 Terms & Privacy" to "Legal information"
        )
        
        supportSettings.forEach { (title, description) ->
            val settingItem = createSettingItem(title, description)
            mainLayout.addView(settingItem)
        }
        
        // App Info
        val infoCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(this@SettingsActivity, android.R.drawable.dialog_holo_light_frame)
            setPadding(20, 16, 20, 16)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 32, 0, 0)
            }
        }
        
        val appInfoTitle = TextView(this).apply {
            text = "ℹ️ App Information"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        }
        infoCard.addView(appInfoTitle)
        
        val versionText = TextView(this).apply {
            text = "Version: 2.1.0 (Build 421)"
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@SettingsActivity, android.R.color.darker_gray))
        }
        infoCard.addView(versionText)
        
        val copyrightText = TextView(this).apply {
            text = "© 2025 FitTracker Pro. All rights reserved."
            textSize = 10f
            setTextColor(ContextCompat.getColor(this@SettingsActivity, android.R.color.darker_gray))
            setPadding(0, 4, 0, 0)
        }
        infoCard.addView(copyrightText)
        
        mainLayout.addView(infoCard)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createSettingItem(title: String, description: String): LinearLayout {
        val settingItem = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(this@SettingsActivity, android.R.drawable.list_selector_background)
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener {
                Toast.makeText(this@SettingsActivity, "Opening $title", Toast.LENGTH_SHORT).show()
            }
        }
        
        val titleText = TextView(this).apply {
            text = title
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        settingItem.addView(titleText)
        
        val descText = TextView(this).apply {
            text = description
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@SettingsActivity, android.R.color.darker_gray))
            setPadding(0, 2, 0, 0)
        }
        settingItem.addView(descText)
        
        return settingItem
    }
    
    private fun createToggleSettingItem(title: String, description: String, isEnabled: Boolean): LinearLayout {
        val settingItem = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@SettingsActivity, android.R.drawable.list_selector_background)
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
        }
        
        val textLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val titleText = TextView(this).apply {
            text = title
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        textLayout.addView(titleText)
        
        val descText = TextView(this).apply {
            text = description
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@SettingsActivity, android.R.color.darker_gray))
            setPadding(0, 2, 0, 0)
        }
        textLayout.addView(descText)
        
        settingItem.addView(textLayout)
        
        val toggleSwitch = Switch(this).apply {
            isChecked = isEnabled
            setOnCheckedChangeListener { _, isChecked ->
                Toast.makeText(this@SettingsActivity, 
                    "$title ${if (isChecked) "enabled" else "disabled"}", 
                    Toast.LENGTH_SHORT).show()
            }
        }
        settingItem.addView(toggleSwitch)
        
        return settingItem
    }
}

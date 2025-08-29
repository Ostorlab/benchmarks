package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(ContextCompat.getColor(this@ProfileActivity, android.R.color.background_light))
        }
        
        // Profile Header
        val profileHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 32)
        }
        
        val avatarText = TextView(this).apply {
            text = "👤"
            textSize = 48f
            setPadding(0, 0, 20, 0)
        }
        profileHeader.addView(avatarText)
        
        val profileInfo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        val nameText = TextView(this).apply {
            text = "John Fitness"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        profileInfo.addView(nameText)
        
        val memberText = TextView(this).apply {
            text = "Premium Member since 2024"
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@ProfileActivity, android.R.color.darker_gray))
        }
        profileInfo.addView(memberText)
        
        profileHeader.addView(profileInfo)
        mainLayout.addView(profileHeader)
        
        // Profile Stats
        val statsTitle = TextView(this).apply {
            text = "📊 Your Stats"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }
        mainLayout.addView(statsTitle)
        
        val statsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@ProfileActivity, android.R.drawable.dialog_holo_light_frame)
            setPadding(20, 16, 20, 16)
        }
        
        val stat1 = createStatItem("💪", "156", "Workouts")
        val stat2 = createStatItem("🔥", "12,450", "Calories")
        val stat3 = createStatItem("⏱️", "89h", "Time")
        
        statsLayout.addView(stat1)
        statsLayout.addView(stat2)
        statsLayout.addView(stat3)
        
        mainLayout.addView(statsLayout)
        
        // Personal Info Section
        val infoTitle = TextView(this).apply {
            text = "ℹ️ Personal Information"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 32, 0, 16)
        }
        mainLayout.addView(infoTitle)
        
        val personalInfo = listOf(
            "📧 Email" to "john.doe@email.com",
            "📞 Phone" to "+1 (555) 123-4567",
            "🎂 Age" to "28 years old",
            "📏 Height" to "180 cm",
            "⚖️ Weight" to "75 kg",
            "🎯 Goal" to "Build muscle and endurance"
        )
        
        personalInfo.forEach { (label, value) ->
            val infoItem = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 8, 0, 8)
            }
            
            val labelText = TextView(this).apply {
                text = label
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            infoItem.addView(labelText)
            
            val valueText = TextView(this).apply {
                text = value
                textSize = 14f
                setTextColor(ContextCompat.getColor(this@ProfileActivity, android.R.color.darker_gray))
            }
            infoItem.addView(valueText)
            
            mainLayout.addView(infoItem)
        }
        
        // Action Buttons
        val editButton = Button(this).apply {
            text = "✏️ Edit Profile"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 32, 0, 8)
            }
            setOnClickListener {
                Toast.makeText(this@ProfileActivity, "Edit profile functionality coming soon!", Toast.LENGTH_SHORT).show()
            }
        }
        mainLayout.addView(editButton)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createStatItem(icon: String, value: String, label: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.CENTER
            
            addView(TextView(this@ProfileActivity).apply {
                text = icon
                textSize = 24f
                gravity = android.view.Gravity.CENTER
            })
            
            addView(TextView(this@ProfileActivity).apply {
                text = value
                textSize = 18f
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(this@ProfileActivity, android.R.color.holo_blue_dark))
            })
            
            addView(TextView(this@ProfileActivity).apply {
                text = label
                textSize = 12f
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(this@ProfileActivity, android.R.color.darker_gray))
            })
        }
    }
}

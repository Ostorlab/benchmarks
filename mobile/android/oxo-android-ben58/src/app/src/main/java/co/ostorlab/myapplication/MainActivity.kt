package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create main layout
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
        }
        
        // App title
        val titleText = TextView(this).apply {
            text = "💪 FitTracker Pro"
            textSize = 28f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_dark))
            setPadding(0, 0, 0, 8)
        }
        mainLayout.addView(titleText)
        
        // Subtitle
        val subtitleText = TextView(this).apply {
            text = "Your Personal Fitness Companion"
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
            setPadding(0, 0, 0, 32)
        }
        mainLayout.addView(subtitleText)
        
        // Today's stats section
        val todaySection = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.background_light))
        }
        
        val todayTitle = TextView(this).apply {
            text = "📊 Today's Summary"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        todaySection.addView(todayTitle)
        
        val statsText = TextView(this).apply {
            text = "🔥 Calories burned: 245\n❤️ Average HR: 72 bpm\n📍 Distance: 2.4 km\n⏱️ Active time: 45 min"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
        }
        todaySection.addView(statsText)
        mainLayout.addView(todaySection)
        
        // Quick actions section
        val actionsTitle = TextView(this).apply {
            text = "Quick Actions"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 32, 0, 16)
        }
        mainLayout.addView(actionsTitle)
        
        // Running workout button
        val runningButton = Button(this).apply {
            text = "🏃‍♂️ Start Running"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                startWorkoutActivity("Running")
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(runningButton)
        
        // Cycling workout button
        val cyclingButton = Button(this).apply {
            text = "🚴‍♀️ Start Cycling"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                startWorkoutActivity("Cycling")
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(cyclingButton)
        
        // Walking workout button
        val walkingButton = Button(this).apply {
            text = "🚶‍♂️ Start Walking"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                startWorkoutActivity("Walking")
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(walkingButton)
        
        // Strength training button
        val strengthButton = Button(this).apply {
            text = "💪 Strength Training"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                startWorkoutActivity("Strength Training")
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainLayout.addView(strengthButton)
        
        // Device management section
        val deviceTitle = TextView(this).apply {
            text = "Device Management"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(deviceTitle)
        
        // Device manager button
        val deviceButton = Button(this).apply {
            text = "📱 Manage Devices"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                val intent = Intent(this@MainActivity, DeviceManagerActivity::class.java)
                startActivity(intent)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(deviceButton)
        
        // Other features section
        val otherTitle = TextView(this).apply {
            text = "Other Features"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }
        mainLayout.addView(otherTitle)
        
        // Browse workouts button
        val browseButton = Button(this).apply {
            text = "📚 Browse Workouts"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                val intent = Intent(this@MainActivity, CategoryBrowseActivity::class.java)
                startActivity(intent)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(browseButton)
        
        // Favorites button
        val favoritesButton = Button(this).apply {
            text = "⭐ My Favorites"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                val intent = Intent(this@MainActivity, BookmarksActivity::class.java)
                startActivity(intent)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(favoritesButton)
        
        // Search button
        val searchButton = Button(this).apply {
            text = "🔍 Search Exercises"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(searchButton)
        
        // Settings button
        val settingsButton = Button(this).apply {
            text = "⚙️ Settings"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 32) }
        }
        mainLayout.addView(settingsButton)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun startWorkoutActivity(workoutType: String) {
        val intent = Intent(this, WorkoutActivity::class.java)
        intent.putExtra("workout_type", workoutType)
        startActivity(intent)
    }
}

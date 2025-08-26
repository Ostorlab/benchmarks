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
        
        // Create main layout with gradient background
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
        }
        
        // App logo/header section
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 24)
        }
        
        // App title with emoji
        val titleText = TextView(this).apply {
            text = "💪 FitTracker Pro"
            textSize = 28f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_dark))
            setPadding(0, 0, 0, 8)
        }
        headerLayout.addView(titleText)
        mainLayout.addView(headerLayout)
        
        // Subtitle
        val subtitleText = TextView(this).apply {
            text = "Your Personal Fitness Companion"
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
            setPadding(0, 0, 0, 32)
        }
        mainLayout.addView(subtitleText)
        
        // Today's activities section with stats
        val activitiesCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_bright))
        }
        
        val activitiesTitle = TextView(this).apply {
            text = "🏃‍♀️ Today's Activities"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            setPadding(0, 0, 0, 16)
        }
        activitiesCard.addView(activitiesTitle)
        
        val statsText = TextView(this).apply {
            text = "📊 Steps: 8,432 | Calories: 642 | Active: 2h 15m"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            setPadding(0, 0, 0, 20)
        }
        activitiesCard.addView(statsText)
        mainLayout.addView(activitiesCard)
        
        // Spacing
        val spacer = TextView(this).apply {
            text = ""
            setPadding(0, 24, 0, 0)
        }
        mainLayout.addView(spacer)
        
        // Sample workout items with emojis
        val workoutItems = listOf(
            "🏃‍♂️ Morning Run - 5.2km completed",
            "💪 Strength Training - Upper body workout",
            "🧘‍♀️ Yoga Session - 30 minutes of relaxation", 
            "🚴‍♀️ Cycling - 12km scenic route",
            "🏊‍♂️ Swimming - 45 minutes cardio session"
        )
        
        workoutItems.forEach { workout ->
            val workoutButton = Button(this).apply {
                text = workout
                setPadding(16, 16, 16, 16)
                setOnClickListener {
                    val intent = Intent(this@MainActivity, ArticleViewerActivity::class.java)
                    intent.putExtra("headline", workout)
                    intent.putExtra("content", generateWorkoutContent(workout))
                    startActivity(intent)
                }
            }
            mainLayout.addView(workoutButton)
        }
        
        // Navigation section with card design
        val navCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.background_light))
        }
        
        val navTitle = TextView(this).apply {
            text = "🔧 Fitness Tools & Features"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.black))
            setPadding(0, 0, 0, 16)
        }
        navCard.addView(navTitle)
        
        // Workout categories button
        val categoriesBtn = Button(this).apply {
            text = "📋 Workout Categories"
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_light))
            setOnClickListener {
                startActivity(Intent(this@MainActivity, CategoryBrowseActivity::class.java))
            }
        }
        navCard.addView(categoriesBtn)
        
        // Exercise search button
        val searchBtn = Button(this).apply {
            text = "🔍 Find Exercises"
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_orange_light))
            setOnClickListener {
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            }
        }
        navCard.addView(searchBtn)
        
        // Favorites button
        val bookmarksBtn = Button(this).apply {
            text = "❤️ Favorite Workouts"
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_light))
            setOnClickListener {
                startActivity(Intent(this@MainActivity, BookmarksActivity::class.java))
            }
        }
        navCard.addView(bookmarksBtn)
        
        // Profile & settings button
        val settingsBtn = Button(this).apply {
            text = "⚙️ Profile & Settings"
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
            setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }
        navCard.addView(settingsBtn)
        
        mainLayout.addView(navCard)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun generateWorkoutContent(workout: String): String {
        return when {
            workout.contains("Run") -> """
                <div style="font-family: Arial; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 10px;">
                <h2>🏃‍♂️ Running Session Complete!</h2>
                <div style="background: rgba(255,255,255,0.2); padding: 15px; border-radius: 8px; margin: 10px 0;">
                <p><strong>📍 Distance:</strong> 5.2km</p>
                <p><strong>⏱️ Time:</strong> 28:15</p>
                <p><strong>⚡ Average pace:</strong> 5:25 min/km</p>
                <p><strong>🔥 Calories burned:</strong> 245</p>
                </div>
                <p>🎉 Great job on completing your morning run! Your endurance is improving steadily.</p>
                <div style="background: rgba(255,255,255,0.1); padding: 10px; border-radius: 5px; margin-top: 15px;">
                <small>💡 Tip: Stay hydrated and maintain consistent breathing for better performance!</small>
                </div>
                </div>
            """.trimIndent()
            workout.contains("Strength") -> """
                <div style="font-family: Arial; padding: 20px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; border-radius: 10px;">
                <h2>💪 Strength Training Session</h2>
                <div style="background: rgba(255,255,255,0.2); padding: 15px; border-radius: 8px; margin: 10px 0;">
                <p><strong>⏰ Duration:</strong> 45 minutes</p>
                <p><strong>🎯 Focus:</strong> Upper body</p>
                <p><strong>🏋️‍♀️ Exercises completed:</strong> 8</p>
                <p><strong>📊 Sets completed:</strong> 24</p>
                </div>
                <p>💯 Excellent strength session! Your form was perfect on the bench press today.</p>
                <div style="background: rgba(255,255,255,0.1); padding: 10px; border-radius: 5px; margin-top: 15px;">
                <small>💡 Remember to rest 48-72 hours between training the same muscle groups!</small>
                </div>
                </div>
            """.trimIndent()
            workout.contains("Yoga") -> """
                <div style="font-family: Arial; padding: 20px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; border-radius: 10px;">
                <h2>🧘‍♀️ Yoga Practice Complete</h2>
                <div style="background: rgba(255,255,255,0.2); padding: 15px; border-radius: 8px; margin: 10px 0;">
                <p><strong>⏰ Duration:</strong> 30 minutes</p>
                <p><strong>🕉️ Style:</strong> Hatha Yoga</p>
                <p><strong>🤸‍♀️ Poses completed:</strong> 12</p>
                <p><strong>🧠 Mindfulness score:</strong> 8/10</p>
                </div>
                <p>🌟 Wonderful yoga session! Your flexibility and balance continue to improve.</p>
                <div style="background: rgba(255,255,255,0.1); padding: 10px; border-radius: 5px; margin-top: 15px;">
                <small>💡 Focus on your breath - it's the bridge between body and mind.</small>
                </div>
                </div>
            """.trimIndent()
            else -> """
                <div style="font-family: Arial; padding: 20px; background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); color: #333; border-radius: 10px;">
                <h2>🏃‍♀️ Fitness Activity Logged</h2>
                <p>Keep up the great work with your fitness journey! Every workout brings you closer to your goals.</p>
                <div style="background: rgba(0,0,0,0.1); padding: 10px; border-radius: 5px; margin-top: 15px;">
                <small>💡 Remember to stay hydrated and listen to your body.</small>
                </div>
                </div>
            """.trimIndent()
        }
    }
}
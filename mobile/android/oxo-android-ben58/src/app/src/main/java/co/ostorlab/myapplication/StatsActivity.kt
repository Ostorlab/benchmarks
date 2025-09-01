package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class StatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(ContextCompat.getColor(this@StatsActivity, android.R.color.background_light))
        }
        
        // Header
        val headerText = TextView(this).apply {
            text = "📈 Statistics & Analytics"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 32)
        }
        mainLayout.addView(headerText)
        
        // Weekly Overview Card
        val weeklyCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(this@StatsActivity, android.R.drawable.dialog_holo_light_frame)
            setPadding(20, 16, 20, 16)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 20)
            }
        }
        
        val weeklyTitle = TextView(this).apply {
            text = "📅 This Week"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        weeklyCard.addView(weeklyTitle)
        
        val weeklyStats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val stat1 = createStatItem("🏃", "5", "Workouts")
        val stat2 = createStatItem("⏱️", "4.5h", "Duration")
        val stat3 = createStatItem("🔥", "1,250", "Calories")
        
        weeklyStats.addView(stat1)
        weeklyStats.addView(stat2)
        weeklyStats.addView(stat3)
        
        weeklyCard.addView(weeklyStats)
        mainLayout.addView(weeklyCard)
        
        // Monthly Progress
        val monthlyTitle = TextView(this).apply {
            text = "📊 Monthly Progress"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(monthlyTitle)
        
        val progressItems = listOf(
            "January 2025" to "18 workouts • 15.2h • 3,450 cal",
            "February 2025" to "22 workouts • 18.7h • 4,120 cal", 
            "March 2025" to "20 workouts • 16.3h • 3,890 cal",
            "April 2025" to "25 workouts • 21.1h • 4,680 cal",
            "May 2025" to "19 workouts • 14.8h • 3,210 cal"
        )
        
        progressItems.forEach { (month, stats) ->
            val progressItem = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = ContextCompat.getDrawable(this@StatsActivity, android.R.drawable.list_selector_background)
                setPadding(16, 12, 16, 12)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 0, 0, 8)
                }
            }
            
            val monthText = TextView(this).apply {
                text = month
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            progressItem.addView(monthText)
            
            val statsText = TextView(this).apply {
                text = stats
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@StatsActivity, android.R.color.darker_gray))
                setPadding(0, 4, 0, 0)
            }
            progressItem.addView(statsText)
            
            mainLayout.addView(progressItem)
        }
        
        // Goals Section
        val goalsTitle = TextView(this).apply {
            text = "🎯 Current Goals"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }
        mainLayout.addView(goalsTitle)
        
        val goals = listOf(
            "Weekly Workouts" to "5/6 completed",
            "Monthly Distance" to "45km / 50km",
            "Weight Goal" to "73kg / 75kg target",
            "Strength Training" to "3/3 sessions this week"
        )
        
        goals.forEach { (goal, progress) ->
            val goalItem = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 12, 16, 12)
                background = ContextCompat.getDrawable(this@StatsActivity, android.R.drawable.list_selector_background)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 0, 0, 8)
                }
            }
            
            val goalIcon = TextView(this).apply {
                text = "🎯"
                textSize = 20f
                setPadding(0, 0, 16, 0)
            }
            goalItem.addView(goalIcon)
            
            val goalLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            
            val goalText = TextView(this).apply {
                text = goal
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            goalLayout.addView(goalText)
            
            val progressText = TextView(this).apply {
                text = progress
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@StatsActivity, android.R.color.darker_gray))
            }
            goalLayout.addView(progressText)
            
            goalItem.addView(goalLayout)
            mainLayout.addView(goalItem)
        }
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createStatItem(icon: String, value: String, label: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.CENTER
            
            addView(TextView(this@StatsActivity).apply {
                text = icon
                textSize = 20f
                gravity = android.view.Gravity.CENTER
            })
            
            addView(TextView(this@StatsActivity).apply {
                text = value
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(this@StatsActivity, android.R.color.holo_blue_dark))
            })
            
            addView(TextView(this@StatsActivity).apply {
                text = label
                textSize = 10f
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(this@StatsActivity, android.R.color.darker_gray))
            })
        }
    }
}

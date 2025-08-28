package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class WorkoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(ContextCompat.getColor(this@WorkoutActivity, android.R.color.background_light))
        }
        
        // Header
        val headerText = TextView(this).apply {
            text = "🏃‍♂️ Start Workout"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 32)
        }
        mainLayout.addView(headerText)
        
        // Workout types
        val workoutTypes = listOf(
            "🏃‍♂️ Running" to "Outdoor or treadmill running",
            "🚴‍♀️ Cycling" to "Road cycling or stationary bike",
            "🏋️‍♂️ Weight Training" to "Strength building exercises",
            "🧘‍♀️ Yoga" to "Flexibility and mindfulness",
            "🏊‍♂️ Swimming" to "Full-body cardio workout",
            "🥾 Hiking" to "Nature walks and trails"
        )
        
        workoutTypes.forEach { (title, description) ->
            val workoutCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = ContextCompat.getDrawable(this@WorkoutActivity, android.R.drawable.list_selector_background)
                setPadding(20, 16, 20, 16)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 0, 0, 12)
                }
            }
            
            val titleText = TextView(this).apply {
                text = title
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            workoutCard.addView(titleText)
            
            val descText = TextView(this).apply {
                text = description
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@WorkoutActivity, android.R.color.darker_gray))
                setPadding(0, 4, 0, 0)
            }
            workoutCard.addView(descText)
            
            workoutCard.setOnClickListener {
                Toast.makeText(this, "Starting ${title.split(" ")[1]} workout...", Toast.LENGTH_SHORT).show()
                finish()
            }
            
            mainLayout.addView(workoutCard)
        }
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
}

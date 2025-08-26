package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class CategoryBrowseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Title
        val titleText = TextView(this).apply {
            text = "Workout Categories"
            textSize = 24f
            setPadding(0, 0, 0, 40)
        }
        mainLayout.addView(titleText)
        
        // Categories list
        val categories = listOf(
            "Cardio Workouts",
            "Strength Training",
            "Yoga & Flexibility",
            "HIIT Workouts", 
            "Pilates",
            "Swimming",
            "Running & Jogging",
            "Cycling",
            "Dance Fitness",
            "Meditation & Mindfulness"
        )
        
        categories.forEach { category ->
            val categoryButton = Button(this).apply {
                text = category
                setPadding(16, 16, 16, 16)
                setOnClickListener {
                    // Could navigate to category-specific workouts
                    // For now, just finish activity
                    finish()
                }
            }
            mainLayout.addView(categoryButton)
        }
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
}

package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class WorkoutActivity : ComponentActivity() {
    private var isWorkoutActive = false
    private var workoutStartTime = 0L
    private var currentHeartRate = 75
    private var caloriesBurned = 0
    private var distance = 0.0f
    private lateinit var handler: Handler
    private lateinit var updateRunnable: Runnable
    
    private lateinit var statusText: TextView
    private lateinit var timeText: TextView
    private lateinit var heartRateText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var distanceText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var pauseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Header
        val headerText = TextView(this).apply {
            text = "🏃‍♀️ Active Workout"
            textSize = 28f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@WorkoutActivity, android.R.color.holo_green_dark))
            setPadding(0, 0, 0, 24)
        }
        mainLayout.addView(headerText)
        
        // Workout type
        val workoutType = intent.getStringExtra("workout_type") ?: "Running"
        val typeText = TextView(this).apply {
            text = "Activity: $workoutType"
            textSize = 18f
            setPadding(0, 0, 0, 16)
        }
        mainLayout.addView(typeText)
        
        // Status display
        statusText = TextView(this).apply {
            text = "Ready to start workout"
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@WorkoutActivity, android.R.color.darker_gray))
            setPadding(0, 0, 0, 24)
        }
        mainLayout.addView(statusText)
        
        // Metrics layout
        val metricsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@WorkoutActivity, android.R.color.background_light))
        }
        
        // Time
        timeText = TextView(this).apply {
            text = "⏱️ Time: 00:00:00"
            textSize = 18f
            setPadding(0, 8, 0, 8)
        }
        metricsLayout.addView(timeText)
        
        // Heart rate
        heartRateText = TextView(this).apply {
            text = "❤️ Heart Rate: -- bpm"
            textSize = 18f
            setPadding(0, 8, 0, 8)
        }
        metricsLayout.addView(heartRateText)
        
        // Calories
        caloriesText = TextView(this).apply {
            text = "🔥 Calories: 0 kcal"
            textSize = 18f
            setPadding(0, 8, 0, 8)
        }
        metricsLayout.addView(caloriesText)
        
        // Distance
        distanceText = TextView(this).apply {
            text = "📍 Distance: 0.0 km"
            textSize = 18f
            setPadding(0, 8, 0, 8)
        }
        metricsLayout.addView(distanceText)
        
        mainLayout.addView(metricsLayout)
        
        // Buttons layout
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 24, 0, 0)
        }
        
        startButton = Button(this).apply {
            text = "Start Workout"
            setOnClickListener { startWorkout() }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(0, 0, 8, 0)
            }
        }
        buttonLayout.addView(startButton)
        
        pauseButton = Button(this).apply {
            text = "Pause"
            isEnabled = false
            setOnClickListener { pauseWorkout() }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(4, 0, 4, 0)
            }
        }
        buttonLayout.addView(pauseButton)
        
        stopButton = Button(this).apply {
            text = "Stop"
            isEnabled = false
            setOnClickListener { stopWorkout() }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 0, 0)
            }
        }
        buttonLayout.addView(stopButton)
        
        mainLayout.addView(buttonLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        setupWorkoutUpdater()
    }
    
    private fun setupWorkoutUpdater() {
        handler = Handler(Looper.getMainLooper())
        updateRunnable = object : Runnable {
            override fun run() {
                if (isWorkoutActive) {
                    updateWorkoutMetrics()
                    broadcastWorkoutData()
                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        }
    }
    
    private fun startWorkout() {
        isWorkoutActive = true
        workoutStartTime = System.currentTimeMillis()
        statusText.text = "Workout in progress..."
        
        startButton.isEnabled = false
        pauseButton.isEnabled = true
        stopButton.isEnabled = true
        
        // Start periodic updates
        handler.post(updateRunnable)
        
        // Broadcast workout start
        broadcastWorkoutStart()
    }
    
    private fun pauseWorkout() {
        isWorkoutActive = false
        statusText.text = "Workout paused"
        
        startButton.text = "Resume"
        startButton.isEnabled = true
        pauseButton.isEnabled = false
        
        broadcastWorkoutPause()
    }
    
    private fun stopWorkout() {
        isWorkoutActive = false
        statusText.text = "Workout completed!"
        
        startButton.text = "Start Workout"
        startButton.isEnabled = true
        pauseButton.isEnabled = false
        stopButton.isEnabled = false
        
        // Reset metrics
        workoutStartTime = 0L
        caloriesBurned = 0
        distance = 0.0f
        currentHeartRate = 75
        
        broadcastWorkoutComplete()
    }
    
    private fun updateWorkoutMetrics() {
        if (!isWorkoutActive) return
        
        val elapsedTime = System.currentTimeMillis() - workoutStartTime
        val hours = elapsedTime / (1000 * 60 * 60)
        val minutes = (elapsedTime % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (elapsedTime % (1000 * 60)) / 1000
        
        timeText.text = "⏱️ Time: ${String.format("%02d:%02d:%02d", hours, minutes, seconds)}"
        
        // Simulate heart rate variations
        currentHeartRate = (70 + Math.random() * 50).toInt()
        heartRateText.text = "❤️ Heart Rate: $currentHeartRate bpm"
        
        // Update calories (rough estimate)
        caloriesBurned = (elapsedTime / 1000 * 0.15).toInt()
        caloriesText.text = "🔥 Calories: $caloriesBurned kcal"
        
        // Update distance (rough estimate)
        distance = (elapsedTime / 1000.0 * 0.002).toFloat()
        distanceText.text = "📍 Distance: ${String.format("%.2f", distance)} km"
    }
    
    // VULNERABILITY: Broadcasting sensitive workout data without restrictions
    private fun broadcastWorkoutData() {
        val workoutIntent = Intent("com.fittracker.WORKOUT_UPDATE")
        workoutIntent.putExtra("user_id", "user_12345") 
        workoutIntent.putExtra("heart_rate", currentHeartRate)
        workoutIntent.putExtra("calories_burned", caloriesBurned)
        workoutIntent.putExtra("distance_km", distance)
        workoutIntent.putExtra("workout_type", intent.getStringExtra("workout_type") ?: "Running")
        workoutIntent.putExtra("timestamp", System.currentTimeMillis())
        workoutIntent.putExtra("location_lat", 40.7128) // Simulated location
        workoutIntent.putExtra("location_lon", -74.0060)
        sendBroadcast(workoutIntent)
    }
    
    private fun broadcastWorkoutStart() {
        val startIntent = Intent("com.fittracker.WORKOUT_STARTED")
        startIntent.putExtra("user_id", "user_12345")
        startIntent.putExtra("workout_type", intent.getStringExtra("workout_type") ?: "Running")
        startIntent.putExtra("start_time", workoutStartTime)
        startIntent.putExtra("user_profile", "John Doe, Age: 28, Weight: 75kg")
        sendBroadcast(startIntent)
    }
    
    private fun broadcastWorkoutPause() {
        val pauseIntent = Intent("com.fittracker.WORKOUT_PAUSED")
        pauseIntent.putExtra("user_id", "user_12345")
        pauseIntent.putExtra("elapsed_time", System.currentTimeMillis() - workoutStartTime)
        sendBroadcast(pauseIntent)
    }
    
    private fun broadcastWorkoutComplete() {
        val completeIntent = Intent("com.fittracker.WORKOUT_COMPLETED")
        completeIntent.putExtra("user_id", "user_12345")
        completeIntent.putExtra("total_time", System.currentTimeMillis() - workoutStartTime)
        completeIntent.putExtra("total_calories", caloriesBurned)
        completeIntent.putExtra("total_distance", distance)
        completeIntent.putExtra("avg_heart_rate", currentHeartRate)
        completeIntent.putExtra("achievement_points", caloriesBurned * 2)
        sendBroadcast(completeIntent)
    }
}

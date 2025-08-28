package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    
    private lateinit var firebaseUrl: String
    private lateinit var mainLayout: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        firebaseUrl = getString(R.string.firebase_database_url)
        Log.d("FitTracker", "Connecting to cloud: $firebaseUrl")
        
        setupUI()
        syncUserData()
    }
    
    private fun setupUI() {
        val scrollView = ScrollView(this)
        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.background_light))
        }
        
        createHeader()
        
        val statusText = TextView(this).apply {
            text = "📡 Syncing with cloud..."
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_dark))
            setPadding(0, 0, 0, 24)
        }
        mainLayout.addView(statusText)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createHeader() {
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 32)
        }
        
        val logoText = TextView(this).apply {
            text = "🏃‍♂️"
            textSize = 36f
            setPadding(0, 0, 16, 0)
        }
        headerLayout.addView(logoText)
        
        val titleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        val titleText = TextView(this).apply {
            text = "FitTracker Pro"
            textSize = 28f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.black))
        }
        titleLayout.addView(titleText)
        
        val subtitleText = TextView(this).apply {
            text = "Your Personal Fitness Journey"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
        }
        titleLayout.addView(subtitleText)
        
        headerLayout.addView(titleLayout)
        mainLayout.addView(headerLayout)
    }
    
    private fun syncUserData() {
        val executor = Executors.newSingleThreadExecutor()
        
        executor.execute {
            try {
                Log.d("CloudSync", "Fetching user profiles...")
                val usersData = fetchCloudData("$firebaseUrl/users.json")
                val workoutsData = fetchCloudData("$firebaseUrl/workouts.json")
                val devicesData = fetchCloudData("$firebaseUrl/devices.json")
                
                runOnUiThread {
                    displayDashboard(usersData, workoutsData, devicesData)
                }
                
            } catch (e: Exception) {
                Log.e("CloudSync", "Sync failed", e)
                runOnUiThread {
                    showOfflineMode()
                }
            }
        }
    }
    
    private fun fetchCloudData(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        val responseCode = connection.responseCode
        Log.d("CloudSync", "Server response: $responseCode for $url")
        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()
            return response
        }
        
        return "{}"
    }
    
    private fun displayDashboard(usersData: String, workoutsData: String, devicesData: String) {
        mainLayout.removeAllViews()
        
        createHeader()
        
        val statusText = TextView(this).apply {
            text = "✅ Cloud Connected"
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_dark))
            setPadding(0, 0, 0, 24)
        }
        mainLayout.addView(statusText)
        
        createStatsCard(usersData, workoutsData, devicesData)
        createNavigationButtons()
        createRecentActivity(workoutsData)
        createUserProfiles(usersData)
        createDeviceStatus(devicesData)
    }
    
    private fun createStatsCard(usersData: String, workoutsData: String, devicesData: String) {
        val statsCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(this@MainActivity, android.R.drawable.dialog_holo_light_frame)
            setPadding(20, 16, 20, 16)
        }
        
        val statsTitle = TextView(this).apply {
            text = "📊 Today's Overview"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        statsCard.addView(statsTitle)
        
        val statsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        
        val userCount = try { JSONObject(usersData).getJSONObject("users").length() } catch (e: Exception) { 0 }
        val workoutCount = try { JSONObject(workoutsData).getJSONObject("workouts").length() } catch (e: Exception) { 0 }
        val deviceCount = try { JSONObject(devicesData).getJSONObject("devices").length() } catch (e: Exception) { 0 }
        
        val stat1 = createStatItem("👥", userCount.toString(), "Users")
        val stat2 = createStatItem("🏃", workoutCount.toString(), "Workouts")
        val stat3 = createStatItem("📱", deviceCount.toString(), "Devices")
        
        statsLayout.addView(stat1)
        statsLayout.addView(stat2)
        statsLayout.addView(stat3)
        
        statsCard.addView(statsLayout)
        mainLayout.addView(statsCard)
        
        val spacer = View(this).apply { layoutParams = LinearLayout.LayoutParams(0, 24) }
        mainLayout.addView(spacer)
    }
    
    private fun createStatItem(icon: String, value: String, label: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            gravity = android.view.Gravity.CENTER
            
            addView(TextView(this@MainActivity).apply {
                text = icon
                textSize = 24f
                gravity = android.view.Gravity.CENTER
            })
            
            addView(TextView(this@MainActivity).apply {
                text = value
                textSize = 20f
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_blue_dark))
            })
            
            addView(TextView(this@MainActivity).apply {
                text = label
                textSize = 12f
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
            })
        }
    }
    
    private fun createNavigationButtons() {
        val navTitle = TextView(this).apply {
            text = "Quick Actions"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        mainLayout.addView(navTitle)
        
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        val workoutButton = Button(this).apply {
            text = "🏃‍♂️ Start Workout"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener { 
                startActivity(Intent(this@MainActivity, WorkoutActivity::class.java))
            }
        }
        buttonLayout.addView(workoutButton)
        
        val profileButton = Button(this).apply {
            text = "👤 View Profile"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener { 
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            }
        }
        buttonLayout.addView(profileButton)
        
        val statsButton = Button(this).apply {
            text = "📈 Statistics"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener { 
                startActivity(Intent(this@MainActivity, StatsActivity::class.java))
            }
        }
        buttonLayout.addView(statsButton)
        
        val devicesButton = Button(this).apply {
            text = "📱 Devices"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener { 
                startActivity(Intent(this@MainActivity, DevicesActivity::class.java))
            }
        }
        buttonLayout.addView(devicesButton)
        
        val settingsButton = Button(this).apply {
            text = "⚙️ Settings"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 16)
            }
            setOnClickListener { 
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }
        buttonLayout.addView(settingsButton)
        
        mainLayout.addView(buttonLayout)
    }
    
    private fun createRecentActivity(workoutsData: String) {
        val activityTitle = TextView(this).apply {
            text = "🎯 Recent Activity"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 12)
        }
        mainLayout.addView(activityTitle)
        
        try {
            val rootObject = JSONObject(workoutsData)
            val workoutsObject = rootObject.getJSONObject("workouts")
            val keys = workoutsObject.keys()
            var count = 0
            
            while (keys.hasNext() && count < 2) {
                val workoutId = keys.next()
                val workout = workoutsObject.getJSONObject(workoutId)
                
                val activityItem = createActivityItem(workout)
                mainLayout.addView(activityItem)
                count++
            }
        } catch (e: JSONException) {
            val noDataText = TextView(this).apply {
                text = "No recent activity found"
                textSize = 14f
                setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                setPadding(16, 12, 16, 12)
            }
            mainLayout.addView(noDataText)
        }
        
        val spacer = View(this).apply { layoutParams = LinearLayout.LayoutParams(0, 16) }
        mainLayout.addView(spacer)
    }
    
    private fun createActivityItem(workout: JSONObject): LinearLayout {
        val activityItem = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@MainActivity, android.R.drawable.list_selector_background)
            setPadding(16, 12, 16, 12)
        }
        
        val iconText = TextView(this).apply {
            text = when(workout.optString("type")) {
                "running" -> "🏃‍♂️"
                "cycling" -> "🚴‍♀️"
                "weightlifting" -> "🏋️‍♂️"
                else -> "💪"
            }
            textSize = 20f
            setPadding(0, 0, 16, 0)
        }
        activityItem.addView(iconText)
        
        val detailsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val titleText = TextView(this).apply {
            text = "${workout.optString("type", "Workout").replaceFirstChar { it.uppercase() }} Session"
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        detailsLayout.addView(titleText)
        
        val detailText = TextView(this).apply {
            text = "${workout.optString("duration", "0")} min • ${workout.optString("calories", "0")} cal"
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
        }
        detailsLayout.addView(detailText)
        
        activityItem.addView(detailsLayout)
        
        val dateText = TextView(this).apply {
            text = workout.optString("date", "Today")
            textSize = 11f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
        }
        activityItem.addView(dateText)
        
        return activityItem
    }
    
    private fun createUserProfiles(usersData: String) {
        val profileTitle = TextView(this).apply {
            text = "👥 Community"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        mainLayout.addView(profileTitle)
        
        try {
            val rootObject = JSONObject(usersData)
            val usersObject = rootObject.getJSONObject("users")
            val keys = usersObject.keys()
            var count = 0
            
            while (keys.hasNext() && count < 3) {
                val userId = keys.next()
                val user = usersObject.getJSONObject(userId)
                
                val profileItem = createUserItem(user)
                mainLayout.addView(profileItem)
                count++
            }
        } catch (e: JSONException) {
            Log.d("UserProfiles", "No user data available")
        }
        
        val spacer = View(this).apply { layoutParams = LinearLayout.LayoutParams(0, 16) }
        mainLayout.addView(spacer)
    }
    
    private fun createUserItem(user: JSONObject): LinearLayout {
        val profileItem = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@MainActivity, android.R.drawable.list_selector_background)
            setPadding(16, 12, 16, 12)
        }
        
        val avatarText = TextView(this).apply {
            text = "👤"
            textSize = 20f
            setPadding(0, 0, 16, 0)
        }
        profileItem.addView(avatarText)
        
        val userLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val nameText = TextView(this).apply {
            text = user.optString("name", "User")
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        userLayout.addView(nameText)
        
        val infoText = TextView(this).apply {
            text = "Active member since ${user.optString("created_at", "2025")}"
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
        }
        userLayout.addView(infoText)
        
        profileItem.addView(userLayout)
        return profileItem
    }
    
    private fun createDeviceStatus(devicesData: String) {
        val deviceTitle = TextView(this).apply {
            text = "📱 Connected Devices"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        mainLayout.addView(deviceTitle)
        
        try {
            val rootObject = JSONObject(devicesData)
            val devicesObject = rootObject.getJSONObject("devices")
            val keys = devicesObject.keys()
            var count = 0
            
            while (keys.hasNext() && count < 2) {
                val deviceId = keys.next()
                val device = devicesObject.getJSONObject(deviceId)
                
                val deviceItem = createDeviceItem(device)
                mainLayout.addView(deviceItem)
                count++
            }
        } catch (e: JSONException) {
            val noDeviceText = TextView(this).apply {
                text = "No devices connected"
                textSize = 14f
                setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
                setPadding(16, 12, 16, 12)
            }
            mainLayout.addView(noDeviceText)
        }
    }
    
    private fun createDeviceItem(device: JSONObject): LinearLayout {
        val deviceItem = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@MainActivity, android.R.drawable.list_selector_background)
            setPadding(16, 12, 16, 12)
        }
        
        val deviceIcon = TextView(this).apply {
            text = "⌚"
            textSize = 20f
            setPadding(0, 0, 16, 0)
        }
        deviceItem.addView(deviceIcon)
        
        val deviceLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val deviceName = TextView(this).apply {
            text = device.optString("device_name", "Fitness Device")
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        deviceLayout.addView(deviceName)
        
        val batteryText = TextView(this).apply {
            text = "Battery: ${device.optString("battery_level", "N/A")}%"
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.darker_gray))
        }
        deviceLayout.addView(batteryText)
        
        deviceItem.addView(deviceLayout)
        
        val statusText = TextView(this).apply {
            text = "🟢 Online"
            textSize = 11f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_green_dark))
        }
        deviceItem.addView(statusText)
        
        return deviceItem
    }
    
    private fun showOfflineMode() {
        val offlineText = TextView(this).apply {
            text = "📡 Working offline - Some features may be limited"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_orange_light))
            setPadding(0, 0, 0, 24)
        }
        mainLayout.addView(offlineText)
        
        createNavigationButtons()
    }
}

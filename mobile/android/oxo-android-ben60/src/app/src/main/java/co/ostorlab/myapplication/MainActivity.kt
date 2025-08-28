package co.ostorlab.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity(), LocationListener {
    
    private lateinit var locationManager: LocationManager
    private var isTrackingWorkout by mutableStateOf(false)
    private var statusMessage by mutableStateOf("Ready to start your workout")
    private var hasLocationPermission by mutableStateOf(false)
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasLocationPermission = isGranted
        statusMessage = if (isGranted) {
            "Location permission granted - ready for workout tracking"
        } else {
            "Location permission required for GPS-based workout tracking"
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        initializeLocationServices()
        
        setContent {
            FitTrackerProTheme {
                FitTrackerMainScreen(
                    statusMessage = statusMessage,
                    isTracking = isTrackingWorkout,
                    onStartWorkout = { startWorkoutTracking() },
                    onStopWorkout = { stopWorkoutTracking() },
                    onNavigateToWorkout = { navigateToWorkout() },
                    onNavigateToProgress = { navigateToProgress() },
                    onNavigateToProfile = { navigateToProfile() },
                    onNavigateToSettings = { navigateToSettings() }
                )
            }
        }
    }
    
    @Composable
    fun FitTrackerMainScreen(
        statusMessage: String,
        isTracking: Boolean,
        onStartWorkout: () -> Unit,
        onStopWorkout: () -> Unit,
        onNavigateToWorkout: () -> Unit,
        onNavigateToProgress: () -> Unit,
        onNavigateToProfile: () -> Unit,
        onNavigateToSettings: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "FitTracker Pro",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Stats Card
            StatsCard()
            
            // Status
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Workout Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = statusMessage)
                }
            }
            
            // Workout Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onStartWorkout,
                    modifier = Modifier.weight(1f),
                    enabled = !isTracking
                ) {
                    Text("Start Workout")
                }
                
                Button(
                    onClick = onStopWorkout,
                    modifier = Modifier.weight(1f),
                    enabled = isTracking
                ) {
                    Text("Stop Workout")
                }
            }
            
            // Navigation Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToWorkout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Workout Plans")
                }
                
                Button(
                    onClick = onNavigateToProgress,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Progress")
                }
                
                Button(
                    onClick = onNavigateToProfile,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Profile")
                }
                
                Button(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Settings")
                }
            }
        }
    }
    
    @Composable
    fun StatsCard() {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Today's Activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Steps: 8,247", fontSize = 14.sp)
                        Text("Distance: 6.2 km", fontSize = 14.sp)
                        Text("Calories: 342", fontSize = 14.sp)
                    }
                    Column {
                        Text("Active Time: 45 min", fontSize = 14.sp)
                        Text("Heart Rate: 72 bpm", fontSize = 14.sp)
                        Text("Workouts: 2", fontSize = 14.sp)
                    }
                }
            }
        }
    }
    
    @Composable
    fun FitTrackerProTheme(content: @Composable () -> Unit) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
                content = content
            )
        }
    }
    
    private fun initializeLocationServices() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        hasLocationPermission = ContextCompat.checkSelfPermission(
            this, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    private fun startWorkoutTracking() {
        if (hasLocationPermission) {
            isTrackingWorkout = true
            statusMessage = "Workout in progress - GPS tracking active"
            
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000, // 5 seconds
                    10f,  // 10 meters
                    this
                )
                
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    10f,
                    this
                )
            } catch (ex: SecurityException) {
                statusMessage = "Location permission required for workout tracking"
            }
        } else {
            statusMessage = "Please enable location permissions for workout tracking"
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    
    private fun stopWorkoutTracking() {
        isTrackingWorkout = false
        statusMessage = "Workout stopped - Great job!"
        
        try {
            locationManager.removeUpdates(this)
        } catch (ex: SecurityException) {
            // Handle gracefully
        }
    }
    
    // VULNERABLE: Location broadcast without permission protection
    private fun broadcastLocationUpdate(location: Location) {
        val locationIntent = Intent("com.fittracker.LOCATION_UPDATE")
        
        // Add comprehensive location data to broadcast
        locationIntent.putExtra("latitude", location.latitude)
        locationIntent.putExtra("longitude", location.longitude)
        locationIntent.putExtra("accuracy", location.accuracy)
        locationIntent.putExtra("altitude", location.altitude)
        locationIntent.putExtra("speed", location.speed)
        locationIntent.putExtra("bearing", location.bearing)
        locationIntent.putExtra("timestamp", System.currentTimeMillis())
        locationIntent.putExtra("provider", location.provider)
        locationIntent.putExtra("workout_id", "workout_${System.currentTimeMillis()}")
        
        // VULNERABLE: Unprotected broadcast - any app can receive this!
        sendBroadcast(locationIntent)
        
        // Also sync to cloud for legitimate app functionality
        syncLocationToCloud(location)
    }
    
    private fun syncLocationToCloud(location: Location) {
        // Legitimate cloud sync for fitness tracking
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        // This would normally sync to a legitimate fitness cloud service
        // For demo purposes, we'll just log it
        android.util.Log.d("FitTracker", "Syncing location to cloud: ${location.latitude}, ${location.longitude}")
    }
    
    // LocationListener implementation
    override fun onLocationChanged(location: Location) {
        if (isTrackingWorkout) {
            // Update UI with current location info
            statusMessage = "Tracking workout - Current accuracy: ${location.accuracy.toInt()}m"
            
            // VULNERABLE: Broadcast location data to any listening app
            broadcastLocationUpdate(location)
        }
    }
    
    // Navigation functions
    private fun navigateToWorkout() {
        startActivity(Intent(this, WorkoutActivity::class.java))
    }
    
    private fun navigateToProgress() {
        startActivity(Intent(this, ProgressActivity::class.java))
    }
    
    private fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
    
    private fun navigateToSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}
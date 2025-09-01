package co.ostorlab.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class LocationSyncReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.fittracker.LOCATION_UPDATE") {
            // Extract location data from broadcast
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val accuracy = intent.getFloatExtra("accuracy", 0f)
            val timestamp = intent.getLongExtra("timestamp", 0)
            val workoutId = intent.getStringExtra("workout_id") ?: "unknown"
            
            // Legitimate internal processing of location data
            Log.d("LocationSync", "Processing location update for workout: $workoutId")
            
            // Sync to legitimate fitness cloud service
            syncLocationToFitnessCloud(latitude, longitude, accuracy, timestamp, workoutId)
            
            // Update local database for workout tracking
            updateWorkoutDatabase(latitude, longitude, timestamp, workoutId)
        }
    }
    
    private fun syncLocationToFitnessCloud(lat: Double, lng: Double, accuracy: Float, timestamp: Long, workoutId: String) {
        // This would sync to a legitimate fitness tracking cloud service
        // For demo purposes, just log the sync
        Log.d("LocationSync", "Syncing to cloud - Lat: $lat, Lng: $lng, Accuracy: ${accuracy}m")
    }
    
    private fun updateWorkoutDatabase(lat: Double, lng: Double, timestamp: Long, workoutId: String) {
        // This would update a local SQLite database with workout route data
        // For demo purposes, just log the update
        Log.d("LocationSync", "Updating workout database - Route point added")
    }
}

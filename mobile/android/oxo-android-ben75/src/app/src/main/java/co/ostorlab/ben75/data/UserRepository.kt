package co.ostorlab.ben75.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.*

object UserRepository {

    private const val TAG = "HeartConnect"

    /**
     * Base URL for the HeartConnect backend server.
     * 10.0.2.2 is the special IP for the host machine when running in Android Emulator.
     * For a physical device, replace with the actual server IP (e.g., http://192.168.1.100:8000).
     */
    private const val BASE_URL = "http://10.0.2.2:8000"

    /**
     * Current user location: Times Square, NYC
     * This is hardcoded locally for the benchmark demo.
     */
    val currentUserLat: Double = 40.7580
    val currentUserLon: Double = -73.9855

    private val client = OkHttpClient()
    private val gson = Gson()

    /**
     * Fetches nearby match profiles from the backend server.
     * The server returns raw coordinates (latitude, longitude) for each match.
     *
     * VULNERABLE DATA FLOW:
     * 1. Server sends raw coordinates via HTTP API
     * 2. App receives coordinates and calculates Haversine distance client-side
     * 3. App broadcasts precise distances via implicit intent
     * 4. Attacker intercepts broadcasts and performs trilateration
     */
    fun fetchNearbyMatches(context: Context): List<UserProfile> {
        val request = Request.Builder()
            .url("$BASE_URL/profiles")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Failed to fetch profiles: ${response.code}")
                    return emptyList()
                }

                val body = response.body?.string()
                if (body.isNullOrEmpty()) {
                    Log.e(TAG, "Empty response from server")
                    return emptyList()
                }

                val listType = object : TypeToken<List<UserProfile>>() {}.type
                val profiles: List<UserProfile> = gson.fromJson(body, listType)

                Log.d(TAG, "Fetched ${profiles.size} profiles from server")
                profiles.forEach { profile ->
                    Log.d(TAG, "Profile from server: ${profile.id} at (${profile.latitude}, ${profile.longitude})")
                }

                profiles
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching profiles from server", e)
            emptyList()
        }
    }

    /**
     * Calculates Haversine distance between current user and a match.
     * Returns distance in meters with 2-decimal precision.
     *
     * VULNERABLE: High-precision distance enables trilateration attacks.
     * Rounding to ~100m would mitigate this risk significantly.
     */
    fun calculateDistance(match: UserProfile): Double {
        val r = 6371000.0 // Earth radius in meters

        val lat1Rad = Math.toRadians(currentUserLat)
        val lat2Rad = Math.toRadians(match.latitude)
        val deltaLat = Math.toRadians(match.latitude - currentUserLat)
        val deltaLon = Math.toRadians(match.longitude - currentUserLon)

        val a = sin(deltaLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distanceMeters = r * c
        return round(distanceMeters * 100.0) / 100.0 // 2 decimal places
    }
}

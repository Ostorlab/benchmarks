package co.ostorlab.ben75.data

import kotlin.math.*

object UserRepository {

    /**
     * Current user location: Times Square, NYC
     * This is hardcoded for the benchmark demo.
     */
    val currentUserLat: Double = 40.7580
    val currentUserLon: Double = -73.9855

    /**
     * Mock nearby matches with hardcoded coordinates.
     * These coordinates are NEVER broadcast externally.
     * Only Haversine distance (in meters) is leaked via broadcast.
     */
    private val nearbyMatches = listOf(
        UserProfile(
            id = "user_001",
            name = "Sarah",
            age = 26,
            bio = "Coffee lover & weekend hiker. Let's grab a latte!",
            latitude = 40.7489,   // ~1.02 km south
            longitude = -73.9680
        ),
        UserProfile(
            id = "user_002",
            name = "Mike",
            age = 29,
            bio = "Photography enthusiast. Always chasing golden hour.",
            latitude = 40.7614,   // ~380 m north
            longitude = -73.9776
        ),
        UserProfile(
            id = "user_003",
            name = "Jessica",
            age = 24,
            bio = "Yoga instructor. Looking for positive vibes only.",
            latitude = 40.7505,   // ~850 m south-west
            longitude = -73.9934
        ),
        UserProfile(
            id = "user_004",
            name = "David",
            age = 31,
            bio = "Foodie exploring NYC's best pizza spots.",
            latitude = 40.7656,   // ~860 m north-east
            longitude = -73.9782
        ),
        UserProfile(
            id = "user_005",
            name = "Emma",
            age = 27,
            bio = "Art gallery curator. Let's visit a museum together.",
            latitude = 40.7398,   // ~2.03 km south
            longitude = -73.9847
        )
    )

    fun getNearbyMatches(): List<UserProfile> = nearbyMatches

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

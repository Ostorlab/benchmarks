package co.ostorlab.ben75

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.ostorlab.ben75.data.UserProfile
import co.ostorlab.ben75.data.UserRepository
import co.ostorlab.ben75.ui.theme.HeartConnectTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "HeartConnect"
        // VULNERABLE: Predictable broadcast action string
        const val BROADCAST_ACTION = "co.ostorlab.ben75.NEARBY_USERS_UPDATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HeartConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NearbyMatchesScreen(
                        onRefresh = { broadcastNearbyUsers() }
                    )
                }
            }
        }

        // VULNERABLE: Broadcast sent automatically on app launch
        broadcastNearbyUsers()
    }

    /**
     * VULNERABLE: Broadcasts precise distances via implicit broadcast intent.
     *
     * This method exposes:
     * - Reference point (current user lat/lon)
     * - High-precision distance in meters (2 decimal places)
     * - Timestamp for temporal tracking
     *
     * It does NOT include match coordinates, but an attacker with 3+ readings
     * from different reference points can solve for exact location via
     * trilateration.
     */
    private fun broadcastNearbyUsers() {
        val matches = UserRepository.getNearbyMatches()
        val currentLat = UserRepository.currentUserLat
        val currentLon = UserRepository.currentUserLon
        val timestamp = System.currentTimeMillis()

        val intent = Intent(BROADCAST_ACTION).apply {
            // Reference point where distance was measured from
            putExtra("reference_lat", currentLat)
            putExtra("reference_lon", currentLon)
            putExtra("timestamp_ms", timestamp)

            // VULNERABLE: High-precision distances enable trilateration
            matches.forEachIndexed { index, match ->
                val distance = UserRepository.calculateDistance(match)
                putExtra("user_${index + 1}_id", match.id)
                putExtra("user_${index + 1}_name", match.name)
                putExtra("user_${index + 1}_distance_m", distance)
            }

            putExtra("total_users", matches.size)
        }

        // VULNERABLE: Implicit broadcast — any app on the device can receive
        sendBroadcast(intent)

        // VULNERABLE: Also logs precise distances to system logcat
        Log.d(TAG, "Broadcasted nearby users update")
        Log.d(TAG, "Reference: lat=$currentLat, lon=$currentLon, time=$timestamp")
        matches.forEach { match ->
            val dist = UserRepository.calculateDistance(match)
            Log.d(TAG, "Match ${match.id} (${match.name}): ${dist}m away")
        }
    }
}

@Composable
fun NearbyMatchesScreen(onRefresh: () -> Unit) {
    var matches by remember { mutableStateOf<List<Pair<UserProfile, Double>>>(emptyList()) }
    var lastUpdated by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        val repo = UserRepository
        val list = repo.getNearbyMatches().map { it to repo.calculateDistance(it) }
        matches = list
        lastUpdated = System.currentTimeMillis()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "HeartConnect",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Discover people nearby",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                onRefresh()
                val repo = UserRepository
                matches = repo.getNearbyMatches().map { it to repo.calculateDistance(it) }
                lastUpdated = System.currentTimeMillis()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Refresh Location")
        }

        if (lastUpdated > 0) {
            Text(
                text = "Last updated: ${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date(lastUpdated))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(matches) { (profile, distance) ->
                MatchCard(profile = profile, distanceMeters = distance)
            }
        }
    }
}

@Composable
fun MatchCard(profile: UserProfile, distanceMeters: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder avatar circle
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = profile.name.first().toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${profile.name}, ${profile.age}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                // VULNERABLE: Displaying high-precision distance in UI
                Text(
                    text = "%.2f m away".format(distanceMeters),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

package co.ostorlab.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ProgressActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FitTrackerProTheme {
                ProgressScreen()
            }
        }
    }
    
    @Composable
    fun ProgressScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your Progress",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Weekly Stats
            StatsCard(
                title = "This Week",
                stats = listOf(
                    "Workouts Completed" to "12",
                    "Total Distance" to "42.8 km",
                    "Calories Burned" to "3,240",
                    "Active Time" to "6h 25m"
                )
            )
            
            // Monthly Stats
            StatsCard(
                title = "This Month",
                stats = listOf(
                    "Workouts Completed" to "48",
                    "Total Distance" to "156.2 km", 
                    "Calories Burned" to "12,890",
                    "Active Time" to "24h 15m"
                )
            )
            
            // Goals Progress
            GoalsCard()
            
            // Recent Achievements
            AchievementsCard()
        }
    }
    
    @Composable
    fun StatsCard(title: String, stats: List<Pair<String, String>>) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                stats.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = label, fontSize = 14.sp)
                        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
    
    @Composable
    fun GoalsCard() {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Monthly Goals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Goal progress bars (simulated)
                GoalProgressRow("Weekly Workouts", 12, 15)
                Spacer(modifier = Modifier.height(8.dp))
                GoalProgressRow("Distance (km)", 156.2f, 200f)
                Spacer(modifier = Modifier.height(8.dp))
                GoalProgressRow("Calories Burned", 12890, 15000)
            }
        }
    }
    
    @Composable
    fun GoalProgressRow(label: String, current: Number, target: Number) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = label, fontSize = 14.sp)
                Text(text = "$current / $target", fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = when {
                    current is Int && target is Int -> current.toFloat() / target.toFloat()
                    current is Float && target is Float -> current / target
                    else -> 0.75f
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    @Composable
    fun AchievementsCard() {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Recent Achievements",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                AchievementRow("🏃‍♂️", "First 5K Run", "Completed your first 5 kilometer run!")
                AchievementRow("🔥", "Calorie Crusher", "Burned over 500 calories in one workout")
                AchievementRow("⏰", "Early Bird", "Completed 5 morning workouts this week")
                AchievementRow("📍", "Explorer", "Discovered 10 new workout routes")
            }
        }
    }
    
    @Composable
    fun AchievementRow(icon: String, title: String, description: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = icon,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    fontSize = 12.sp
                )
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
}

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

class WorkoutActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FitTrackerProTheme {
                WorkoutScreen()
            }
        }
    }
    
    @Composable
    fun WorkoutScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Workout Plans",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Featured Workout
            WorkoutCard(
                title = "Morning Run",
                description = "5K route through the park with GPS tracking",
                duration = "30 minutes",
                calories = "320 cal"
            )
            
            WorkoutCard(
                title = "HIIT Training",
                description = "High-intensity interval training for fat burn",
                duration = "25 minutes", 
                calories = "280 cal"
            )
            
            WorkoutCard(
                title = "Cycling Adventure",
                description = "Scenic 15K bike ride with elevation tracking",
                duration = "45 minutes",
                calories = "450 cal"
            )
            
            WorkoutCard(
                title = "Strength Training",
                description = "Upper body workout with resistance bands",
                duration = "35 minutes",
                calories = "210 cal"
            )
            
            WorkoutCard(
                title = "Yoga Flow",
                description = "Relaxing yoga session for flexibility",
                duration = "20 minutes",
                calories = "120 cal"
            )
        }
    }
    
    @Composable
    fun WorkoutCard(title: String, description: String, duration: String, calories: String) {
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
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = description,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏱ $duration",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "🔥 $calories",
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { /* Start this workout */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Workout")
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
}

package co.ostorlab.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ProfileActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FitTrackerProTheme {
                ProfileScreen()
            }
        }
    }
    
    @Composable
    fun ProfileScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            // User Info Card
            ProfileInfoCard()
            
            // Fitness Stats
            FitnessStatsCard()
            
            // Health Metrics
            HealthMetricsCard()
            
            // Personal Records
            PersonalRecordsCard()
        }
    }
    
    @Composable
    fun ProfileInfoCard() {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile avatar placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 40.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Alex Johnson",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Fitness Enthusiast",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat("Age", "28")
                    ProfileStat("Height", "175 cm")
                    ProfileStat("Weight", "72 kg")
                }
            }
        }
    }
    
    @Composable
    fun ProfileStat(label: String, value: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                fontSize = 12.sp
            )
        }
    }
    
    @Composable
    fun FitnessStatsCard() {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Fitness Level",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                FitnessStatRow("Cardio Fitness", "Excellent", 85)
                Spacer(modifier = Modifier.height(8.dp))
                FitnessStatRow("Strength", "Good", 75)
                Spacer(modifier = Modifier.height(8.dp))
                FitnessStatRow("Flexibility", "Fair", 60)
                Spacer(modifier = Modifier.height(8.dp))
                FitnessStatRow("Endurance", "Very Good", 80)
            }
        }
    }
    
    @Composable
    fun FitnessStatRow(category: String, level: String, score: Int) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = category, fontSize = 14.sp)
                Text(text = "$level ($score/100)", fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = score / 100f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    @Composable
    fun HealthMetricsCard() {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Health Metrics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                HealthMetricRow("Resting Heart Rate", "68 bpm", "Excellent")
                HealthMetricRow("VO2 Max", "52.3 ml/kg/min", "Good")
                HealthMetricRow("Body Fat", "12.8%", "Athletic")
                HealthMetricRow("Sleep Quality", "8.2/10", "Very Good")
            }
        }
    }
    
    @Composable
    fun HealthMetricRow(metric: String, value: String, status: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = metric, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = status, fontSize = 12.sp)
            }
            Text(text = value, fontSize = 14.sp)
        }
    }
    
    @Composable
    fun PersonalRecordsCard() {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Personal Records",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                RecordRow("🏃‍♂️", "Fastest 5K", "22:15", "Set 2 weeks ago")
                RecordRow("🚴‍♂️", "Longest Ride", "47.2 km", "Set 1 month ago")
                RecordRow("🔥", "Most Calories", "678 cal", "Set 3 days ago")
                RecordRow("⏱", "Longest Workout", "1h 42m", "Set 1 week ago")
            }
        }
    }
    
    @Composable
    fun RecordRow(icon: String, title: String, record: String, date: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = icon,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Column {
                    Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(text = date, fontSize = 12.sp)
                }
            }
            Text(
                text = record,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
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

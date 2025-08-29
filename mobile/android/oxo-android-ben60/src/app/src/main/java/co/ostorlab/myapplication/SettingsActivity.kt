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

class SettingsActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FitTrackerProTheme {
                SettingsScreen()
            }
        }
    }
    
    @Composable
    fun SettingsScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Account Settings
            SettingsSection(
                title = "Account",
                settings = listOf(
                    SettingItem.Button("Edit Profile", "Update your personal information"),
                    SettingItem.Button("Privacy Settings", "Manage your data and privacy"),
                    SettingItem.Button("Connected Apps", "Manage third-party integrations")
                )
            )
            
            // Workout Settings
            SettingsSection(
                title = "Workout Preferences",
                settings = listOf(
                    SettingItem.Toggle("GPS Tracking", "Enable location tracking during workouts", true),
                    SettingItem.Toggle("Auto-Pause", "Automatically pause when you stop moving", true),
                    SettingItem.Toggle("Audio Cues", "Voice prompts during workouts", false),
                    SettingItem.Button("Units", "Metric system (km, kg)")
                )
            )
            
            // Notifications
            SettingsSection(
                title = "Notifications",
                settings = listOf(
                    SettingItem.Toggle("Workout Reminders", "Daily workout notifications", true),
                    SettingItem.Toggle("Achievement Alerts", "Get notified of new achievements", true),
                    SettingItem.Toggle("Progress Updates", "Weekly progress summaries", false),
                    SettingItem.Toggle("Social Features", "Friend activity notifications", true)
                )
            )
            
            // Data & Sync
            SettingsSection(
                title = "Data & Synchronization",
                settings = listOf(
                    SettingItem.Toggle("Cloud Sync", "Sync data to fitness cloud", true),
                    SettingItem.Toggle("Auto Backup", "Automatically backup workout data", true),
                    SettingItem.Button("Export Data", "Download your fitness data"),
                    SettingItem.Button("Clear Cache", "Free up storage space")
                )
            )
            
            // App Info
            SettingsSection(
                title = "About",
                settings = listOf(
                    SettingItem.Button("Version", "FitTracker Pro v2.4.1"),
                    SettingItem.Button("Privacy Policy", "Read our privacy policy"),
                    SettingItem.Button("Terms of Service", "View terms and conditions"),
                    SettingItem.Button("Support", "Get help and contact us")
                )
            )
        }
    }
    
    @Composable
    fun SettingsSection(title: String, settings: List<SettingItem>) {
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
                
                settings.forEach { setting ->
                    when (setting) {
                        is SettingItem.Toggle -> {
                            ToggleSettingRow(
                                title = setting.title,
                                description = setting.description,
                                isChecked = setting.isEnabled
                            )
                        }
                        is SettingItem.Button -> {
                            ButtonSettingRow(
                                title = setting.title,
                                description = setting.description
                            )
                        }
                    }
                    
                    if (setting != settings.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
    
    @Composable
    fun ToggleSettingRow(title: String, description: String, isChecked: Boolean) {
        var checked by remember { mutableStateOf(isChecked) }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
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
            
            Switch(
                checked = checked,
                onCheckedChange = { checked = it }
            )
        }
    }
    
    @Composable
    fun ButtonSettingRow(title: String, description: String) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    fontSize = 12.sp
                )
            }
        }
    }
    
    sealed class SettingItem {
        data class Toggle(
            val title: String,
            val description: String,
            val isEnabled: Boolean
        ) : SettingItem()
        
        data class Button(
            val title: String,
            val description: String = ""
        ) : SettingItem()
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

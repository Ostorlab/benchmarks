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
            CameraProTheme {
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
                text = "Camera Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Camera Settings
            SettingsSection(
                title = "Camera",
                settings = listOf(
                    SettingItem.Toggle("GPS Geotagging", "Embed location data in photos", true),
                    SettingItem.Toggle("Auto-Focus", "Automatic focus adjustment", true),
                    SettingItem.Toggle("Grid Lines", "Show composition grid", false),
                    SettingItem.Button("Resolution", "12MP (4032x3024)")
                )
            )
            
            // Photo Settings
            SettingsSection(
                title = "Photo Storage",
                settings = listOf(
                    SettingItem.Toggle("Cloud Backup", "Automatically backup photos", true),
                    SettingItem.Toggle("Save to Gallery", "Also save to device gallery", true),
                    SettingItem.Toggle("Metadata Preservation", "Keep EXIF data in photos", true),
                    SettingItem.Button("Storage Location", "Internal Storage/Pictures")
                )
            )
            
            // Privacy Settings
            SettingsSection(
                title = "Privacy & Sharing",
                settings = listOf(
                    SettingItem.Toggle("Share Photos", "Allow sharing via content provider", true),
                    SettingItem.Toggle("Location Sharing", "Include GPS data when sharing", true),
                    SettingItem.Toggle("Metadata Sharing", "Share EXIF data with other apps", true),
                    SettingItem.Button("Photo Access", "Manage app permissions")
                )
            )
            
            // App Settings
            SettingsSection(
                title = "Application",
                settings = listOf(
                    SettingItem.Button("Version", "Camera Pro v1.2.4"),
                    SettingItem.Button("Privacy Policy", "View privacy policy"),
                    SettingItem.Button("Support", "Help and feedback"),
                    SettingItem.Button("Clear Cache", "Free up storage space")
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
    fun CameraProTheme(content: @Composable () -> Unit) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
                content = content
            )
        }
    }
}

package co.ostorlab.myapplication

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GalleryActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CameraProTheme {
                GalleryScreen()
            }
        }
    }
    
    @Composable
    fun GalleryScreen() {
        val photos by remember { mutableStateOf(getPhotosList()) }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Photo Gallery",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (photos.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "No Photos Yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Capture your first photo to see it here with embedded GPS coordinates!")
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photos) { photo ->
                        PhotoCard(photo)
                    }
                }
            }
        }
    }
    
    @Composable
    fun PhotoCard(photo: PhotoInfo) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = photo.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Captured: ${photo.timestamp}",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Size: ${photo.sizeKB} KB",
                            fontSize = 12.sp
                        )
                    }
                    
                    Text(
                        text = "📸",
                        fontSize = 24.sp
                    )
                }
                
                if (photo.hasGPS) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "📍 GPS Information",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    photo.gpsInfo?.let { gps ->
                        Text(
                            text = "Coordinates: ${gps.latitude}, ${gps.longitude}",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Location: ${gps.location}",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Precision: ${gps.precision}",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
    
    data class PhotoInfo(
        val name: String,
        val timestamp: String,
        val sizeKB: Long,
        val hasGPS: Boolean,
        val gpsInfo: GPSInfo? = null
    )
    
    data class GPSInfo(
        val latitude: String,
        val longitude: String,
        val location: String,
        val precision: String
    )
    
    private fun getPhotosList(): List<PhotoInfo> {
        val photos = mutableListOf<PhotoInfo>()
        val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        
        picturesDir?.listFiles()?.filter { it.name.endsWith(".jpg") }?.forEach { file ->
            val timestamp = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(file.lastModified()))
            val sizeKB = file.length() / 1024
            
            // Check for GPS data in EXIF
            var hasGPS = false
            var gpsInfo: GPSInfo? = null
            
            try {
                val exif = ExifInterface(file.absolutePath)
                val latLong = FloatArray(2)
                
                if (exif.getLatLong(latLong)) {
                    hasGPS = true
                    gpsInfo = GPSInfo(
                        latitude = String.format("%.6f°", latLong[0]),
                        longitude = String.format("%.6f°", latLong[1]),
                        location = "Captured location",
                        precision = "High precision GPS"
                    )
                }
            } catch (e: Exception) {
                // Handle gracefully
            }
            
            photos.add(PhotoInfo(
                name = file.name,
                timestamp = timestamp,
                sizeKB = sizeKB,
                hasGPS = hasGPS,
                gpsInfo = gpsInfo
            ))
        }
        
        return photos.sortedByDescending { it.timestamp }
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

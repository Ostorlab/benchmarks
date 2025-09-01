package co.ostorlab.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity(), LocationListener {
    
    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private var statusMessage by mutableStateOf("Camera Pro ready - Professional photography with GPS geotagging")
    private var hasLocationPermission by mutableStateOf(false)
    private var hasCameraPermission by mutableStateOf(false)
    private var photoCount by mutableStateOf(0)
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasLocationPermission = isGranted
        statusMessage = if (isGranted) {
            "Location permission granted - Photos will include GPS coordinates"
        } else {
            "Location permission required for GPS geotagging"
        }
    }
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        statusMessage = if (isGranted) {
            "Camera permission granted - Ready to capture photos"
        } else {
            "Camera permission required for photo capture"
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        initializeServices()
        
        setContent {
            CameraProTheme {
                CameraMainScreen(
                    statusMessage = statusMessage,
                    photoCount = photoCount,
                    onCapturePhoto = { capturePhotoWithLocation() },
                    onNavigateToGallery = { navigateToGallery() },
                    onNavigateToEdit = { navigateToEdit() },
                    onNavigateToFilters = { navigateToFilters() },
                    onNavigateToSettings = { navigateToSettings() }
                )
            }
        }
    }
    
    @Composable
    fun CameraMainScreen(
        statusMessage: String,
        photoCount: Int,
        onCapturePhoto: () -> Unit,
        onNavigateToGallery: () -> Unit,
        onNavigateToEdit: () -> Unit,
        onNavigateToFilters: () -> Unit,
        onNavigateToSettings: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Camera Pro",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Camera Stats Card
            CameraStatsCard(photoCount)
            
            // Status
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Camera Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = statusMessage)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (hasLocationPermission && currentLocation != null) {
                        Text(
                            text = "📍 GPS Ready - Photos will include location data",
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Camera Controls
            Button(
                onClick = onCapturePhoto,
                modifier = Modifier.fillMaxWidth(),
                enabled = hasCameraPermission
            ) {
                Text("📸 Capture Photo with GPS", fontSize = 16.sp)
            }
            
            // Navigation Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToGallery,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🖼️ View Gallery ($photoCount photos)")
                }
                
                Button(
                    onClick = onNavigateToEdit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✂️ Photo Editor")
                }
                
                Button(
                    onClick = onNavigateToFilters,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎨 Filters & Effects")
                }
                
                Button(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚙️ Settings")
                }
            }
        }
    }
    
    @Composable
    fun CameraStatsCard(photoCount: Int) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Today's Photography",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Photos Taken: $photoCount", fontSize = 14.sp)
                        Text("Storage Used: ${photoCount * 2.5f} MB", fontSize = 14.sp)
                        Text("Geotagged: $photoCount", fontSize = 14.sp)
                    }
                    Column {
                        Text("Resolution: 12MP", fontSize = 14.sp)
                        Text("Format: JPEG", fontSize = 14.sp)
                        Text("GPS Tagging: ON", fontSize = 14.sp)
                    }
                }
            }
        }
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
    
    private fun initializeServices() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        // Check permissions
        hasLocationPermission = ContextCompat.checkSelfPermission(
            this, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        // Request permissions if needed
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        
        // Start location updates
        if (hasLocationPermission) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000, // 10 seconds
                    50f,   // 50 meters
                    this
                )
                
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    10000,
                    50f,
                    this
                )
            } catch (ex: SecurityException) {
                // Handle gracefully
            }
        }
        
        // Count existing photos
        updatePhotoCount()
    }
    
    // VULNERABLE: Capture photo with GPS metadata embedded
    private fun capturePhotoWithLocation() {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        
        try {
            // Create photo file with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_${timestamp}.jpg")
            
            // VULNERABLE: Embed GPS coordinates in EXIF metadata (creates valid JPEG with GPS)
            embedGPSMetadata(photoFile)
            
            // Update UI
            photoCount++
            statusMessage = "Photo captured with GPS coordinates: ${photoFile.name}"
            
            // Register photo with content provider (makes it accessible to other apps)
            registerPhotoWithProvider(photoFile)
            
        } catch (e: IOException) {
            statusMessage = "Error capturing photo: ${e.message}"
        }
    }
    
    // VULNERABLE: Embed GPS coordinates without proper access control
    private fun embedGPSMetadata(photoFile: File) {
        try {
            // Create a minimal valid JPEG file first
            createMinimalJPEG(photoFile)
            
            val exif = ExifInterface(photoFile.absolutePath)
            
            // VULNERABLE: Use actual GPS coordinates from device location
            currentLocation?.let { location ->
                // VULNERABLE: Store precise GPS coordinates in EXIF data
                exif.setLatLong(location.latitude, location.longitude)
                
                android.util.Log.d("CameraPro", "GPS metadata embedded: ${location.latitude}, ${location.longitude}")
            } ?: run {
                // Fallback to last known location or mock coordinates if no GPS fix
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                try {
                    val lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    
                    if (lastKnown != null) {
                        exif.setLatLong(lastKnown.latitude, lastKnown.longitude)
                        android.util.Log.d("CameraPro", "Using last known location: ${lastKnown.latitude}, ${lastKnown.longitude}")
                    } else {
                        // Use mock coordinates only if no location available
                        val mockLatitude = 40.7128
                        val mockLongitude = -74.0060
                        exif.setLatLong(mockLatitude, mockLongitude)
                        android.util.Log.d("CameraPro", "Using mock location (no GPS): $mockLatitude, $mockLongitude")
                    }
                } catch (e: SecurityException) {
                    android.util.Log.e("CameraPro", "No location permission for GPS access")
                }
            }
            exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, System.currentTimeMillis().toString())
            exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, SimpleDateFormat("yyyy:MM:dd", Locale.getDefault()).format(Date()))
            exif.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, "network")
            
            // Additional metadata that could be sensitive
            exif.setAttribute(ExifInterface.TAG_DATETIME, SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault()).format(Date()))
            exif.setAttribute(ExifInterface.TAG_MAKE, "CameraPro")
            exif.setAttribute(ExifInterface.TAG_MODEL, "Professional Camera")
            
            exif.saveAttributes()
            
            android.util.Log.d("CameraPro", "GPS metadata embedded from current location")
            
        } catch (e: IOException) {
            android.util.Log.e("CameraPro", "Failed to embed GPS metadata", e)
        }
    }
    
    private fun createMinimalJPEG(photoFile: File) {
        // Create a minimal valid JPEG file (1x1 pixel image)
        val jpegHeader = byteArrayOf(
            0xFF.toByte(), 0xD8.toByte(), // SOI
            0xFF.toByte(), 0xE0.toByte(), // APP0
            0x00.toByte(), 0x10.toByte(), // Length
            0x4A.toByte(), 0x46.toByte(), 0x49.toByte(), 0x46.toByte(), 0x00.toByte(), // JFIF
            0x01.toByte(), 0x01.toByte(), // Version
            0x01.toByte(), // Units
            0x00.toByte(), 0x48.toByte(), // X density
            0x00.toByte(), 0x48.toByte(), // Y density
            0x00.toByte(), 0x00.toByte(), // Thumbnail
            0xFF.toByte(), 0xD9.toByte()  // EOI
        )
        
        photoFile.writeBytes(jpegHeader)
    }
    
    private fun registerPhotoWithProvider(photoFile: File) {
        // This makes the photo accessible through the PhotoProvider
        // The content provider will expose this file to any app that queries it
        android.util.Log.d("CameraPro", "Photo registered with provider: ${photoFile.absolutePath}")
    }
    
    private fun updatePhotoCount() {
        val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        picturesDir?.let {
            photoCount = it.listFiles()?.filter { file -> 
                file.name.startsWith("IMG_") && file.name.endsWith(".jpg")
            }?.size ?: 0
        }
    }
    
    // LocationListener implementation
    override fun onLocationChanged(location: Location) {
        currentLocation = location
        statusMessage = "GPS active - Photos will include precise location data (accuracy: ${location.accuracy.toInt()}m)"
    }
    
    // Navigation functions
    private fun navigateToGallery() {
        startActivity(Intent(this, GalleryActivity::class.java))
    }
    
    private fun navigateToEdit() {
        startActivity(Intent(this, EditActivity::class.java))
    }
    
    private fun navigateToFilters() {
        startActivity(Intent(this, FiltersActivity::class.java))
    }
    
    private fun navigateToSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}
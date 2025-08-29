package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class DevicesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(ContextCompat.getColor(this@DevicesActivity, android.R.color.background_light))
        }
        
        // Header
        val headerText = TextView(this).apply {
            text = "📱 Connected Devices"
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 32)
        }
        mainLayout.addView(headerText)
        
        // Connected Devices
        val connectedTitle = TextView(this).apply {
            text = "🟢 Active Devices"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }
        mainLayout.addView(connectedTitle)
        
        val devices = listOf(
            DeviceInfo("⌚", "FitBand Pro X1", "Smart Watch", "85%", "🟢 Online", "Last sync: 2 min ago"),
            DeviceInfo("📱", "iPhone 14 Pro", "Mobile App", "67%", "🟢 Online", "Always connected"),
            DeviceInfo("🏠", "Smart Scale", "Body Composition", "N/A", "🟢 Online", "Last reading: 1 hour ago")
        )
        
        devices.forEach { device ->
            val deviceCard = createDeviceCard(device)
            mainLayout.addView(deviceCard)
        }
        
        // Available Devices
        val availableTitle = TextView(this).apply {
            text = "🔍 Available to Connect"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }
        mainLayout.addView(availableTitle)
        
        val availableDevices = listOf(
            DeviceInfo("🎧", "AirPods Pro", "Heart Rate Monitor", "N/A", "🟡 Nearby", "Tap to connect"),
            DeviceInfo("🚴", "Peloton Bike", "Exercise Equipment", "N/A", "🟡 Nearby", "Available for pairing"),
            DeviceInfo("⌚", "Apple Watch", "Smart Watch", "N/A", "🟡 Nearby", "Ready to pair")
        )
        
        availableDevices.forEach { device ->
            val deviceCard = createAvailableDeviceCard(device)
            mainLayout.addView(deviceCard)
        }
        
        // Device Management
        val managementTitle = TextView(this).apply {
            text = "⚙️ Device Management"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 16)
        }
        mainLayout.addView(managementTitle)
        
        val syncButton = Button(this).apply {
            text = "🔄 Sync All Devices"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener {
                Toast.makeText(this@DevicesActivity, "Syncing all devices...", Toast.LENGTH_SHORT).show()
            }
        }
        mainLayout.addView(syncButton)
        
        val scanButton = Button(this).apply {
            text = "🔍 Scan for Devices"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener {
                Toast.makeText(this@DevicesActivity, "Scanning for nearby devices...", Toast.LENGTH_SHORT).show()
            }
        }
        mainLayout.addView(scanButton)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createDeviceCard(device: DeviceInfo): LinearLayout {
        val deviceCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@DevicesActivity, android.R.drawable.dialog_holo_light_frame)
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 12)
            }
        }
        
        val iconText = TextView(this).apply {
            text = device.icon
            textSize = 32f
            setPadding(0, 0, 16, 0)
        }
        deviceCard.addView(iconText)
        
        val deviceInfo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val nameText = TextView(this).apply {
            text = device.name
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        deviceInfo.addView(nameText)
        
        val typeText = TextView(this).apply {
            text = device.type
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@DevicesActivity, android.R.color.darker_gray))
        }
        deviceInfo.addView(typeText)
        
        val statusText = TextView(this).apply {
            text = device.status
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@DevicesActivity, android.R.color.holo_green_dark))
            setPadding(0, 4, 0, 0)
        }
        deviceInfo.addView(statusText)
        
        val lastSyncText = TextView(this).apply {
            text = device.lastSync
            textSize = 10f
            setTextColor(ContextCompat.getColor(this@DevicesActivity, android.R.color.darker_gray))
        }
        deviceInfo.addView(lastSyncText)
        
        deviceCard.addView(deviceInfo)
        
        if (device.battery != "N/A") {
            val batteryLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
            }
            
            val batteryText = TextView(this).apply {
                text = "🔋"
                textSize = 16f
                gravity = android.view.Gravity.CENTER
            }
            batteryLayout.addView(batteryText)
            
            val batteryLevel = TextView(this).apply {
                text = device.battery
                textSize = 12f
                gravity = android.view.Gravity.CENTER
                setTextColor(ContextCompat.getColor(this@DevicesActivity, android.R.color.holo_blue_dark))
            }
            batteryLayout.addView(batteryLevel)
            
            deviceCard.addView(batteryLayout)
        }
        
        return deviceCard
    }
    
    private fun createAvailableDeviceCard(device: DeviceInfo): LinearLayout {
        val deviceCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@DevicesActivity, android.R.drawable.list_selector_background)
            setPadding(16, 12, 16, 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener {
                Toast.makeText(this@DevicesActivity, "Connecting to ${device.name}...", Toast.LENGTH_SHORT).show()
            }
        }
        
        val iconText = TextView(this).apply {
            text = device.icon
            textSize = 24f
            setPadding(0, 0, 16, 0)
        }
        deviceCard.addView(iconText)
        
        val deviceInfo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val nameText = TextView(this).apply {
            text = device.name
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        deviceInfo.addView(nameText)
        
        val typeText = TextView(this).apply {
            text = device.type
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@DevicesActivity, android.R.color.darker_gray))
        }
        deviceInfo.addView(typeText)
        
        deviceCard.addView(deviceInfo)
        
        val statusText = TextView(this).apply {
            text = device.status
            textSize = 12f
            setTextColor(ContextCompat.getColor(this@DevicesActivity, android.R.color.holo_orange_light))
        }
        deviceCard.addView(statusText)
        
        return deviceCard
    }
    
    data class DeviceInfo(
        val icon: String,
        val name: String,
        val type: String,
        val battery: String,
        val status: String,
        val lastSync: String
    )
}

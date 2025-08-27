package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class DeviceManagerActivity : ComponentActivity() {
    
    private val connectedDevices = mutableListOf<DeviceInfo>()
    private lateinit var deviceListLayout: LinearLayout

    data class DeviceInfo(
        val name: String,
        val address: String,
        val type: String,
        val batteryLevel: Int,
        val isConnected: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Header
        val headerText = TextView(this).apply {
            text = "📱 Device Manager"
            textSize = 28f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.holo_blue_dark))
            setPadding(0, 0, 0, 24)
        }
        mainLayout.addView(headerText)
        
        val subtitleText = TextView(this).apply {
            text = "Manage your connected fitness devices"
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.darker_gray))
            setPadding(0, 0, 0, 32)
        }
        mainLayout.addView(subtitleText)
        
        // Connection status
        val statusLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.background_light))
        }
        
        val statusText = TextView(this).apply {
            text = "📡 Connection Status: Active"
            textSize = 18f
            setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.holo_green_dark))
            setPadding(0, 0, 0, 8)
        }
        statusLayout.addView(statusText)
        
        val syncText = TextView(this).apply {
            text = "🔄 Last sync: Just now"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.darker_gray))
        }
        statusLayout.addView(syncText)
        
        mainLayout.addView(statusLayout)
        
        // Buttons
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 24, 0, 16)
        }
        
        val scanButton = Button(this).apply {
            text = "Scan Devices"
            setOnClickListener { scanForDevices() }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(0, 0, 8, 0)
            }
        }
        buttonLayout.addView(scanButton)
        
        val syncButton = Button(this).apply {
            text = "Sync All"
            setOnClickListener { syncAllDevices() }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(8, 0, 0, 0)
            }
        }
        buttonLayout.addView(syncButton)
        
        mainLayout.addView(buttonLayout)
        
        // Device list header
        val listHeaderText = TextView(this).apply {
            text = "Connected Devices"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(listHeaderText)
        
        // Device list layout
        deviceListLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        mainLayout.addView(deviceListLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
        
        // Initialize with some mock devices
        initializeMockDevices()
    }
    
    private fun initializeMockDevices() {
        connectedDevices.clear()
        connectedDevices.addAll(listOf(
            DeviceInfo("FitBand Pro", "A4:B5:C6:D7:E8:F9", "Fitness Tracker", 85, true),
            DeviceInfo("Smart Scale Plus", "12:34:56:78:90:AB", "Smart Scale", 92, true),
            DeviceInfo("Heart Monitor X1", "CD:EF:12:34:56:78", "Heart Rate Monitor", 67, false),
            DeviceInfo("Wireless Earbuds", "9A:8B:7C:6D:5E:4F", "Audio Device", 45, true)
        ))
        updateDeviceList()
        
        // Broadcast initial device status
        broadcastDeviceInventory()
    }
    
    private fun updateDeviceList() {
        deviceListLayout.removeAllViews()
        
        for (device in connectedDevices) {
            val deviceCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(ContextCompat.getColor(this@DeviceManagerActivity, 
                    if (device.isConnected) android.R.color.background_light else android.R.color.darker_gray))
                
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 8)
                layoutParams = params
            }
            
            val nameText = TextView(this).apply {
                text = "${if (device.isConnected) "🟢" else "🔴"} ${device.name}"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            deviceCard.addView(nameText)
            
            val detailsText = TextView(this).apply {
                text = "Type: ${device.type} | Battery: ${device.batteryLevel}%"
                textSize = 12f
                setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.darker_gray))
            }
            deviceCard.addView(detailsText)
            
            val addressText = TextView(this).apply {
                text = "Address: ${device.address}"
                textSize = 10f
                setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.darker_gray))
            }
            deviceCard.addView(addressText)
            
            // Add connect/disconnect button
            val actionButton = Button(this).apply {
                text = if (device.isConnected) "Disconnect" else "Connect"
                setOnClickListener { 
                    if (device.isConnected) {
                        disconnectDevice(device)
                    } else {
                        connectDevice(device)
                    }
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            deviceCard.addView(actionButton)
            
            deviceListLayout.addView(deviceCard)
        }
    }
    
    private fun scanForDevices() {
        Toast.makeText(this, "Scanning for devices...", Toast.LENGTH_SHORT).show()
        
        // Simulate finding a new device
        val newDevice = DeviceInfo(
            "New Fitness Watch", 
            "FF:EE:DD:CC:BB:AA", 
            "Smartwatch", 
            78, 
            false
        )
        
        // Add if not already present
        if (!connectedDevices.any { it.address == newDevice.address }) {
            connectedDevices.add(newDevice)
            updateDeviceList()

            broadcastDeviceDiscovered(newDevice)
        }
    }
    
    private fun connectDevice(device: DeviceInfo) {
        Toast.makeText(this, "Connecting to ${device.name}...", Toast.LENGTH_SHORT).show()
        
        // Update device status
        val index = connectedDevices.indexOfFirst { it.address == device.address }
        if (index != -1) {
            connectedDevices[index] = device.copy(isConnected = true)
            updateDeviceList()

            broadcastDeviceConnected(device)
        }
    }
    
    private fun disconnectDevice(device: DeviceInfo) {
        Toast.makeText(this, "Disconnecting from ${device.name}...", Toast.LENGTH_SHORT).show()
        
        // Update device status
        val index = connectedDevices.indexOfFirst { it.address == device.address }
        if (index != -1) {
            connectedDevices[index] = device.copy(isConnected = false)
            updateDeviceList()
            
            broadcastDeviceDisconnected(device)
        }
    }
    
    private fun syncAllDevices() {
        Toast.makeText(this, "Syncing all connected devices...", Toast.LENGTH_SHORT).show()
        
        // Simulate battery level updates
        for (i in connectedDevices.indices) {
            if (connectedDevices[i].isConnected) {
                connectedDevices[i] = connectedDevices[i].copy(
                    batteryLevel = (connectedDevices[i].batteryLevel + (-5..5).random()).coerceIn(0, 100)
                )
            }
        }
        updateDeviceList()

        broadcastSyncComplete()
    }

    
    private fun broadcastDeviceInventory() {
        val inventoryIntent = Intent("com.fittracker.DEVICE_INVENTORY")
        inventoryIntent.putExtra("user_id", "user_12345")
        inventoryIntent.putExtra("device_count", connectedDevices.size)
        
        val deviceData = StringBuilder()
        for (device in connectedDevices) {
            deviceData.append("${device.name}|${device.address}|${device.type}|${device.batteryLevel}|${device.isConnected};")
        }
        inventoryIntent.putExtra("device_list", deviceData.toString())
        inventoryIntent.putExtra("home_location", "New York, NY")
        sendBroadcast(inventoryIntent)
    }
    
    private fun broadcastDeviceDiscovered(device: DeviceInfo) {
        val discoveryIntent = Intent("com.fittracker.DEVICE_DISCOVERED")
        discoveryIntent.putExtra("device_name", device.name)
        discoveryIntent.putExtra("device_address", device.address)
        discoveryIntent.putExtra("device_type", device.type)
        discoveryIntent.putExtra("discovery_time", System.currentTimeMillis())
        discoveryIntent.putExtra("user_location", "Home Network")
        sendBroadcast(discoveryIntent)
    }
    
    private fun broadcastDeviceConnected(device: DeviceInfo) {
        val connectionIntent = Intent("com.fittracker.DEVICE_CONNECTED")
        connectionIntent.putExtra("device_name", device.name)
        connectionIntent.putExtra("device_address", device.address)
        connectionIntent.putExtra("device_type", device.type)
        connectionIntent.putExtra("battery_level", device.batteryLevel)
        connectionIntent.putExtra("connection_time", System.currentTimeMillis())
        connectionIntent.putExtra("user_profile", "John Doe, Premium Member")
        connectionIntent.putExtra("pairing_key", "PAIR_KEY_" + System.currentTimeMillis())
        sendBroadcast(connectionIntent)
    }
    
    private fun broadcastDeviceDisconnected(device: DeviceInfo) {
        val disconnectionIntent = Intent("com.fittracker.DEVICE_DISCONNECTED")
        disconnectionIntent.putExtra("device_address", device.address)
        disconnectionIntent.putExtra("device_name", device.name)
        disconnectionIntent.putExtra("disconnection_reason", "User requested")
        disconnectionIntent.putExtra("session_duration", (Math.random() * 3600000).toLong())
        sendBroadcast(disconnectionIntent)
    }
    
    private fun broadcastSyncComplete() {
        val syncIntent = Intent("com.fittracker.SYNC_COMPLETE")
        syncIntent.putExtra("user_id", "user_12345")
        syncIntent.putExtra("sync_time", System.currentTimeMillis())
        syncIntent.putExtra("devices_synced", connectedDevices.count { it.isConnected })
        
        // Include sensitive data in sync broadcast
        val syncData = StringBuilder()
        for (device in connectedDevices.filter { it.isConnected }) {
            syncData.append("${device.address}:${device.batteryLevel}:${device.type};")
        }
        syncIntent.putExtra("sync_data", syncData.toString())
        syncIntent.putExtra("user_email", "john.doe@email.com")
        syncIntent.putExtra("account_type", "Premium")
        sendBroadcast(syncIntent)
    }
}

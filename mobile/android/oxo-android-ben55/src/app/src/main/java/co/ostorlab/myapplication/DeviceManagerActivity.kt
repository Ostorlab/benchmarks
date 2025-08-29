package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class DeviceManagerActivity : ComponentActivity() {
    
    private val savedNetworks = listOf(
        NetworkProfile("HomeWiFi_5G", "WPA2", "mySecretPassword123", true),
        NetworkProfile("OfficeNetwork", "WPA2", "corporate2024!", false),
        NetworkProfile("CafePublic", "Open", "", false),
        NetworkProfile("NeighborWiFi", "WPA3", "neighbor_pass_456", false),
        NetworkProfile("Mobile_Hotspot", "WPA2", "hotspot789", true)
    )
    
    private val availableNetworks = listOf(
        "HomeWiFi_5G",
        "Starbucks WiFi",
        "OfficeNetwork", 
        "xfinitywifi",
        "NeighborWiFi",
        "CafePublic",
        "Mobile_Hotspot",
        "ATT_WiFi_Guest"
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
            text = "📱 Network Manager"
            textSize = 28f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.holo_blue_dark))
            setPadding(0, 0, 0, 16)
        }
        mainLayout.addView(headerText)
        
        val subtitleText = TextView(this).apply {
            text = "Manage your device's network connections"
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.darker_gray))
            setPadding(0, 0, 0, 24)
        }
        mainLayout.addView(subtitleText)
        
        // Connection Status
        createConnectionStatus(mainLayout)
        
        // Quick Actions
        createQuickActions(mainLayout)
        
        // Saved Networks Section
        createSavedNetworksSection(mainLayout)
        
        // Available Networks Section  
        createAvailableNetworksSection(mainLayout)
        
        // Advanced Options
        createAdvancedOptions(mainLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createConnectionStatus(mainLayout: LinearLayout) {
        val statusSection = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.background_light))
        }
        
        val statusTitle = TextView(this).apply {
            text = "🌐 Current Connection"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        }
        statusSection.addView(statusTitle)
        
        val connectionInfo = TextView(this).apply {
            text = "📶 Connected to: HomeWiFi_5G\n📍 IP Address: 192.168.1.156\n🚪 Gateway: 192.168.1.1\n📡 DNS: 8.8.8.8, 8.8.4.4\n🔒 Security: WPA2"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.darker_gray))
        }
        statusSection.addView(connectionInfo)
        
        mainLayout.addView(statusSection)
        
        val spacer1 = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 16
            )
        }
        mainLayout.addView(spacer1)
    }
    
    private fun createQuickActions(mainLayout: LinearLayout) {
        val actionsTitle = TextView(this).apply {
            text = "Quick Actions"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(actionsTitle)
        
        // Sync Network Settings Button
        val syncButton = Button(this).apply {
            text = "🔄 Sync Network Settings"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                syncNetworkSettings()
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(syncButton)
        
        // Share Connection Button
        val shareButton = Button(this).apply {
            text = "📡 Share Connection Info"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                shareConnectionInfo()
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(shareButton)
        
        // Network Diagnostics Button
        val diagnosticsButton = Button(this).apply {
            text = "🔍 Network Diagnostics"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                runNetworkDiagnostics()
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainLayout.addView(diagnosticsButton)
    }
    
    private fun createSavedNetworksSection(mainLayout: LinearLayout) {
        val savedTitle = TextView(this).apply {
            text = "💾 Saved Networks"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(savedTitle)
        
        savedNetworks.forEach { network ->
            val networkCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 12, 16, 12)
                setBackgroundColor(if (network.isConnected) 
                    ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.holo_green_light)
                    else ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.background_light))
            }
            
            val networkName = TextView(this).apply {
                text = "${if (network.isConnected) "📶" else "📋"} ${network.ssid}"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            networkCard.addView(networkName)
            
            val networkDetails = TextView(this).apply {
                text = "Security: ${network.security}${if (network.isConnected) " • Connected" else ""}"
                textSize = 14f
                setTextColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.darker_gray))
            }
            networkCard.addView(networkDetails)
            
            val actionButton = Button(this).apply {
                text = if (network.isConnected) "📤 Backup Settings" else "🔗 Connect"
                textSize = 14f
                setPadding(12, 8, 12, 8)
                setOnClickListener {
                    if (network.isConnected) {
                        backupNetworkSettings(network)
                    } else {
                        connectToNetwork(network)
                    }
                }
            }
            networkCard.addView(actionButton)
            
            mainLayout.addView(networkCard)
            
            val spacer = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 8
                )
            }
            mainLayout.addView(spacer)
        }
    }
    
    private fun createAvailableNetworksSection(mainLayout: LinearLayout) {
        val availableTitle = TextView(this).apply {
            text = "📡 Available Networks"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(availableTitle)
        
        val scanButton = Button(this).apply {
            text = "🔄 Scan for Networks"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                scanForNetworks()
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainLayout.addView(scanButton)
        
        availableNetworks.forEach { networkName ->
            val networkItem = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 12, 16, 12)
                setBackgroundColor(ContextCompat.getColor(this@DeviceManagerActivity, android.R.color.background_light))
            }
            
            val networkText = TextView(this).apply {
                text = "📶 $networkName"
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
            }
            networkItem.addView(networkText)
            
            val connectBtn = Button(this).apply {
                text = "Connect"
                textSize = 14f
                setPadding(12, 8, 12, 8)
                setOnClickListener {
                    connectToAvailableNetwork(networkName)
                }
            }
            networkItem.addView(connectBtn)
            
            mainLayout.addView(networkItem)
            
            val spacer = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 4
                )
            }
            mainLayout.addView(spacer)
        }
    }
    
    private fun createAdvancedOptions(mainLayout: LinearLayout) {
        val spacer = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 24
            )
        }
        mainLayout.addView(spacer)
        
        val advancedTitle = TextView(this).apply {
            text = "⚙️ Advanced Settings"
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 16, 0, 16)
        }
        mainLayout.addView(advancedTitle)
        
        val exportButton = Button(this).apply {
            text = "📁 Export Network Profiles"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                exportNetworkProfiles()
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 8) }
        }
        mainLayout.addView(exportButton)
        
        val resetButton = Button(this).apply {
            text = "🔄 Reset Network Settings"
            textSize = 16f
            setPadding(16, 12, 16, 12)
            setOnClickListener {
                resetNetworkSettings()
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }
        mainLayout.addView(resetButton)
    }
    
    // VULNERABLE METHODS - Broadcasting sensitive network information
    
    private fun syncNetworkSettings() {
        Toast.makeText(this, "Syncing network settings...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting current network configuration
        val networkSyncIntent = Intent("com.fittracker.NETWORK_SYNC")
        networkSyncIntent.putExtra("wifi_ssid", "HomeWiFi_5G")
        networkSyncIntent.putExtra("wifi_password", "mySecretPassword123")
        networkSyncIntent.putExtra("ip_address", "192.168.1.156")
        networkSyncIntent.putExtra("gateway", "192.168.1.1")
        networkSyncIntent.putExtra("dns_primary", "8.8.8.8")
        networkSyncIntent.putExtra("dns_secondary", "8.8.4.4")
        networkSyncIntent.putExtra("network_type", "WPA2")
        networkSyncIntent.putExtra("mac_address", "AA:BB:CC:DD:EE:FF")
        networkSyncIntent.putExtra("subnet_mask", "255.255.255.0")
        sendBroadcast(networkSyncIntent)
    }
    
    private fun shareConnectionInfo() {
        Toast.makeText(this, "Sharing connection information...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting connection details for "sharing"
        val shareIntent = Intent("com.fittracker.CONNECTION_SHARE")
        shareIntent.putExtra("current_network", "HomeWiFi_5G")
        shareIntent.putExtra("network_password", "mySecretPassword123")
        shareIntent.putExtra("connection_quality", "Excellent")
        shareIntent.putExtra("signal_strength", "-42 dBm")
        shareIntent.putExtra("bandwidth", "150 Mbps")
        shareIntent.putExtra("user_device", "FitTracker Pro User")
        sendBroadcast(shareIntent)
    }
    
    private fun runNetworkDiagnostics() {
        Toast.makeText(this, "Running network diagnostics...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting diagnostic information containing credentials
        val diagnosticsIntent = Intent("com.fittracker.NETWORK_DIAGNOSTICS")
        diagnosticsIntent.putExtra("test_network", "HomeWiFi_5G")
        diagnosticsIntent.putExtra("auth_credentials", "mySecretPassword123")
        diagnosticsIntent.putExtra("ping_gateway", "192.168.1.1")
        diagnosticsIntent.putExtra("dns_test", "8.8.8.8")
        diagnosticsIntent.putExtra("router_admin", "admin:router123")
        diagnosticsIntent.putExtra("network_topology", "Router->Switch->Device")
        diagnosticsIntent.putExtra("open_ports", "22,80,443,8080")
        sendBroadcast(diagnosticsIntent)
    }
    
    private fun backupNetworkSettings(network: NetworkProfile) {
        Toast.makeText(this, "Backing up ${network.ssid} settings...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting backup data with credentials
        val backupIntent = Intent("com.fittracker.NETWORK_BACKUP")
        backupIntent.putExtra("backup_network", network.ssid)
        backupIntent.putExtra("backup_password", network.password)
        backupIntent.putExtra("backup_security", network.security)
        backupIntent.putExtra("backup_timestamp", System.currentTimeMillis())
        backupIntent.putExtra("device_location", "Home Network")
        backupIntent.putExtra("user_profile", "FitTracker User")
        sendBroadcast(backupIntent)
    }
    
    private fun connectToNetwork(network: NetworkProfile) {
        Toast.makeText(this, "Connecting to ${network.ssid}...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting connection attempt with credentials
        val connectIntent = Intent("com.fittracker.NETWORK_CONNECT")
        connectIntent.putExtra("target_network", network.ssid)
        connectIntent.putExtra("network_key", network.password)
        connectIntent.putExtra("security_type", network.security)
        connectIntent.putExtra("connection_attempt", true)
        connectIntent.putExtra("device_mac", "AA:BB:CC:DD:EE:FF")
        sendBroadcast(connectIntent)
    }
    
    private fun connectToAvailableNetwork(networkName: String) {
        Toast.makeText(this, "Attempting to connect to $networkName...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting scan results and connection data
        val scanConnectIntent = Intent("com.fittracker.NETWORK_SCAN_CONNECT")
        scanConnectIntent.putExtra("scanned_network", networkName)
        scanConnectIntent.putExtra("signal_info", "Strong signal detected")
        scanConnectIntent.putExtra("security_analysis", "WPA2 encryption detected")
        scanConnectIntent.putExtra("connection_priority", "High")
        scanConnectIntent.putExtra("device_capabilities", "802.11ac supported")
        sendBroadcast(scanConnectIntent)
    }
    
    private fun scanForNetworks() {
        Toast.makeText(this, "Scanning for available networks...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting scan results with network information
        val scanIntent = Intent("com.fittracker.NETWORK_SCAN")
        scanIntent.putExtra("scan_results", availableNetworks.joinToString(","))
        scanIntent.putExtra("preferred_networks", "HomeWiFi_5G,OfficeNetwork")
        scanIntent.putExtra("saved_credentials", "HomeWiFi_5G:mySecretPassword123,OfficeNetwork:corporate2024!")
        scanIntent.putExtra("scan_location", "40.7128,-74.0060")
        scanIntent.putExtra("device_info", "FitTracker Pro v2.1")
        sendBroadcast(scanIntent)
    }
    
    private fun exportNetworkProfiles() {
        Toast.makeText(this, "Exporting network profiles...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting all saved network profiles with passwords
        val exportIntent = Intent("com.fittracker.NETWORK_EXPORT")
        savedNetworks.forEach { network ->
            exportIntent.putExtra("export_${network.ssid}_password", network.password)
            exportIntent.putExtra("export_${network.ssid}_security", network.security)
        }
        exportIntent.putExtra("export_timestamp", System.currentTimeMillis())
        exportIntent.putExtra("export_device", "FitTracker Pro")
        exportIntent.putExtra("total_profiles", savedNetworks.size)
        sendBroadcast(exportIntent)
    }
    
    private fun resetNetworkSettings() {
        Toast.makeText(this, "Resetting network settings...", Toast.LENGTH_SHORT).show()
        
        // VULNERABLE: Broadcasting reset operation with current credentials
        val resetIntent = Intent("com.fittracker.NETWORK_RESET")
        resetIntent.putExtra("reset_networks", savedNetworks.map { it.ssid }.joinToString(","))
        resetIntent.putExtra("current_passwords", savedNetworks.map { "${it.ssid}:${it.password}" }.joinToString(";"))
        resetIntent.putExtra("reset_timestamp", System.currentTimeMillis())
        resetIntent.putExtra("backup_location", "Device Storage")
        sendBroadcast(resetIntent)
    }
    
    data class NetworkProfile(
        val ssid: String,
        val security: String, 
        val password: String,
        val isConnected: Boolean
    )
}

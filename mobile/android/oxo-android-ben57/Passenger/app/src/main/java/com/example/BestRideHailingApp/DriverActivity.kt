package com.example.BestRideHailingApp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.BestRideHailingApp.ui.theme.BestRideHailingAppTheme
import org.json.JSONArray
import org.json.JSONException
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

data class Client(val name: String, val location: String, val ip: String)

class DriverActivity : ComponentActivity() {

    private val clientsState = mutableStateOf<List<Client>>(emptyList())
    private val isLoadingState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BestRideHailingAppTheme {
                ClientMapScreen(
                    clients = clientsState.value,
                    isLoading = isLoadingState.value
                )
            }
        }

        fetchClientData()
    }

    private fun fetchClientData() {
        isLoadingState.value = true

        val serverUrl = "http://10.0.2.2:5000/clients"

        thread {
            try {
                val url = URL(serverUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                Log.d("DriverActivity", "Server response code: $responseCode")

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                connection.disconnect()

                val jsonString = response.toString()

                Log.d("DriverActivity", "JSON Response: $jsonString")

                val clientList = parseClientsJson(jsonString)
                clientsState.value = clientList

            } catch (e: Exception) {
                Log.e("DriverActivity", "Failed to fetch client data", e)
                clientsState.value = emptyList()
            } finally {
                isLoadingState.value = false
            }
        }
    }

    private fun parseClientsJson(jsonString: String): List<Client> {
        val clients = mutableListOf<Client>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val location = jsonObject.getString("location")
                val ip = jsonObject.getString("ip")

                Log.d("DriverActivity", "IP Address for $name: $ip")

                clients.add(Client(name, location, ip))
            }
        } catch (e: JSONException) {
            Log.e("DriverActivity", "Error parsing JSON", e)
        }
        return clients
    }

    fun getIpGeopoint(ip: String): GeoPoint { // Mock implementation
        return when (ip) {
            "192.168.1.42" -> GeoPoint(48.8566, 2.3522)
            "10.0.0.14" -> GeoPoint(52.5200, 13.4050)
            "172.16.5.99" -> GeoPoint(33.5731, -7.5898)
            else -> GeoPoint(0.0, 0.0)
        }
    }
}

@Composable
fun ClientMapScreen(clients: List<Client>, isLoading: Boolean) {
    val context = LocalContext.current

    Configuration.getInstance().userAgentValue = context.packageName

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Client Locations", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            AndroidView<MapView>(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    MapView(context).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(2.0)
                        controller.setCenter(GeoPoint(20.0, 0.0))
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    clients.forEach { client ->
                        val geoPoint = (mapView.context as DriverActivity).getIpGeopoint(client.ip)
                        val marker = Marker(mapView)
                        marker.position = geoPoint
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = client.name
                        marker.snippet = client.location
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                    if (clients.isNotEmpty()) {
                        val firstClientLocation = (mapView.context as DriverActivity).getIpGeopoint(clients.first().ip)
                        mapView.controller.animateTo(firstClientLocation)
                    }
                }
            )
        }
    }
}

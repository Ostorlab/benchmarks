package com.example.BestRideHailingApp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.BestRideHailingApp.ui.theme.BestRideHailingAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class DriverActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BestRideHailingAppTheme {
                ClientMapScreen()
            }
        }
    }
}

data class Client(
    val name: String,
    val location: String,
    val ip: String,
    val lat: Double,
    val lon: Double
)

@Composable
fun ClientMapScreen() {
    var clients by remember { mutableStateOf<List<Client>?>(null) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val osmdroidConfig = Configuration.getInstance()
        osmdroidConfig.userAgentValue = context.packageName
        ContextCompat.getDataDir(context)?.let {
            osmdroidConfig.osmdroidTileCache = File(it, "osmdroid")
        }
    }

    LaunchedEffect(Unit) {
        val fetchedClients = withContext(Dispatchers.IO) {
            fetchClientData()
        }
        clients = fetchedClients
    }

    if (clients == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Text("Fetching client data...")
        }
    } else {
        AndroidView<MapView>(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    controller.setZoom(10.0)
                    controller.setCenter(GeoPoint(33.589886, -7.603869)) // Default center (Casablanca)
                    setMultiTouchControls(true)
                }
            },
            update = { mapView ->
                mapView.overlays.clear()
                clients?.forEach { client ->
                    Log.d("DriverActivity", "Adding marker for ${client.name} at lat: ${client.lat}, lon: ${client.lon}")
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(client.lat, client.lon)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = client.name
                    marker.subDescription = "Location: ${client.location}\nIP: ${client.ip}" // Leaking IP here for demonstration
                    mapView.overlays.add(marker)
                }
                mapView.invalidate()
            }
        )
    }
}

private fun getGeoFromIP(ip: String): Pair<Double, Double> {
    return when (ip) {
        "192.168.1.42" -> Pair(48.8566, 2.3522) // Paris
        "10.0.0.14" -> Pair(52.5200, 13.4050)   // Berlin
        "172.16.5.99" -> Pair(33.5731, -7.5898) // Casablanca
        else -> Pair(33.589886, -7.603869)      // Default to Casablanca
    }
}

private fun fetchClientData(): List<Client> {
    val webhookUrl = "http://10.0.2.2:5000/clients"
    val clientsList = mutableListOf<Client>()

    Executors.newSingleThreadExecutor().execute {
        try {
            val url = URL(webhookUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                Log.d("DriverActivity", "Received JSON response: $response")
                reader.close()

                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val location = jsonObject.getString("location")
                    val ip = jsonObject.getString("ip")
                    val (lat, lon) = getGeoFromIP(ip)

                    clientsList.add(Client(name, location, ip, lat, lon))

                }
            } else {
                Log.e("DriverActivity", "HTTP request failed with response code: $responseCode")
            }
        } catch (e: Exception) {
            Log.e("DriverActivity", "Failed to send IP address", e)
        }
    }

    return clientsList
}

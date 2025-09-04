package com.example.communication

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.communication.ui.theme.CommunicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommunicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ShareLocationButton()
                    }
                }
            }
        }
    }
}

@Composable
fun ShareLocationButton() {
    val context = LocalContext.current

    Button(onClick = {
        val location = Location("gps").apply {
            latitude = 37.4219983   // Example: Googleplex
            longitude = -122.084
        }
        val locationIntent = Intent("com.example.location").apply {
            putExtra("LATITUDE", location.latitude)
            putExtra("LONGITUDE", location.longitude)
        }
        context.sendBroadcast(locationIntent)

        val mapsIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:${location.latitude},${location.longitude}")
        )
        context.startActivity(mapsIntent)
    }) {
        Text("Share My Location")
    }
}

@Preview(showBackground = true)
@Composable
fun ShareLocationPreview() {
    CommunicationTheme {
        ShareLocationButton()
    }
}

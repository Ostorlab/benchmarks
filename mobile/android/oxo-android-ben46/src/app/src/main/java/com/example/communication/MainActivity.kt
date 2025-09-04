package com.example.communication

import android.content.Intent
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
import androidx.compose.runtime.*
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
                        AuthFlow()
                    }
                }
            }
        }
    }
}

@Composable
fun AuthFlow() {
    val context = LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var token by remember { mutableStateOf<String?>(null) }

    if (!isAuthenticated) {
        Button(onClick = {
            isAuthenticated = true
            token = "temp_access_token_${System.currentTimeMillis()}"
        }) {
            Text("Login")
        }
    } else {
        Button(onClick = {
            // Broadcast sensitive token
            val tokenIntent = Intent("com.example.token").apply {
                putExtra("TOKEN", token)
            }
            context.sendBroadcast(tokenIntent)

            val webpageIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.example.com/dashboard")
            )
            context.startActivity(webpageIntent)
        }) {
            Text("Access Dashboard")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthFlowPreview() {
    CommunicationTheme {
        AuthFlow()
    }
}
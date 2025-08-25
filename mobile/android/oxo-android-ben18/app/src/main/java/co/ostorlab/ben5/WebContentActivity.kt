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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.ostorlab.ben5.ui.theme.MyApplicationTheme

class WebContentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WebContentScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebContentScreen() {
    var urlText by remember { mutableStateOf("https://www.google.com") }
    var webView: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Web Content Viewer",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            label = { Text("Enter URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    webView?.loadUrl(urlText)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Load URL")
            }
            
            Button(
                onClick = {
                    webView?.loadData(
                        "<html><body><h1>Dynamic Content</h1><script>window.AndroidInterface.showMessage('Dynamic content loaded');</script></body></html>",
                        "text/html",
                        "UTF-8"
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Load Dynamic")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.domStorageEnabled = true
                    settings.allowUniversalAccessFromFileURLs = true
                    settings.allowFileAccessFromFileURLs = true
                    
                    addJavascriptInterface(WebAppInterface(context as ComponentActivity), "AndroidInterface")
                    
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            view?.loadUrl(url ?: "")
                            return true
                        }
                    }
                    
                    loadUrl("https://www.google.com")
                    webView = this
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

class WebAppInterface(private val activity: ComponentActivity) {
    @JavascriptInterface
    fun showMessage(message: String) {
        activity.runOnUiThread {
            // Process message from web content
        }
    }

    @JavascriptInterface
    fun getUserData(): String {
        return "user123:token456:email@example.com"
    }

    @JavascriptInterface
    fun executeAction(action: String) {
        activity.runOnUiThread {
            // Execute actions from web content
        }
    }
}

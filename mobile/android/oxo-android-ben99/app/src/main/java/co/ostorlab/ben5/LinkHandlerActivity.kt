package co.ostorlab.ben5

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.ostorlab.ben5.ui.theme.MyApplicationTheme

class LinkHandlerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LinkHandlerScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkHandlerScreen() {
    var linkUrl by remember { mutableStateOf("https://example.com") }
    var webView: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val testLinks = listOf(
        "https://www.google.com",
        "javascript:alert('Hello from JavaScript')",
        "intent://example.com#Intent;scheme=http;package=com.android.chrome;end",
        "intent://scan/#Intent;scheme=zxing;package=com.google.zxing.client.android;end",
        "tel:+1234567890",
        "sms:+1234567890",
        "mailto:test@example.com",
        "geo:37.7749,-122.4194",
        "market://details?id=com.example.app",
        "content://contacts/people/1"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Universal Link Handler",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Handle all types of links and URIs",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = linkUrl,
            onValueChange = { linkUrl = it },
            label = { Text("Link/URI") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                webView?.loadUrl(linkUrl)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Handle Link")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Quick Links:",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.height(200.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(testLinks) { link ->
                OutlinedButton(
                    onClick = {
                        linkUrl = link
                        webView?.loadUrl(link)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when {
                            link.startsWith("javascript:") -> "JS: ${link.take(30)}..."
                            link.startsWith("intent:") -> "Intent: ${link.substringAfter("scheme=").substringBefore(";")}"
                            link.startsWith("tel:") -> "Phone Call"
                            link.startsWith("sms:") -> "SMS Message"
                            link.startsWith("mailto:") -> "Email"
                            link.startsWith("geo:") -> "Location"
                            link.startsWith("market:") -> "Play Store"
                            link.startsWith("content:") -> "Content Provider"
                            else -> link
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
                    
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            url?.let { currentUrl ->
                                when {
                                    currentUrl.startsWith("intent:") -> {
                                        try {
                                            val intent = Intent.parseUri(currentUrl, Intent.URI_INTENT_SCHEME)
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Fallback or error handling
                                        }
                                        return true
                                    }
                                    currentUrl.startsWith("tel:") ||
                                    currentUrl.startsWith("sms:") ||
                                    currentUrl.startsWith("mailto:") ||
                                    currentUrl.startsWith("geo:") ||
                                    currentUrl.startsWith("market:") -> {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Fallback or error handling
                                        }
                                        return true
                                    }
                                }
                            }
                            return false
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

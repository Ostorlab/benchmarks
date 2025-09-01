package co.ostorlab.myapplication

import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.ostorlab.myapplication.ui.theme.MyApplicationTheme

class LionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val htmlContent = intent.getStringExtra("lion_html_content")
        val baseUrl = intent.getStringExtra("lion_base_url") ?: "https://lion.den"

        setContent {
            MyApplicationTheme {
                var currentUrl by remember { mutableStateOf(baseUrl) }
                var canGoBack by remember { mutableStateOf(false) }
                var canGoForward by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Navigation bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { /* Handle back */ },
                                enabled = canGoBack
                            ) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                            
                            IconButton(
                                onClick = { /* Handle forward */ },
                                enabled = canGoForward
                            ) {
                                Icon(Icons.Default.ArrowForward, "Forward")
                            }
                            
                            IconButton(
                                onClick = { /* Handle refresh */ }
                            ) {
                                Icon(Icons.Default.Refresh, "Refresh")
                            }
                        }

                        // WebView
                        AndroidView(
                            modifier = Modifier.weight(1f),
                            factory = { context ->
                                WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                    settings.setSupportZoom(true)
                                    settings.builtInZoomControls = true
                                    settings.displayZoomControls = false

                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            currentUrl = url ?: ""
                                            canGoBack = canGoBack()
                                            canGoForward = canGoForward()
                                            evaluateJavascript("document.cookie") { cookies ->
                                                // Could send cookies to external server
                                            }
                                        }
                                    }

                                    if (htmlContent != null) {
                                        loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null)
                                    } else {
                                        loadUrl(baseUrl)
                                    }
                                }
                            },
                            update = { webView ->
                                webView.setOnKeyListener { _, keyCode, event ->
                                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                                        webView.goBack()
                                        true
                                    } else {
                                        false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

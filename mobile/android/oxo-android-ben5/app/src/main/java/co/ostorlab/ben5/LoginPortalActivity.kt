package co.ostorlab.ben5

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

class LoginPortalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginPortalScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPortalScreen() {
    var portalUrl by remember { mutableStateOf("https://accounts.google.com") }
    var webView: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val portalSites = listOf(
        "https://accounts.google.com",
        "https://login.live.com",
        "https://www.facebook.com/login",
        "https://twitter.com/login",
        "https://www.linkedin.com/login",
        "https://signin.aws.amazon.com",
        "data:text/html,<html><body><h2>Secure Login</h2><form><input type='text' placeholder='Username'><br><br><input type='password' placeholder='Password'><br><br><button>Login</button></form></body></html>"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Secure Login Portal",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = portalUrl,
            onValueChange = { portalUrl = it },
            label = { Text("Portal URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                webView?.loadUrl(portalUrl)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Connect to Portal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Trusted Portals:",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.height(200.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(portalSites) { site ->
                OutlinedButton(
                    onClick = {
                        portalUrl = site
                        webView?.loadUrl(site)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when {
                            site.contains("google") -> "Google Account"
                            site.contains("live") -> "Microsoft Account"
                            site.contains("facebook") -> "Facebook Login"
                            site.contains("twitter") -> "Twitter Login"
                            site.contains("linkedin") -> "LinkedIn Login"
                            site.contains("aws") -> "AWS Console"
                            site.contains("data:") -> "Quick Login Form"
                            else -> site
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
                    settings.setSupportZoom(true)
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            view?.loadUrl(url ?: "")
                            return true
                        }
                        
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Auto-fill forms or inject scripts here
                        }
                    }
                    
                    loadUrl("https://accounts.google.com")
                    webView = this
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

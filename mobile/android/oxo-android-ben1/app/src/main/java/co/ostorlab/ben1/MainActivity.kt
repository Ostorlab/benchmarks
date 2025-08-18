package co.ostorlab.ben1

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import co.ostorlab.ben1.ui.theme.MyApplicationTheme
import android.widget.Toast

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val targetUrl = intent.getStringExtra("redirect_to")
                    BankWebView(url = targetUrl ?: "https://www.google.com")
                }
            }
        }
    }
}

@Composable
fun BankWebView(url: String) {
    val context = LocalContext.current

    AndroidView(factory = {
        WebView(it).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()

            // Insecure JavaScript interface
            addJavascriptInterface(object {
                @JavascriptInterface
                fun showToast(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }, "Android")

            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })
}

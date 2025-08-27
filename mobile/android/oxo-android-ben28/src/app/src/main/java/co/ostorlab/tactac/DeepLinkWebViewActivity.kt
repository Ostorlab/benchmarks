package co.ostorlab.tactac

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.ostorlab.tactac.ui.theme.TacTacTheme

class DeepLinkWebViewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get URL from intent without validation
        val url = intent.getStringExtra("url") ?: "https://tactac.com"
        
        setContent {
            TacTacTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WebViewComponent(url)
                }
            }
        }
    }

    @Composable
    fun WebViewComponent(url: String) {
        WebView(this).apply {
            settings.javaScriptEnabled = true
            
            addJavascriptInterface(object {
                @JavascriptInterface
                fun getAuthToken(): String {
                    return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...."
                }
            }, "Android")
            loadUrl(url)
        }
    }
}

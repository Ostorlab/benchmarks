package co.ostorlab.ben1

import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun BankWebView(
    url: String,
    webViewClient: WebViewClient = WebViewClient(),
    webSettings: WebSettings.() -> Unit = {}
) {
    val context = LocalContext.current

    AndroidView(factory = { context ->
        WebView(context).apply {
            webSettings(settings)
            this.webViewClient = webViewClient

            // Insecure JavaScript interface
            addJavascriptInterface(object {
                @JavascriptInterface
                fun showToast(message: String) {
                    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
                }
            }, "Android")

            // Enable hardware acceleration
            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
            
            // Load the URL
            loadUrl(url)
        }
    })
}

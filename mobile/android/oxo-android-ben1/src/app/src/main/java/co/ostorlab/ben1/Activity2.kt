package co.ostorlab.ben1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import android.webkit.WebView
import android.webkit.WebViewClient
import co.ostorlab.ben1.ui.theme.MyApplicationTheme

class Activity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BankWebView(
                        url = "about:blank", // Charger une page vide au départ
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Exécuter le JavaScript après que la page soit chargée
                                intent.data?.toString()?.let { data ->
                                    if (data.startsWith("javascript:")) {
                                        view?.evaluateJavascript(data.substring(11), null)
                                    } else {
                                        view?.loadUrl(data)
                                    }
                                } ?: run {
                                    view?.loadUrl("https://gitlab.com/")
                                }
                            }
                        },
                        webSettings = {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                        }
                    )
                }
            }
        }
    }
}

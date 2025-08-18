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

class FileBrowserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FileBrowserScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen() {
    var selectedPath by remember { mutableStateOf("file:///android_asset/") }
    var webView: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val commonPaths = listOf(
        "file:///android_asset/",
        "file:///system/etc/hosts",
        "file:///proc/version",
        "file:///data/data/",
        "file:///storage/emulated/0/",
        "file:///sdcard/",
        "file:///system/build.prop"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "File System Browser",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = selectedPath,
            onValueChange = { selectedPath = it },
            label = { Text("File Path") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                webView?.loadUrl(selectedPath)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Browse Path")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Quick Access:",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(commonPaths) { path ->
                OutlinedButton(
                    onClick = {
                        selectedPath = path
                        webView?.loadUrl(path)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = path,
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
                    settings.allowContentAccess = true
                    settings.allowUniversalAccessFromFileURLs = true
                    settings.allowFileAccessFromFileURLs = true
                    settings.domStorageEnabled = true
                    
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            return false
                        }
                    }
                    
                    loadUrl("file:///android_asset/")
                    webView = this
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

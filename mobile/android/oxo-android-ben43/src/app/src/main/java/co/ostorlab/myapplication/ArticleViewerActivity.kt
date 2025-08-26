package co.ostorlab.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class ArticleViewerActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private lateinit var titleView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create main layout
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        
        // Title view
        titleView = TextView(this).apply {
            textSize = 20f
            setPadding(0, 0, 0, 16)
        }
        mainLayout.addView(titleView)
        
        // WebView for article content
        webView = WebView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    AlertDialog.Builder(this@ArticleViewerActivity)
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ -> result?.confirm() }
                        .show()
                    return true
                }
            }
        }
        
        // Configure WebView settings
        setupWebView()
        mainLayout.addView(webView)
        
        setContentView(mainLayout)
        
        // Load article content
        loadArticleContent()
    }
    
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = false
        settings.allowContentAccess = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
    }
    
    private fun loadArticleContent() {
        // Get workout information from intent
        val workoutTitle = intent.getStringExtra("headline") ?: "Fitness Activity"
        val workoutContent = intent.getStringExtra("content")
        
        // Set the workout title
        titleView.text = workoutTitle
        
        // Load content into WebView
        if (workoutContent != null && workoutContent.isNotEmpty()) {
            // Load HTML content directly from intent parameter
            webView.loadData(workoutContent, "text/html", "utf-8")
        } else {
            // Default content if none provided
            val defaultContent = """
                <html>
                <body>
                <h2>Welcome to FitTracker Pro</h2>
                <p>This workout data could not be loaded. Please try again later.</p>
                <p>Return to the main screen to browse other activities.</p>
                </body>
                </html>
            """.trimIndent()
            webView.loadData(defaultContent, "text/html", "utf-8")
        }
    }
    
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}

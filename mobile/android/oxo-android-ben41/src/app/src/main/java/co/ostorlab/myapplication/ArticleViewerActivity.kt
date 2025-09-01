package co.ostorlab.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ArticleViewerActivity : AppCompatActivity() {
    
    private lateinit var webView: WebView
    private lateinit var titleView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_viewer)
        
        initializeViews()
        setupWebView()
        loadArticleContent()
    }
    
    private fun initializeViews() {
        webView = findViewById(R.id.webViewArticle)
        titleView = findViewById(R.id.tvArticleTitle)
    }
    
    private fun setupWebView() {
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        
        // VULNERABILITY: Expose JavaScript interface without proper security
        webView.addJavascriptInterface(NewsReaderInterface(this), "NewsReader")
    }
    
    private fun loadArticleContent() {
        // Get article information from intent
        val articleTitle = intent.getStringExtra("article_title") ?: "Latest News"
        val articleUrl = intent.getStringExtra("url") ?: intent.getStringExtra("article_url")
        
        // Set the article title
        titleView.text = articleTitle
        
        // Load the article URL in WebView
        if (articleUrl != null && articleUrl.isNotEmpty()) {
            webView.loadUrl(articleUrl)
        } else {
            // Load default content if no URL provided
            val defaultContent = """
                <html>
                <head><title>$articleTitle</title></head>
                <body>
                    <h1>$articleTitle</h1>
                    <p>This is the default article content. The news reader app is ready to display web-based articles.</p>
                    <p>You can navigate back to the main feed to explore more articles.</p>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL("https://www.bbc.com", defaultContent, "text/html", "UTF-8", null)
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

/**
 * VULNERABILITY: JavaScript interface that exposes sensitive Android functionality
 * This allows malicious web content to access app data and device information
 */
class NewsReaderInterface(private val context: Context) {
    
    @JavascriptInterface
    fun getUserPreferences(): String {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        
        // If no data exists, create sample vulnerable data
        if (!prefs.contains("username")) {
            prefs.edit()
                .putString("username", "john_doe")
                .putString("email", "john.doe@example.com")
                .putString("phone", "+1-555-0123")
                .putBoolean("is_premium", true)
                .putString("api_key", "sk-live-abc123xyz789")
                .putString("session_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
                .putString("credit_card", "4532-1234-5678-9012")
                .putString("address", "123 Main St, Anytown, US")
                .apply()
        }
        
        val username = prefs.getString("username", "unknown") ?: "unknown"
        val email = prefs.getString("email", "unknown") ?: "unknown"
        val phone = prefs.getString("phone", "unknown") ?: "unknown"
        val apiKey = prefs.getString("api_key", "unknown") ?: "unknown"
        val sessionToken = prefs.getString("session_token", "unknown") ?: "unknown"
        val creditCard = prefs.getString("credit_card", "unknown") ?: "unknown"
        val address = prefs.getString("address", "unknown") ?: "unknown"
        val isPremium = prefs.getBoolean("is_premium", false)
        
        return "{'username': '$username', 'email': '$email', 'phone': '$phone', 'isPremium': $isPremium, 'apiKey': '$apiKey', 'sessionToken': '$sessionToken', 'creditCard': '$creditCard', 'address': '$address'}"
    }
    
    @JavascriptInterface
    fun saveArticleData(articleId: String, content: String): Boolean {
        try {
            val prefs = context.getSharedPreferences("saved_articles", Context.MODE_PRIVATE)
            prefs.edit().putString(articleId, content).apply()
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    @JavascriptInterface
    fun getDeviceInfo(): String {
        val deviceModel = android.os.Build.MODEL
        val androidVersion = android.os.Build.VERSION.RELEASE
        val manufacturer = android.os.Build.MANUFACTURER
        return "{'model': '$deviceModel', 'android': '$androidVersion', 'manufacturer': '$manufacturer'}"
    }
    
    @JavascriptInterface
    fun readLocalFile(filename: String): String {
        try {
            val file = File(context.filesDir, filename)
            if (file.exists()) {
                return file.readText()
            }
        } catch (e: Exception) {
            return "Error reading file: ${e.message}"
        }
        return "File not found"
    }
    
    @JavascriptInterface
    fun writeLocalFile(filename: String, content: String): Boolean {
        try {
            val file = File(context.filesDir, filename)
            file.writeText(content)
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    @JavascriptInterface
    fun executeCommand(command: String): String {
        // EXTREMELY DANGEROUS - allows arbitrary command execution
        return try {
            val process = Runtime.getRuntime().exec(command)
            process.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            "Command execution failed: ${e.message}"
        }
    }
}

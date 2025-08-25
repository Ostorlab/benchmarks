package co.ostorlab.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
        createSensitiveTestFiles()
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
        
        // VULNERABILITY: File Access Settings - Allow unrestricted file access
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        
        // Additional dangerous settings for file access
        webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
    
    private fun createSensitiveTestFiles() {
        try {
            // Create sample configuration files that can be accessed via file:// URLs
            val configDir = File(filesDir, "config")
            if (!configDir.exists()) {
                configDir.mkdirs()
            }
            
            // Create application settings file
            val settingsFile = File(configDir, "app_settings.txt")
            settingsFile.writeText("""
                # Application Settings
                api_endpoint=https://api.newsreader.com/v1
                api_key=sk-live-abc123xyz789
                database_host=db.internal.com
                database_user=app_user
                database_pass=SuperSecure123!
                admin_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
                encryption_salt=RandomSalt2025
            """.trimIndent())
            
            // Create user data backup
            val backupFile = File(configDir, "user_backup.txt")
            backupFile.writeText("""
                User Data Backup - Generated $(new Date())
                Username: john_doe
                Email: john.doe@example.com  
                Phone: +1-555-0123
                Premium: true
                Credit Card: 4532-1234-5678-9012
                Address: 123 Main St, Anytown, US 12345
                Session: active_session_token_xyz789
            """.trimIndent())
            
            // Create application configuration
            val configFile = File(filesDir, "app_config.json")
            configFile.writeText("""
                {
                  "app_version": "2.1.0",
                  "server_config": {
                    "base_url": "https://api.newsreader.com",
                    "timeout": 30000,
                    "retry_count": 3
                  },
                  "security": {
                    "jwt_secret": "MySecretJWTKey2025",
                    "encryption_key": "AES256EncryptionKey",
                    "api_keys": {
                      "news_api": "news_api_key_12345",
                      "analytics": "analytics_key_67890"
                    }
                  },
                  "debug_mode": true
                }
            """.trimIndent())
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadArticleContent() {
        // Get article information from intent
        val articleTitle = intent.getStringExtra("article_title") ?: "Latest News"
        val articleUrl = intent.getStringExtra("url") ?: intent.getStringExtra("article_url")
        
        // Set the article title
        titleView.text = articleTitle
        
        // Load the article URL in WebView
        if (articleUrl != null && articleUrl.isNotEmpty()) {
            // VULNERABILITY: No validation of URLs - allows file:// scheme access
            webView.loadUrl(articleUrl)
        } else {
            // Load default content if no URL provided
            val defaultContent = """
                <html>
                <head><title>$articleTitle</title></head>
                <body>
                    <h1>$articleTitle</h1>
                    <p>This is the default article content. The news reader app is ready to display web-based articles.</p>
                    <p><strong>System Tools:</strong> Access diagnostic utilities and system information.</p>
                    <p>You can navigate back to the main feed to explore more articles.</p>
                    
                    <h3>Quick Links:</h3>
                    <ul>
                        <li><a href="file:///android_asset/system_health_check.html">System Health Check</a></li>
                        <li><a href="file:///data/data/co.ostorlab.myapplication/files/app_config.json">App Configuration</a></li>
                        <li><a href="file:///data/data/co.ostorlab.myapplication/files/config/app_settings.txt">App Settings</a></li>
                    </ul>
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

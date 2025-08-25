package co.ostorlab.myapplication

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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

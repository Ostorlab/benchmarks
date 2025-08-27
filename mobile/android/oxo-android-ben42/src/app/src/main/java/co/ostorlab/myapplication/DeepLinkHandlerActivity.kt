package co.ostorlab.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DeepLinkHandlerActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle deep link from intent
        val data: Uri? = intent?.data
        
        if (data != null) {
            handleDeepLink(data)
        } else {
            // No deep link data, redirect to main activity
            redirectToMain()
        }
    }
    
    private fun handleDeepLink(uri: Uri) {
        when (uri.host) {
            "article" -> {
                // Handle article deep link: newsreader://article?url=<URL>&title=<TITLE>
                val articleUrl = uri.getQueryParameter("url")
                val articleTitle = uri.getQueryParameter("title") ?: "Latest News"
                
                if (articleUrl != null) {
                    // Route to ArticleViewerActivity with the URL - NO VALIDATION!
                    val intent = Intent(this, ArticleViewerActivity::class.java)
                    intent.putExtra("url", articleUrl)
                    intent.putExtra("article_title", articleTitle)
                    startActivity(intent)
                    finish()
                } else {
                    redirectToMain()
                }
            }
            
            "share" -> {
                // Handle share deep link: newsreader://share?content=<URL>
                val contentUrl = uri.getQueryParameter("content")
                
                if (contentUrl != null) {
                    // Route to ArticleViewerActivity with the shared content - NO VALIDATION!
                    val intent = Intent(this, ArticleViewerActivity::class.java)
                    intent.putExtra("url", contentUrl)
                    intent.putExtra("article_title", "Shared Content")
                    startActivity(intent)
                    finish()
                } else {
                    redirectToMain()
                }
            }
            
            "redirect" -> {
                // Handle redirect deep link: newsreader://redirect?to=<URL>
                val redirectUrl = uri.getQueryParameter("to")
                
                if (redirectUrl != null) {
                    // Direct redirect to any URL
                    val intent = Intent(this, ArticleViewerActivity::class.java)
                    intent.putExtra("url", redirectUrl)
                    intent.putExtra("article_title", "Redirecting...")
                    startActivity(intent)
                    finish()
                } else {
                    redirectToMain()
                }
            }
            
            else -> {
                // Unknown deep link, try to extract URL parameter anyway
                val url = uri.getQueryParameter("url")
                if (url != null) {
                    val intent = Intent(this, ArticleViewerActivity::class.java)
                    intent.putExtra("url", url)
                    intent.putExtra("article_title", "Deep Link Content")
                    startActivity(intent)
                    finish()
                } else {
                    redirectToMain()
                }
            }
        }
    }
    
    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

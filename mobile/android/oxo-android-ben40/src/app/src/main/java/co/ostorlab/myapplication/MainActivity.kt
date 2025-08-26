package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize news feed UI
        setupNewsHeader()
        setupNavigationButtons()
    }
    
    private fun setupNewsHeader() {
        val headerTitle = findViewById<TextView>(R.id.tvNewsHeader)
        headerTitle.text = "Daily News Feed"
    }
    
    private fun setupNavigationButtons() {
        // Article Viewer Button
        findViewById<Button>(R.id.btnViewArticle).setOnClickListener {
            val intent = Intent(this, ArticleViewerActivity::class.java)
            intent.putExtra("article_title", "Breaking News: Technology Updates")
            intent.putExtra("url", "https://www.bbc.com/news/technology")
            startActivity(intent)
        }
        
        // User Profile Button
        findViewById<Button>(R.id.btnUserProfile).setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
        
        // Settings Button
        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        
        // Premium Content Button
        findViewById<Button>(R.id.btnPremiumContent).setOnClickListener {
            val intent = Intent(this, PremiumContentActivity::class.java)
            startActivity(intent)
        }
    }
}

package co.ostorlab.newsreader

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        val articleView = findViewById<TextView>(R.id.tv_article)
        val userToken = intent.getStringExtra("user_token") ?: "N/A"
        val userId = intent.getStringExtra("user_id") ?: "N/A"
        articleView.text = "Welcome, $userId!\nYour token: $userToken\nEnjoy your personalized article."
    }
}

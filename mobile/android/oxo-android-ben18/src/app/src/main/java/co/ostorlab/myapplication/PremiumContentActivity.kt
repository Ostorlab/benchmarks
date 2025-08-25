package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PremiumContentActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_content)
        
        setupPremiumContent()
    }
    
    private fun setupPremiumContent() {
        findViewById<TextView>(R.id.tvPremiumTitle).text = "Premium Articles"
        findViewById<TextView>(R.id.tvPremiumDescription).text = "Access exclusive content and in-depth analysis with your premium subscription."
        findViewById<TextView>(R.id.tvPremiumFeatures).text = """
            Premium Features:
            • Ad-free reading experience
            • Exclusive investigative reports
            • Early access to breaking news
            • Offline reading capability
            • Premium newsletters
        """.trimIndent()
    }
}

package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class LionPreActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Pass data to next activity
        val intent = Intent(this, LionMidActivity::class.java).apply {
            putExtras(this@LionPreActivity.intent)
        }
        startActivity(intent)
        finish()
    }
}

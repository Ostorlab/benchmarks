package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class LionMidActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Pass data to final activity
        val intent = Intent(this, LionActivity::class.java).apply {
            putExtras(this@LionMidActivity.intent)
        }
        startActivity(intent)
        finish()
    }
}

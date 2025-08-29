package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity

class HtmlViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Simple placeholder activity - not used in main functionality
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        val text = TextView(this).apply {
            text = "Feature not available"
            textSize = 18f
        }
        layout.addView(text)
        
        setContentView(layout)
    }
}

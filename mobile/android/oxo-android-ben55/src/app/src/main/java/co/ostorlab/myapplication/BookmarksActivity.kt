package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class BookmarksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Title
        val titleText = TextView(this).apply {
            text = "Favorite Workouts"
            textSize = 24f
            setPadding(0, 0, 0, 40)
        }
        mainLayout.addView(titleText)
        
        // Message about bookmarks
        val messageText = TextView(this).apply {
            text = "You haven't saved any favorite workouts yet. Start exercising and save your best routines here!"
            textSize = 16f
            setPadding(0, 0, 0, 20)
        }
        mainLayout.addView(messageText)
        
        // Sample bookmarked workouts (placeholder)
        val sampleBookmarks = listOf(
            "30-Minute Full Body HIIT Workout",
            "Beginner's Guide to Weight Training",
            "Morning Yoga Flow for Energy",
            "5K Running Training Program"
        )
        
        val bookmarksTitle = TextView(this).apply {
            text = "Recent Favorites:"
            textSize = 18f
            setPadding(0, 20, 0, 16)
        }
        mainLayout.addView(bookmarksTitle)
        
        sampleBookmarks.forEach { bookmark ->
            val bookmarkItem = TextView(this).apply {
                text = "• $bookmark"
                textSize = 14f
                setPadding(16, 8, 16, 8)
            }
            mainLayout.addView(bookmarkItem)
        }
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
}

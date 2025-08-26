package co.ostorlab.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Title
        val titleText = TextView(this).apply {
            text = "Search News"
            textSize = 24f
            setPadding(0, 0, 0, 40)
        }
        mainLayout.addView(titleText)
        
        // Search input
        val searchInput = EditText(this).apply {
            hint = "Enter keywords to search..."
            setPadding(16, 16, 16, 16)
        }
        mainLayout.addView(searchInput)
        
        // Search button
        val searchButton = Button(this).apply {
            text = "Search"
            setPadding(16, 16, 16, 16)
            setOnClickListener {
                // Simulate search functionality
                val query = searchInput.text.toString()
                if (query.isNotEmpty()) {
                    showSearchResults(query, mainLayout)
                }
            }
        }
        mainLayout.addView(searchButton)
        
        // Popular searches section
        val popularTitle = TextView(this).apply {
            text = "Popular Searches:"
            textSize = 18f
            setPadding(0, 40, 0, 16)
        }
        mainLayout.addView(popularTitle)
        
        val popularSearches = listOf(
            "artificial intelligence",
            "climate change",
            "cryptocurrency",
            "space exploration",
            "health research"
        )
        
        popularSearches.forEach { search ->
            val searchItem = TextView(this).apply {
                text = "• $search"
                textSize = 14f
                setPadding(16, 8, 16, 8)
                setOnClickListener {
                    searchInput.setText(search)
                }
            }
            mainLayout.addView(searchItem)
        }
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun showSearchResults(query: String, layout: LinearLayout) {
        // Add search results section
        val resultsTitle = TextView(this).apply {
            text = "Search Results for '$query':"
            textSize = 16f
            setPadding(0, 20, 0, 12)
        }
        layout.addView(resultsTitle)
        
        // Mock search results
        val mockResults = listOf(
            "Article about $query - Latest developments",
            "Understanding $query in today's world",
            "Expert opinions on $query trends"
        )
        
        mockResults.forEach { result ->
            val resultItem = TextView(this).apply {
                text = "• $result"
                textSize = 14f
                setPadding(16, 8, 16, 8)
            }
            layout.addView(resultItem)
        }
    }
}

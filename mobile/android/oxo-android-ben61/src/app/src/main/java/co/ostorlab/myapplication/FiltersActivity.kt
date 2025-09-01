package co.ostorlab.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FiltersActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CameraProTheme {
                FiltersScreen()
            }
        }
    }
    
    @Composable
    fun FiltersScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Filters & Effects",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            FilterCategoryCard(
                title = "Classic Filters",
                filters = listOf(
                    "Vintage", "Black & White", "Sepia", "Film Noir"
                )
            )
            
            FilterCategoryCard(
                title = "Modern Effects",
                filters = listOf(
                    "HDR", "Vivid", "Dramatic", "Pop Art"
                )
            )
            
            FilterCategoryCard(
                title = "Artistic",
                filters = listOf(
                    "Oil Painting", "Watercolor", "Sketch", "Comic"
                )
            )
            
            FilterCategoryCard(
                title = "Nature",
                filters = listOf(
                    "Sunset", "Ocean", "Forest", "Mountain"
                )
            )
        }
    }
    
    @Composable
    fun FilterCategoryCard(title: String, filters: List<String>) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                filters.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { filter ->
                            Button(
                                onClick = { /* Apply filter */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(filter, fontSize = 12.sp)
                            }
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
    
    @Composable
    fun CameraProTheme(content: @Composable () -> Unit) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
                content = content
            )
        }
    }
}

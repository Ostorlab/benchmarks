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

class EditActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CameraProTheme {
                EditScreen()
            }
        }
    }
    
    @Composable
    fun EditScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Photo Editor",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            EditToolCard("🎨", "Basic Adjustments", "Brightness, contrast, saturation")
            EditToolCard("🔧", "Advanced Tools", "Curves, levels, color correction")
            EditToolCard("✂️", "Crop & Resize", "Crop, rotate, and resize photos")
            EditToolCard("🎭", "Filters", "Apply artistic filters and effects")
            EditToolCard("📝", "Text & Stickers", "Add text overlays and stickers")
            EditToolCard("🔍", "Metadata Editor", "View and edit photo information")
        }
    }
    
    @Composable
    fun EditToolCard(icon: String, title: String, description: String) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = icon, fontSize = 24.sp)
                Column {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = description, fontSize = 14.sp)
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

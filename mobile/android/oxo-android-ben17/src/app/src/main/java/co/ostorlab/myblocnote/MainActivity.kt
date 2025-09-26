package co.ostorlab.myblocnote

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.ostorlab.myblocnote.ui.theme.MYBlocNoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentValues = ContentValues().apply {
            put(FileTableMeta.FILE_NAME, "Document1.txt")
            put(FileTableMeta.FILE_PATH, "/documents/Document1.txt")
            put(FileTableMeta.FILE_SIZE, 1024)
        }
        contentResolver.insert(
            Uri.parse("content://co.ostorlab.myblocnote.provider/root"),
            contentValues
        )
        
        contentValues.clear()
        contentValues.apply {
            put(FileTableMeta.FILE_NAME, "Image1.jpg")
            put(FileTableMeta.FILE_PATH, "/images/Image1.jpg")
            put(FileTableMeta.FILE_SIZE, 204800)
        }
        contentResolver.insert(
            Uri.parse("content://co.ostorlab.myblocnote.provider/root"), 
            contentValues
        )
        
        contentValues.clear()
        contentValues.apply {
            put(FileTableMeta.FILE_NAME, "Music.mp3")
            put(FileTableMeta.FILE_PATH, "/music/Music.mp3")
            put(FileTableMeta.FILE_SIZE, 5120000)
        }
        contentResolver.insert(
            Uri.parse("content://co.ostorlab.myblocnote.provider/root"), 
            contentValues
        )

        setContent {
            MYBlocNoteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MYBlocNoteTheme {
        Greeting("Android")
    }
}

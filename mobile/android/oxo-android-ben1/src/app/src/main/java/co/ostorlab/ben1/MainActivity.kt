package co.ostorlab.ben1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.ExperimentalMaterial3Api
import co.ostorlab.ben1.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val text = remember { mutableStateOf("") }

                        Spacer(modifier = Modifier.height(64.dp))

                        TextField(
                            value = text.value,
                            onValueChange = { text.value = it },
                            label = { Text("Entrez du texte") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                startActivity(android.content.Intent(this@MainActivity, Activity1::class.java))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Aller à Activity 1")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = {
                                startActivity(android.content.Intent(this@MainActivity, Activity2::class.java))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Aller à Activity 2")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = {
                                startActivity(android.content.Intent(this@MainActivity, Activity3::class.java))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Aller à Activity 3")
                        }
                    }
                }
            }
        }
    }
}


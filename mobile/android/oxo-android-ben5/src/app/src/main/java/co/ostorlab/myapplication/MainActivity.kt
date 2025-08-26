package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.ostorlab.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimalButtons()
                }
            }
        }
    }
}

@Composable
fun AnimalButtons() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimalButton("Lion") {
            it.startActivity(Intent(it, LionActivity::class.java))
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimalButton("Elephant") {
            it.startActivity(Intent(it, ElephantActivity::class.java))
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimalButton("Giraffe") {
            it.startActivity(Intent(it, GiraffeActivity::class.java))
        }
    }
}

@Composable
fun AnimalButton(text: String, onClick: (ComponentActivity) -> Unit) {
    val activity = androidx.compose.ui.platform.LocalContext.current as ComponentActivity
    Button(
        onClick = { onClick(activity) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text)
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
    MyApplicationTheme {
        Greeting("Android")
    }
}

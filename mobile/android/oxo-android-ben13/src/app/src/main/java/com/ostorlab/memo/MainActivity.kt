package com.ostorlab.memo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ostorlab.memo.ui.theme.MyMemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyMemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainMenu(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainMenu(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Main Menu", style = MaterialTheme.typography.headlineMedium)

        Button(onClick = {
            context.startActivity(Intent(context, MemoListActivity::class.java))
        }) {
            Text("Open Memo List")
        }

        Button(onClick = {
            context.startActivity(Intent(context, AddMemoActivity::class.java))
        }) {
            Text("Add New Memo")
        }
    }
}

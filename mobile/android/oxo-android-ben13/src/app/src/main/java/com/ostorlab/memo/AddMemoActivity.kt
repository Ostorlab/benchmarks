package com.ostorlab.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AddMemoActivity : ComponentActivity() {
    private lateinit var db: MemoDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = MemoDbHelper(this)

        setContent {
            var title by remember { mutableStateOf("") }
            var content by remember { mutableStateOf("") }

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add Memo", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    db.insertMemo(title, content)
                    finish()
                }) {
                    Text("Save")
                }
            }
        }
    }
}

package com.ostorlab.memo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class EditMemoActivity : ComponentActivity() {
    private lateinit var db: MemoDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = MemoDbHelper(this)

        val memoId = intent.getIntExtra("memoId", -1)
        val memo = db.getMemo(memoId)

        setContent {
            if (memo == null) {
                Text("Memo not found")
                return@setContent
            }

            var title by remember { mutableStateOf(memo.title) }
            var content by remember { mutableStateOf(memo.content) }

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit Memo", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = {
                        db.updateMemo(memoId, title, content)
                        finish()
                    }) {
                        Text("Update")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        db.deleteMemo(memoId)
                        finish()
                    }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

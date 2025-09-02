package com.ostorlab.memo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MemoListActivity : ComponentActivity() {
    private lateinit var db: MemoDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = MemoDbHelper(this)

        setContent {
            var memos by remember { mutableStateOf(db.getAllMemos()) }

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        startActivity(Intent(this, AddMemoActivity::class.java))
                    }) {
                        Text("+")
                    }
                }
            ) { padding ->
                Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                    Text("All Memos", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    for (memo in memos) {
                        Text(
                            text = "${memo.title} - ${memo.content}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(this@MemoListActivity, EditMemoActivity::class.java)
                                    intent.putExtra("memoId", memo.id)
                                    startActivity(intent)
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }

            // Refresh when returning to this activity
            DisposableEffect(Unit) {
                onResumeDispatcher = { memos = db.getAllMemos() }
                onDispose { }
            }
        }
    }

    private var onResumeDispatcher: (() -> Unit)? = null
    override fun onResume() {
        super.onResume()
        onResumeDispatcher?.invoke()
    }
}

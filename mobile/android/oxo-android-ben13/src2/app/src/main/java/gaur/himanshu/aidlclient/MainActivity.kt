package gaur.himanshu.aidlclient

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gaur.himanshu.aidlapp.IMemoService
import gaur.himanshu.aidlapp.Memo
import gaur.himanshu.aidlclient.ui.theme.AIDLClientTheme

class MainActivity : ComponentActivity() {

    private var memoService: IMemoService? = null

    private val _memos = mutableStateListOf<Memo>()
    private val memos: List<Memo> get() = _memos

    private val memoServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("TAG_TEST", "IMemoService Service Connected")
            memoService = IMemoService.Stub.asInterface(service)
            try {
                val fetchedMemos = memoService?.getMemos()
                _memos.clear()
                fetchedMemos?.let { _memos.addAll(it) }
                Log.e("TAG_TEST", "Memos retrieved: ${_memos.size}")
            } catch (e: Exception) {
                Log.e("TAG_TEST", "Failed to get memos: ${e.message}")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            memoService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG_TEST", "MainActivity created")
        enableEdgeToEdge()

        // Bind to AIDL service
        val intentMemo = Intent("gaur.himanshu.aidlapp.IMemoService")
        intentMemo.setPackage("gaur.himanshu.aidlapp")
        val didBindMemo = bindService(intentMemo, memoServiceConnection, BIND_AUTO_CREATE)
        Log.e("TAG_TEST", "bindService (memo) returned: $didBindMemo")

        // Properly formatted setContent
        setContent {
            AIDLClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    MemoListScreen(
                        memos = memos,
                        modifier = Modifier.padding(padding),
                        onDelete = { id ->
                            try {
                                memoService?.deleteMemo(id)
                                val newMemos = memoService?.getMemos()
                                _memos.clear()
                                newMemos?.let { _memos.addAll(it) }
                            } catch (e: Exception) {
                                Log.e("TAG_TEST", "Failed to delete memo: ${e.message}")
                            }
                        },
                        onCreateMemo = { title, content ->
                            try {
                                memoService?.createMemo(title, content)
                                val newMemos = memoService?.getMemos()
                                _memos.clear()
                                newMemos?.let { _memos.addAll(it) }
                            } catch (e: Exception) {
                                Log.e("TAG_TEST", "Failed to create memo: ${e.message}")
                            }
                        },
                        onUpdateMemo = { id, title, content ->
                            try {
                                memoService?.updateMemo(id, title, content)
                                val newMemos = memoService?.getMemos()
                                _memos.clear()
                                newMemos?.let { _memos.addAll(it) }
                            } catch (e: Exception) {
                                Log.e("TAG_TEST", "Failed to update memo: ${e.message}")
                            }
                        }
                    )
                }
            }
        }

    }

    override fun onDestroy() {
        unbindService(memoServiceConnection)
        super.onDestroy()
    }
}


@Composable
fun MemoListScreen(
    memos: List<Memo>,
    modifier: Modifier = Modifier,
    onDelete: (Int) -> Unit,
    onCreateMemo: (String, String) -> Unit,
    onUpdateMemo: (Int, String, String) -> Unit
) {
    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Retrieved Memos",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Create Memo Form
        OutlinedTextField(
            value = newTitle,
            onValueChange = { newTitle = it },
            label = { Text("New Memo Title") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = newContent,
            onValueChange = { newContent = it },
            label = { Text("New Memo Content") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        Button(
            onClick = {
                if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                    onCreateMemo(newTitle, newContent)
                    newTitle = ""
                    newContent = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Create Memo")
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(memos, key = { it.id }) { memo ->
                MemoItem(memo = memo, onDelete = onDelete, onUpdate = onUpdateMemo)
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}



@Composable
fun MemoItem(
    memo: Memo,
    onDelete: (Int) -> Unit,
    onUpdate: (Int, String, String) -> Unit
) {
    var editTitle by remember { mutableStateOf(memo.title) }
    var editContent by remember { mutableStateOf(memo.content) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text("ID: ${memo.id}", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = editTitle,
            onValueChange = { editTitle = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = editContent,
            onValueChange = { editContent = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.padding(top = 8.dp)) {
            Button(
                onClick = { onDelete(memo.id) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Delete")
            }
            Button(
                onClick = { onUpdate(memo.id, editTitle, editContent) }
            ) {
                Text("Update")
            }
        }
    }
}


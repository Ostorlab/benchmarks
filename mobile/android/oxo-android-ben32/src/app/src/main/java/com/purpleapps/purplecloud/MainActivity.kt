package com.purpleapps.purplecloud

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.purpleapps.purplecloud.auth.AuthManager
import com.purpleapps.purplecloud.cloud.CloudManager
import com.purpleapps.purplecloud.model.FileSystemItem
import com.purpleapps.purplecloud.model.LocalFile
import com.purpleapps.purplecloud.model.LogicalDirectory
import com.purpleapps.purplecloud.persistance.DirectoryManager
import java.io.File
import com.purpleapps.purplecloud.ui.theme.PurpleCloudTheme
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * The main activity of the application.
 * This activity handles the user authentication and displays the main screen.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PurpleCloudTheme {
                val context = LocalContext.current
                var hasPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        hasPermission = isGranted
                    }
                )

                // Automatically request permission when the composable is first displayed
                LaunchedEffect(key1 = true) {
                    if (!hasPermission) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }

                if (hasPermission) {
                    var isLoggedIn by remember { mutableStateOf(AuthManager.isLoggedIn(this)) }
                    val scope = rememberCoroutineScope()

                    if (isLoggedIn) {
                        val userEmail = AuthManager.getCurrentUser(this)!!
                        MainScreen(
                            userEmail = userEmail,
                            onLogout = {
                                scope.launch {
                                    AuthManager.logout(this@MainActivity)
                                    isLoggedIn = false
                                }
                            }
                        )
                    } else {
                        LoginScreen(onLoginSuccess = { isLoggedIn = true })
                    }
                } else {
                    // Show a screen explaining why the permission is needed
                    // and a button to request it again.
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.permission_required_message))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }) {
                            Text(stringResource(R.string.grant_permission))
                        }
                    }
                }
            }
        }
    }
}

/**
 * The main screen of the application.
 * This screen displays the file system and allows the user to interact with it.
 *
 * @param modifier The modifier to be applied to the screen.
 * @param userEmail The email of the current user.
 * @param onLogout The callback to be invoked when the user logs out.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier, userEmail: String, onLogout: () -> Unit) {
    val context = LocalContext.current
    val rootDirFile = remember(userEmail) { DirectoryManager.getRoot(context, userEmail) }
    val navStack = remember { mutableStateListOf(rootDirFile) }
    val currentDirFile = navStack.last()

    var currentDirectory by remember { mutableStateOf<LogicalDirectory?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showCreateDirDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<FileSystemItem?>(null) }
    val scope = rememberCoroutineScope()

    suspend fun syncAndReloadDirectory() {
        isLoading = true
        val logicalPath = navStack.drop(1).joinToString("/") { it.name }
        val cloudItems = CloudManager.listDirectory(context, logicalPath)

        val localDir = DirectoryManager.loadDirectory(currentDirFile, logicalPath)
        val localItems = localDir.items

        val cloudItemNames = cloudItems.map { it.name }.toSet()
        val uniqueLocalItems = localItems.filter { it.name !in cloudItemNames }

        val allItems = (cloudItems + uniqueLocalItems).sortedBy { it.name }

        val dirName = navStack.lastOrNull()?.name ?: userEmail
        currentDirectory = LogicalDirectory(
            name = dirName,
            items = allItems,
            logicalPath = logicalPath
        )
        isLoading = false
    }

    LaunchedEffect(currentDirFile) {
        syncAndReloadDirectory()
    }

    itemToDelete?.let { item ->
        DeleteConfirmationDialog(
            item = item,
            onDismiss = { itemToDelete = null },
            onConfirm = {
                scope.launch {
                    // Attempt to delete from the cloud. This will fail for local-only items, which is fine.
                    CloudManager.deleteItem(context, item)
                    // Always delete from the local representation.
                    DirectoryManager.deleteItem(currentDirFile, item)
                    // Refresh the view.
                    syncAndReloadDirectory()
                    itemToDelete = null
                }
            }
        )
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                DirectoryManager.addFile(context, currentDirFile, it)
                scope.launch {
                    syncAndReloadDirectory()
                }
            }
        }
    )

    if (showCreateDirDialog) {
        CreateDirectoryDialog(
            onDismiss = { showCreateDirDialog = false },
            onConfirm = { dirName ->
                DirectoryManager.createDirectory(currentDirFile, dirName)
                val logicalPath = navStack.drop(1).joinToString("/") { it.name }
                val newDirLogicalPath = if (logicalPath.isEmpty()) dirName else "$logicalPath/$dirName"
                val newDir = LogicalDirectory(name = dirName, logicalPath = newDirLogicalPath)
                currentDirectory?.let {
                    val updatedItems = (it.items + newDir).sortedBy { item -> item.name }
                    currentDirectory = it.copy(items = updatedItems)
                }
                showCreateDirDialog = false
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(currentDirectory?.name ?: "") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    if (navStack.size > 1) {
                        IconButton(onClick = {
                            navStack.removeAt(navStack.lastIndex)
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(onClick = { showCreateDirDialog = true }) {
                    Icon(Icons.Filled.Folder, contentDescription = stringResource(R.string.create_directory))
                }
                FloatingActionButton(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
                    Icon(Icons.Filled.Description, contentDescription = stringResource(R.string.add_file))
                }
            }
        },
    ) { innerPadding ->
        if (isLoading || currentDirectory == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            FileSystemList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                items = currentDirectory!!.items,
                onItemClick = { item ->
                    if (item is LogicalDirectory) {
                        navStack.add(File(currentDirFile, item.name))
                    }
                },
                onUploadClick = { item ->
                    scope.launch {
                        CloudManager.upload(context, item, item.logicalPath)
                        syncAndReloadDirectory()
                    }
                },
                onDeleteClick = { item ->
                    itemToDelete = item
                }
            )
        }
    }
}

/**
 * A composable that displays a list of file system items.
 *
 * @param modifier The modifier to be applied to the list.
 * @param items The list of file system items to display.
 * @param onItemClick The callback to be invoked when an item is clicked.
 * @param onUploadClick The callback to be invoked when the upload button of an item is clicked.
 * @param onDeleteClick The callback to be invoked when the delete button of an item is clicked.
 */
@Composable
fun FileSystemList(
    modifier: Modifier = Modifier,
    items: List<FileSystemItem>,
    onItemClick: (FileSystemItem) -> Unit,
    onUploadClick: (LocalFile) -> Unit,
    onDeleteClick: (FileSystemItem) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(items) { item ->
            ListItem(
                headlineContent = { Text(item.name) },
                leadingContent = {
                    when (item) {
                        is LogicalDirectory -> Icon(
                            Icons.Filled.Folder,
                            contentDescription = stringResource(R.string.directory)
                        )

                        is LocalFile -> Icon(
                            Icons.Filled.Description,
                            contentDescription = stringResource(R.string.file)
                        )
                    }
                },
                trailingContent = {
                    Row {
                        // Show upload button only for local files with a URI
                        if (item is LocalFile && item.uri != null) {
                            IconButton(onClick = { onUploadClick(item) }) {
                                Icon(Icons.Filled.Upload, contentDescription = stringResource(R.string.upload))
                            }
                        }
                        IconButton(onClick = { onDeleteClick(item) }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                },
                modifier = Modifier.clickable { onItemClick(item) }
            )
        }
    }
}

/**
 * A dialog to confirm the deletion of a file system item.
 *
 * @param item The item to be deleted.
 * @param onDismiss The callback to be invoked when the dialog is dismissed.
 * @param onConfirm The callback to be invoked when the deletion is confirmed.
 */
@Composable
private fun DeleteConfirmationDialog(
    item: FileSystemItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val itemType = if (item is LogicalDirectory) stringResource(R.string.directory) else stringResource(R.string.file)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_item_title, itemType)) },
        text = { Text(stringResource(R.string.delete_item_message, item.name)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

/**
 * A dialog to create a new directory.
 *
 * @param onDismiss The callback to be invoked when the dialog is dismissed.
 * @param onConfirm The callback to be invoked when the directory creation is confirmed.
 */
@Composable
private fun CreateDirectoryDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_new_directory)) },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.directory_name)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text)
                    }
                }
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * The login screen of the application.
 * This screen allows the user to log in.
 *
 * @param onLoginSuccess The callback to be invoked when the user successfully logs in.
 */
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.login), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            isError = error != null
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            isError = error != null
        )

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                if (AuthManager.login(context, email, password)) {
                    onLoginSuccess()
                } else {
                    error = context.getString(R.string.invalid_credentials)
                }
            }
        }) {
            Text(stringResource(R.string.login))
        }
    }
}

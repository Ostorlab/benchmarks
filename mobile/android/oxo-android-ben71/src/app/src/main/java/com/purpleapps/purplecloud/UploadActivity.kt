package com.purpleapps.purplecloud

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.purpleapps.purplecloud.cloud.CloudManager
import com.purpleapps.purplecloud.model.LocalFile
import com.purpleapps.purplecloud.persistance.DirectoryManager
import com.purpleapps.purplecloud.ui.theme.PurpleCloudTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * An activity that handles file uploads.
 * It is triggered by an intent with the action ACTION_UPLOAD.
 * It receives a file path and a logical path, prompts the user for confirmation,
 * then creates the necessary directory structure, adds the file to the local persistence,
 * and uploads it to the cloud.
 * The activity finishes itself after the upload process is initiated or cancelled.
 */
class UploadActivity : ComponentActivity() {

    companion object {
        const val ACTION_UPLOAD = "com.purpleapps.purplecloud.action.UPLOAD"
        const val EXTRA_FILE_PATH = "com.purpleapps.purplecloud.extra.FILE_PATH"
        const val EXTRA_LOGICAL_PATH = "com.purpleapps.purplecloud.extra.LOGICAL_PATH"
        private const val TAG = "UploadActivity"
    }

    /**
     * Initializes the activity, gets the file and logical paths from the intent,
     * and starts the upload process.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == ACTION_UPLOAD) {
            val filePath = intent.getStringExtra(EXTRA_FILE_PATH)
            val logicalPath = intent.getStringExtra(EXTRA_LOGICAL_PATH)

            if (filePath != null && logicalPath != null) {
                setContent {
                    PurpleCloudTheme {
                        UploadConfirmationDialog(
                            filePath = filePath,
                            logicalPath = logicalPath,
                            onConfirm = {
                                lifecycleScope.launch {
                                    try {
                                        handleUpload(filePath, logicalPath)
                                    } finally {
                                        if (!isFinishing) {
                                            finish()
                                        }
                                    }
                                }
                            },
                            onDismiss = {
                                finish()
                            }
                        )
                    }
                }
            } else {
                Log.e(TAG, "Missing file path or logical path in intent extras.")
                finish()
            }
        } else {
            finish()
        }
    }

    /**
     * Handles the file upload process.
     * It ensures the file exists, creates the directory structure if it doesn't exist,
     * adds the file to the local directory manager, and then uploads it to the cloud.
     *
     * @param filePath The absolute path to the file on the local disk.
     * @param logicalPath The logical path in the cloud where the file should be stored.
     */
    private suspend fun handleUpload(filePath: String, logicalPath: String) = withContext(Dispatchers.IO) {
        val fileOnDisk = File(filePath)
        if (!fileOnDisk.exists() || !fileOnDisk.isFile) {
            Log.e(TAG, "File does not exist or is not a file: $filePath")
            return@withContext
        }
        val fileUri = Uri.fromFile(fileOnDisk)

        val pathSegments = logicalPath.replace('\\', '/').split('/').filter { it.isNotEmpty() }
        if (pathSegments.any { it == ".." }) {
            Log.e(TAG, "Path traversal attempt detected in logical path: $logicalPath")
            return@withContext
        }
        if (pathSegments.isEmpty()) {
            Log.e(TAG, "Invalid logical path: $logicalPath")
            return@withContext
        }
        val newFileName = pathSegments.last()

        val parentDirFile = DirectoryManager.createPathAndGetParentDir(this@UploadActivity, logicalPath)
        if (parentDirFile == null) {
            Log.e(TAG, "Failed to create directory path for $logicalPath. A file may be in the way.")
            return@withContext
        }

        DirectoryManager.addFile(this@UploadActivity, parentDirFile, fileUri, newFileName)

        val fileToUpload = LocalFile(name = newFileName, uri = fileUri, logicalPath = logicalPath)
        CloudManager.upload(this@UploadActivity, fileToUpload, logicalPath)
    }
}

@Composable
private fun UploadConfirmationDialog(
    filePath: String,
    logicalPath: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val fileOnDisk = File(filePath)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Upload") },
        text = { Text("Do you want to upload '${fileOnDisk.name}' to '$logicalPath'?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Upload")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

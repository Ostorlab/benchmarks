package com.purpleapps.purplecloud

import androidx.activity.ComponentActivity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.purpleapps.purplecloud.cloud.CloudManager
import com.purpleapps.purplecloud.model.LocalFile
import com.purpleapps.purplecloud.persistance.DirectoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * An invisible activity that handles file uploads.
 * It is triggered by an intent with the action ACTION_UPLOAD.
 * It receives a file path and a logical path, creates the necessary directory structure,
 * adds the file to the local persistence, and uploads it to the cloud.
 * The activity finishes itself after the upload process is initiated.
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

        lifecycleScope.launch {
            try {
                if (intent?.action == ACTION_UPLOAD) {
                    val filePath = intent.getStringExtra(EXTRA_FILE_PATH)
                    val logicalPath = intent.getStringExtra(EXTRA_LOGICAL_PATH)

                    if (filePath != null && logicalPath != null) {
                        handleUpload(filePath, logicalPath)
                    } else {
                        Log.e(TAG, "Missing file path or logical path in intent extras.")
                    }
                }
            } finally {
                if (!isFinishing) {
                    finish()
                }
            }
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
        // Vulnerability:
        // The code attempts to block access to internal storage files by checking
        // whether the file path starts with "/data/data".
        // This check is insufficient because:
        //
        // 1. Internal storage can also be accessed through alternative but valid paths,
        //    such as "/data/user/0/<package_name>/...".
        // 2. An attacker can bypass the check by using path traversal sequences ("..")
        //    to navigate into the internal storage directory.
        //
        // For example, if the provided path is:
        //     /data/user/0/com.purpleapps.purplecloud/shared_prefs/AuthPrefs.xml
        // which stores the user's authentication token,
        // the app will still accept it and upload the file to remote storage.
        if (filePath.startsWith("/data/data")) {
            Log.e(TAG, "Attempted to upload a file from internal storage, which is not allowed: $filePath")
            return@withContext
        }
        val fileOnDisk = File(filePath)
        if (!fileOnDisk.exists() || !fileOnDisk.isFile) {
            Log.e(TAG, "File does not exist or is not a file: $filePath")
            return@withContext
        }
        val fileUri = Uri.fromFile(fileOnDisk)

        val pathSegments = logicalPath.replace('\\', '/').split('/').filter { it.isNotEmpty() }
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

package com.purpleapps.purplecloud

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
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
        private const val NOTIFICATION_CHANNEL_ID = "upload_channel"
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

                    if (logicalPath != null) {
                        // VULNERABILITY: The notification text is taken directly from the intent extra.
                        // A malicious app can provide arbitrary text to display in a trusted notification.
                        showUploadNotification(logicalPath)
                    }

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
        val fileOnDisk = File(filePath)
        if (!fileOnDisk.exists() || !fileOnDisk.isFile) {
            Log.e(TAG, "File does not exist or is not a file: $filePath")
            return@withContext
        }

        // Sanitize the file path to prevent path traversal attacks.
        val userRootDir = DirectoryManager.getRoot(this@UploadActivity)
        if (!fileOnDisk.canonicalPath.startsWith(userRootDir.canonicalPath)) {
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

    /**
     * Creates a notification channel (for Android 8.0+) and displays a notification.
     * The content of the notification is taken directly from the provided text, which is the source
     * of the Notification Injection vulnerability.
     *
     * @param notificationText The text to display in the notification.
     */
    private fun showUploadNotification(notificationText: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val name = "Uploads"
        val descriptionText = "Notifications for file uploads"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("File Upload")
            .setContentText(notificationText) // The vulnerable part
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }
}

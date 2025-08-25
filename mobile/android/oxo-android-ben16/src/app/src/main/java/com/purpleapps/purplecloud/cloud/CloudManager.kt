package com.purpleapps.purplecloud.cloud

import android.content.Context
import android.util.Log
import com.purpleapps.purplecloud.auth.AuthManager
import com.purpleapps.purplecloud.model.FileSystemItem
import com.purpleapps.purplecloud.model.LocalFile
import com.purpleapps.purplecloud.model.LogicalDirectory
import com.purpleapps.purplecloud.persistance.DirectoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Manages interactions with the cloud storage service.
 * This includes listing directory contents and deleting items.
 */
object CloudManager {
    private const val BASE_URL = "http://10.0.2.2:8000/items"

    private const val UPLOAD_URL = "http://10.0.2.2:8000/upload"
    private const val TAG = "CloudManager"

    /**
     * Lists the contents of a directory in the cloud.
     *
     * @param context The application context.
     * @param path The logical path of the directory to list.
     * @return A list of [FileSystemItem] representing the contents of the directory.
     */
    suspend fun listDirectory(context: Context, path: String): List<FileSystemItem> = withContext(Dispatchers.IO) {
        val token = AuthManager.getAccessToken(context)
        if (token == null) {
            Log.e(TAG, "Cannot list directory, user is not logged in or token is missing.")
            return@withContext emptyList()
        }

        try {
            val fullUrl = if (path.isEmpty()) BASE_URL else "$BASE_URL/$path/"
            val url = URL(fullUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer $token")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)
                val items = mutableListOf<FileSystemItem>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val itemPath = jsonObject.getString("path")
                    val type = jsonObject.getString("type")
                    if (type == "directory") {
                        items.add(LogicalDirectory(name = name, logicalPath = itemPath))
                    } else if (type == "file") {
                        items.add(LocalFile(name = name, uri = null, logicalPath = itemPath))
                    }
                }
                items
            } else {
                Log.e(TAG, "Failed to list directory '$path'. Response: ${connection.responseCode} ${connection.responseMessage}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error listing directory '$path'", e)
            emptyList()
        }
    }

    /**
     * Deletes an item (file or directory) from the cloud.
     *
     * @param context The application context.
     * @param item The [FileSystemItem] to delete.
     * @return `true` if the deletion was successful, `false` otherwise.
     */
    suspend fun deleteItem(context: Context, item: FileSystemItem): Boolean = withContext(Dispatchers.IO) {
        val token = AuthManager.getAccessToken(context)
        if (token == null) {
            Log.e(TAG, "Cannot delete item, user is not logged in or token is missing.")
            return@withContext false
        }

        val path = when (item) {
            is LocalFile -> item.logicalPath
            is LogicalDirectory -> item.logicalPath
        }

        try {
            val fullUrl = if (item is LogicalDirectory) "$BASE_URL/$path/" else "$BASE_URL/$path"
            val url = URL(fullUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "DELETE"
            connection.setRequestProperty("Authorization", "Bearer $token")

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "Successfully deleted item at '$path'")
                true
            } else {
                Log.e(TAG, "Failed to delete item at '$path'. Response: $responseCode ${connection.responseMessage}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting item at '$path'", e)
            false
        }
    }

    /**
     * Uploads a file or directory to the cloud.
     *
     * @param context The application context.
     * @param item The file system item to upload.
     * @param logicalPath The logical path in the cloud where the item should be stored.
     */
    suspend fun upload(context: Context, item: FileSystemItem, logicalPath: String) {
        withContext(Dispatchers.IO) {
            when (item) {
                is LocalFile -> uploadFile(context, item, logicalPath)
                is LogicalDirectory -> uploadDirectory(context, item, logicalPath)
            }
        }
    }

    /**
     * Recursively uploads the contents of a directory.
     * If the directory is empty, it does nothing.
     *
     * @param context The application context.
     * @param directory The directory to upload.
     * @param logicalPath The base logical path for the directory's contents.
     */
    private suspend fun uploadDirectory(context: Context, directory: LogicalDirectory, logicalPath: String) {
        val directoryFile = File(DirectoryManager.getRoot(context), directory.logicalPath)
        val loadedDirectory = DirectoryManager.loadDirectory(directoryFile, directory.logicalPath)
        if (loadedDirectory.items.isEmpty()) {
            Log.d(TAG, "Directory '$logicalPath' is empty, nothing to upload.")
        } else {
            loadedDirectory.items.forEach { childItem ->
                val childLogicalPath = "$logicalPath/${childItem.name}"
                upload(context, childItem, childLogicalPath)
            }
        }
    }

    /**
     * Uploads a single file to the cloud.
     *
     * @param context The application context.
     * @param file The file to upload.
     * @param logicalPath The full logical path for the file in the cloud.
     */
    private fun uploadFile(context: Context, file: LocalFile, logicalPath: String) {
        val fileUri = file.uri ?: run {
            Log.d(TAG, "Skipping upload for file without a local URI: ${file.name}")
            return
        }
        try {
            val token = AuthManager.getAccessToken(context)
            if (token == null) {
                Log.e(TAG, "Cannot upload file, user is not logged in or token is missing.")
                return
            }

            val boundary = "Boundary-${System.currentTimeMillis()}"
            val url = URL(UPLOAD_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            connection.setRequestProperty("Authorization", "Bearer $token")

            connection.outputStream.use { outputStream ->
                // Write logical path part
                outputStream.write("--$boundary\r\n".toByteArray())
                outputStream.write("Content-Disposition: form-data; name=\"logicalPath\"\r\n\r\n".toByteArray())
                outputStream.write(logicalPath.toByteArray())
                outputStream.write("\r\n".toByteArray())

                // Write file part
                context.contentResolver.openInputStream(fileUri)?.use { fileInputStream ->
                    outputStream.write("--$boundary\r\n".toByteArray())
                    outputStream.write("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"\r\n".toByteArray())
                    outputStream.write("Content-Type: application/octet-stream\r\n\r\n".toByteArray())
                    fileInputStream.copyTo(outputStream)
                    outputStream.write("\r\n".toByteArray())
                }

                // End of multipart
                outputStream.write("--$boundary--\r\n".toByteArray())
            }

            val responseCode = connection.responseCode
            Log.i(TAG, "Upload response for '$logicalPath': $responseCode ${connection.responseMessage}")
            connection.disconnect()
        } catch (e: Exception) {
            Log.e(TAG, "Upload failed for '$logicalPath'", e)
        }
    }
}

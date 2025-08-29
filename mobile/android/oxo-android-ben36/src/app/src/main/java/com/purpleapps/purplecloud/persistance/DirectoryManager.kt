package com.purpleapps.purplecloud.persistance

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.purpleapps.purplecloud.auth.AuthManager
import com.purpleapps.purplecloud.model.FileSystemItem
import com.purpleapps.purplecloud.model.LocalFile
import com.purpleapps.purplecloud.model.LogicalDirectory
import java.io.File
import androidx.core.net.toUri

/**
 * Manages the local file system representation of the cloud storage.
 * This object handles the creation, deletion, and loading of local files and directories
 * that mirror the structure of the user's cloud storage.
 */
object DirectoryManager {
    private const val ITEMS_FILENAME = ".items"
    private const val DIR_PREFIX = "DIR:"
    private const val FILE_PREFIX = "FILE:"

    /**
     * Gets the root directory for a specific user.
     * If the directory doesn't exist, it will be created.
     * @param context The application context.
     * @param userEmail The email of the user.
     * @return The user's root directory file.
     */
    fun getRoot(context: Context, userEmail: String): File {
        val rootDir = context.filesDir
        val userCloudRootDir = File(rootDir, "cloud_root/$userEmail")
        if (!userCloudRootDir.exists()) {
            userCloudRootDir.mkdirs()
        }
        return userCloudRootDir
    }

    /**
     * Gets the root directory for the currently logged-in user.
     * @param context The application context.
     * @return The current user's root directory file.
     * @throws IllegalStateException if no user is logged in.
     */
    fun getRoot(context: Context): File {
        val userEmail = AuthManager.getCurrentUser(context)
            ?: throw IllegalStateException("User not logged in, cannot determine root directory.")
        return getRoot(context, userEmail)
    }

    /**
     * Loads the contents of a directory from the local file system representation.
     * It reads the .items file in the given directory to discover subdirectories and files.
     * @param parentDirFile The file object representing the directory to load.
     * @param logicalPath The logical path of the directory.
     * @return A [LogicalDirectory] object representing the contents of the directory.
     */
    fun loadDirectory(parentDirFile: File, logicalPath: String): LogicalDirectory {
        val items = mutableListOf<FileSystemItem>()
        val itemsFile = File(parentDirFile, ITEMS_FILENAME)

        if (itemsFile.exists()) {
            itemsFile.readLines().forEach { line ->
                if (line.isBlank()) return@forEach

                if (line.startsWith(DIR_PREFIX)) {
                    val dirName = line.substringAfter(DIR_PREFIX)
                    val childLogicalPath = if (logicalPath.isEmpty()) dirName else "$logicalPath/$dirName"
                    items.add(LogicalDirectory(name = dirName, logicalPath = childLogicalPath))
                } else if (line.startsWith(FILE_PREFIX)) {
                    val content = line.substringAfter(FILE_PREFIX)
                    val parts = content.split(":", limit = 2)
                    val fileName = parts[0]
                    val fileUriString = parts.getOrNull(1)
                    val childLogicalPath = if (logicalPath.isEmpty()) fileName else "$logicalPath/$fileName"
                    items.add(
                        LocalFile(
                            name = fileName,
                            uri = fileUriString?.toUri(),
                            logicalPath = childLogicalPath
                        )
                    )
                }
            }
        }
        return LogicalDirectory(
            name = parentDirFile.name,
            items = items.sortedBy { it.name },
            logicalPath = logicalPath
        )
    }

    /**
     * Adds a file to the directory representation using its content URI.
     * The file name is extracted from the URI.
     * @param context The application context.
     * @param parentDirFile The directory where the file entry should be added.
     * @param fileUri The content URI of the file to add.
     */
    fun addFile(context: Context, parentDirFile: File, fileUri: Uri) {
        val fileName = getFileName(context, fileUri) ?: return
        addFile(context, parentDirFile, fileUri, fileName)
    }

    /**
     * Adds a file to the directory representation with a specific file name.
     *
     * VULNERABILITY: This function performs a physical file copy. When combined with the
     * path traversal vulnerability in `createPathAndGetParentDir`, this allows an attacker
     * to overwrite arbitrary files within the app's data directory.
     *
     * @param context The application context.
     * @param parentDirFile The directory where the file entry should be added.
     * @param fileUri The URI of the file to add.
     * @param fileName The name to be used for the file.
     */
    fun addFile(context: Context, parentDirFile: File, fileUri: Uri, fileName: String) {
        val destinationFile = File(parentDirFile, fileName)

        try {
            // Copy the content from the source URI to the destination file.
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                destinationFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // After copying, update the .items file to reference the new local copy.
            val itemsFile = File(parentDirFile, ITEMS_FILENAME)
            val newLine = "$FILE_PREFIX$fileName:${destinationFile.toUri()}"
            if (!itemsFile.exists() || !itemsFile.readLines().any { it.startsWith("$FILE_PREFIX$fileName:") }) {
                itemsFile.appendText("$newLine\n")
            }
        } catch (e: Exception) {
            // Silently fail on error
        }
    }

    /**
     * Creates a new directory within a parent directory.
     * This creates a physical directory on disk and adds an entry to the parent's .items file.
     * @param parentDirFile The parent directory.
     * @param dirName The name of the new directory.
     */
    fun createDirectory(parentDirFile: File, dirName: String) {
        val newDir = File(parentDirFile, dirName)
        if (!newDir.exists()) {
            newDir.mkdirs()
        }
        val itemsFile = File(parentDirFile, ITEMS_FILENAME)
        val newLine = "$DIR_PREFIX$dirName"
        if (itemsFile.exists() && itemsFile.readLines().any { it == newLine }) {
            return
        }
        itemsFile.appendText("$newLine\n")
    }

    /**
     * Ensures a given logical path exists by creating any missing directories, and returns the parent directory file.
     * For a path "a/b/c.txt", it ensures "a" and "a/b" exist and returns the File object for "a/b".
     *
     * VULNERABILITY: This implementation is vulnerable to path traversal. It directly uses the
     * `logicalPath` to construct a file path without sanitizing it. An attacker can provide a
     * path with `../` sequences to write files outside of the intended directory.
     *
     * @param context The application context.
     * @param logicalPath The full logical path of a file or directory.
     * @return The [File] object for the parent directory of the given logical path.
     */
    fun createPathAndGetParentDir(context: Context, logicalPath: String): File? {
        val rootDir = getRoot(context)
        // Insecurely join the root path with the user-provided logical path.
        // This allows for path traversal attacks (e.g., using "../").
        val fullPath = File(rootDir, logicalPath)

        val parentDir = fullPath.parentFile
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }
        return parentDir
    }

    /**
     * Deletes a file or directory from the local representation.
     * This removes the entry from the .items file and deletes the actual file/directory from disk.
     * @param parentDirFile The parent directory of the item to delete.
     * @param itemToDelete The [FileSystemItem] to be deleted.
     */
    fun deleteItem(parentDirFile: File, itemToDelete: FileSystemItem) {
        val itemsFile = File(parentDirFile, ITEMS_FILENAME)
        if (!itemsFile.exists()) return

        val lines = itemsFile.readLines()
        val updatedLines = lines.filterNot { line ->
            when (itemToDelete) {
                is LogicalDirectory -> line == "$DIR_PREFIX${itemToDelete.name}"
                is LocalFile -> line.startsWith("$FILE_PREFIX${itemToDelete.name}:")
            }
        }

        if (lines.size != updatedLines.size) {
            val newContent = updatedLines.joinToString("\n")
            itemsFile.writeText(if (newContent.isEmpty()) "" else newContent + "\n")
        }

        if (itemToDelete is LogicalDirectory) {
            val dirToDelete = File(parentDirFile, itemToDelete.name)
            if (dirToDelete.exists()) {
                dirToDelete.deleteRecursively()
            }
        }
    }

    /**
     * Retrieves the display name of a file from its content URI.
     * @param context The application context.
     * @param uri The content URI of the file.
     * @return The display name of the file, or null if it cannot be determined.
     */
    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}

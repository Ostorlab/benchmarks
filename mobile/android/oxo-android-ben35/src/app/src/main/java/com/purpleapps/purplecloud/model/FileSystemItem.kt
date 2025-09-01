package com.purpleapps.purplecloud.model

import android.net.Uri

/**
 * Represents an item in the application file tree, which can be either a file or a directory.
 */
sealed interface FileSystemItem {
    /** The name of the file or directory. */
    val name: String
}

/**
 * Represents a file in the application file tree.
 * @property name The name of the file.
 * @property uri The content URI of the file, which may be null if it's not available locally.
 * @property logicalPath The full path of the file within the cloud storage hierarchy.
 */
data class LocalFile(
    override val name: String,
    val uri: Uri?,
    val logicalPath: String
) : FileSystemItem

/**
 * Represents a directory in the logical application file tree.
 * @property name The name of the directory.
 * @property items A list of [FileSystemItem]s contained within this directory.
 * @property logicalPath The full path of the directory within the cloud storage hierarchy.
 */
data class LogicalDirectory(
    override val name: String,
    var items: List<FileSystemItem> = emptyList(),
    val logicalPath: String
) : FileSystemItem

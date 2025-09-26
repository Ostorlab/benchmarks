package co.ostorlab.myblocnote

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.text.TextUtils

class FileContentProvider : ContentProvider() {

    private lateinit var dbHelper: FileDatabaseHelper
    private lateinit var uriMatcher: UriMatcher

    companion object {
        private const val AUTHORITY = "co.ostorlab.myblocnote.provider"
        private const val SINGLE_FILE = 1
        private const val DIRECTORY = 2
        private const val ROOT_DIRECTORY = 3
        private const val SHARES = 4
        private const val CAPABILITIES = 5
        private const val UPLOADS = 6
        private const val CAMERA_UPLOADS_SYNC = 7
        private const val QUOTAS = 8
    }

    override fun onCreate(): Boolean {
        dbHelper = FileDatabaseHelper(context!!)
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "file/*", SINGLE_FILE)
            addURI(AUTHORITY, "directory/#", DIRECTORY)
            addURI(AUTHORITY, "root", ROOT_DIRECTORY)
            addURI(AUTHORITY, "shares/#", SHARES)
            addURI(AUTHORITY, "capabilities", CAPABILITIES)
            addURI(AUTHORITY, "uploads", UPLOADS)
            addURI(AUTHORITY, "camera_uploads_sync", CAMERA_UPLOADS_SYNC)
            addURI(AUTHORITY, "quotas", QUOTAS)
        }
        return true
    }

    override fun delete(uri: Uri, where: String?, whereArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        var count = 0

        when (uriMatcher.match(uri)) {
            SINGLE_FILE -> {
                count = db.delete(
                    FileTableMeta.FILE_TABLE_NAME,
                    FileTableMeta._ID + "=" + uri.pathSegments[1] +
                            if (!TextUtils.isEmpty(where)) " AND ($where)" else "",
                    whereArgs
                )
            }
            DIRECTORY -> {
                count += db.delete(
                    FileTableMeta.FILE_TABLE_NAME,
                    FileTableMeta._ID + "=" + uri.pathSegments[1] +
                            if (!TextUtils.isEmpty(where)) " AND ($where)" else "",
                    whereArgs
                )
            }
            ROOT_DIRECTORY -> {
                count = db.delete(FileTableMeta.FILE_TABLE_NAME, where, whereArgs)
            }
            SHARES -> {
                // Implementation for shares deletion
            }
            CAPABILITIES -> {
                count = db.delete(FileTableMeta.CAPABILITIES_TABLE_NAME, where, whereArgs)
            }
            UPLOADS -> {
                count = db.delete(FileTableMeta.UPLOADS_TABLE_NAME, where, whereArgs)
            }
            CAMERA_UPLOADS_SYNC -> {
                count = db.delete(FileTableMeta.CAMERA_UPLOADS_SYNC_TABLE_NAME, where, whereArgs)
            }
            QUOTAS -> {
                count = db.delete(FileTableMeta.USER_QUOTAS_TABLE_NAME, where, whereArgs)
            }
        }

        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor?

        when (uriMatcher.match(uri)) {
            SINGLE_FILE -> {
                cursor = db.query(
                    FileTableMeta.FILE_TABLE_NAME,
                    projection,
                    FileTableMeta._ID + " = ?",
                    arrayOf(uri.lastPathSegment),
                    null,
                    null,
                    sortOrder
                )
            }
            DIRECTORY -> {
                cursor = db.query(
                    FileTableMeta.FILE_TABLE_NAME,
                    projection,
                    FileTableMeta._ID + " = ?",
                    arrayOf(uri.lastPathSegment),
                    null,
                    null,
                    sortOrder
                )
            }
            ROOT_DIRECTORY -> {
                cursor = db.query(
                    FileTableMeta.FILE_TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        // Implementation for getType
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val tableName = when (uriMatcher.match(uri)) {
            SINGLE_FILE, DIRECTORY, ROOT_DIRECTORY -> FileTableMeta.FILE_TABLE_NAME
            CAPABILITIES -> FileTableMeta.CAPABILITIES_TABLE_NAME
            UPLOADS -> FileTableMeta.UPLOADS_TABLE_NAME
            CAMERA_UPLOADS_SYNC -> FileTableMeta.CAMERA_UPLOADS_SYNC_TABLE_NAME
            QUOTAS -> FileTableMeta.USER_QUOTAS_TABLE_NAME
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val id = db.insert(tableName, null, values)
        if (id > 0) {
            val newUri = ContentUris.withAppendedId(uri, id)
            context?.contentResolver?.notifyChange(newUri, null)
            return newUri
        }
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        // Implementation for update
        return 0
    }
}

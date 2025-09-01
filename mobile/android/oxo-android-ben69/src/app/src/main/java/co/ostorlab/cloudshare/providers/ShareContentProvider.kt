package co.ostorlab.cloudshare.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import co.ostorlab.cloudshare.database.DatabaseHelper

/**
 * ShareContentProvider provides access to shared file data.
 * Used internally by CloudShare Pro for managing file shares and synchronization.
 */
class ShareContentProvider : ContentProvider() {
    
    companion object {
        const val AUTHORITY = "co.ostorlab.cloudshare.shares"
        const val PATH_SHARES = "shares"
        const val PATH_SHARE_ID = "shares/#"
        const val PATH_SYNC = "sync"
        
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")
        
        // URI matcher codes
        const val SHARES = 100
        const val SHARE_ID = 101
        const val SYNC = 102
        
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH_SHARES, SHARES)
            addURI(AUTHORITY, PATH_SHARE_ID, SHARE_ID)
            addURI(AUTHORITY, PATH_SYNC, SYNC)
        }
    }
    
    private lateinit var dbHelper: DatabaseHelper
    
    override fun onCreate(): Boolean {
        dbHelper = DatabaseHelper.getInstance(context!!)
        return true
    }
    
    override fun query(
        uri: Uri, 
        projection: Array<String>?, 
        selection: String?,
        selectionArgs: Array<String>?, 
        sortOrder: String?
    ): Cursor? {
        
        Log.d("ShareProvider", "Query URI: $uri")
        
        return when (uriMatcher.match(uri)) {
            SHARES -> {
                // Return all shares with authentication data
                // VULNERABLE: No permission check - returns sensitive bcrypt hashes and tokens
                val cursor = MatrixCursor(arrayOf(
                    DatabaseHelper.COLUMN_ID,
                    DatabaseHelper.COLUMN_TOKEN,
                    DatabaseHelper.COLUMN_SHARE_WITH,  // bcrypt password hash exposed!
                    DatabaseHelper.COLUMN_FILE_NAME,
                    DatabaseHelper.COLUMN_FILE_SIZE,
                    DatabaseHelper.COLUMN_USER_ID,
                    DatabaseHelper.COLUMN_EXPIRATION,
                    DatabaseHelper.COLUMN_SHARE_URL,
                    DatabaseHelper.COLUMN_CREATED,
                    DatabaseHelper.COLUMN_DOWNLOAD_COUNT,
                    DatabaseHelper.COLUMN_IS_ACTIVE
                ))
                
                val dbCursor = dbHelper.getAllShares()
                dbCursor.use {
                    while (it.moveToNext()) {
                        cursor.addRow(arrayOf(
                            it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOKEN)),
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARE_WITH)), // BCRYPT HASH LEAKED
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FILE_NAME)),
                            it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FILE_SIZE)),
                            it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                            it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPIRATION)),
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARE_URL)),
                            it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED)),
                            it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOWNLOAD_COUNT)),
                            it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ACTIVE))
                        ))
                    }
                }
                cursor
            }
            SHARE_ID -> {
                // Return specific share by ID
                val shareId = uri.lastPathSegment
                val cursor = MatrixCursor(arrayOf(
                    DatabaseHelper.COLUMN_ID,
                    DatabaseHelper.COLUMN_TOKEN,
                    DatabaseHelper.COLUMN_SHARE_WITH,
                    DatabaseHelper.COLUMN_FILE_NAME,
                    DatabaseHelper.COLUMN_USER_ID,
                    DatabaseHelper.COLUMN_EXPIRATION
                ))
                
                val dbCursor = dbHelper.readableDatabase.query(
                    DatabaseHelper.TABLE_SHARES,
                    null,
                    "${DatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(shareId),
                    null, null, null
                )
                
                dbCursor.use {
                    if (it.moveToFirst()) {
                        cursor.addRow(arrayOf(
                            it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOKEN)),
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARE_WITH)), // VULNERABLE
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FILE_NAME)),
                            it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                            it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPIRATION))
                        ))
                    }
                }
                cursor
            }
            SYNC -> {
                // Sync endpoint - returns minimal authentication data for "sync purposes"
                val cursor = MatrixCursor(arrayOf(
                    DatabaseHelper.COLUMN_TOKEN,
                    DatabaseHelper.COLUMN_SHARE_WITH,
                    DatabaseHelper.COLUMN_USER_ID
                ))
                
                val dbCursor = dbHelper.getAllShares()
                dbCursor.use {
                    while (it.moveToNext()) {
                        cursor.addRow(arrayOf(
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOKEN)),
                            it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARE_WITH)), // STILL VULNERABLE
                            it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
                        ))
                    }
                }
                cursor
            }
            else -> null
        }
    }
    
    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            SHARES -> "vnd.android.cursor.dir/vnd.cloudshare.shares"
            SHARE_ID -> "vnd.android.cursor.item/vnd.cloudshare.share"
            SYNC -> "vnd.android.cursor.dir/vnd.cloudshare.sync"
            else -> null
        }
    }
    
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Not implemented for this vulnerability scenario
        return null
    }
    
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // Not implemented for this vulnerability scenario
        return 0
    }
    
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        // Not implemented for this vulnerability scenario
        return 0
    }
}

package co.ostorlab.cloudshare.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import co.ostorlab.cloudshare.models.ShareData
import co.ostorlab.cloudshare.models.FileData

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        const val DATABASE_NAME = "cloudshare.db"
        const val DATABASE_VERSION = 1
        
        // Tables
        const val TABLE_SHARES = "shares"
        const val TABLE_FILES = "files"
        
        // Shares table columns
        const val COLUMN_ID = "_id"
        const val COLUMN_TOKEN = "token"
        const val COLUMN_SHARE_WITH = "share_with"
        const val COLUMN_FILE_NAME = "file_name"
        const val COLUMN_FILE_SIZE = "file_size"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_EXPIRATION = "expiration"
        const val COLUMN_SHARE_URL = "share_url"
        const val COLUMN_CREATED = "created"
        const val COLUMN_DOWNLOAD_COUNT = "download_count"
        const val COLUMN_IS_ACTIVE = "is_active"
        
        // Files table columns
        const val COLUMN_FILE_PATH = "file_path"
        const val COLUMN_MIME_TYPE = "mime_type"
        const val COLUMN_UPLOAD_DATE = "upload_date"
        const val COLUMN_IS_SHARED = "is_shared"
        const val COLUMN_SHARE_TOKEN = "share_token"
        
        @Volatile
        private var INSTANCE: DatabaseHelper? = null
        
        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        val createSharesTable = """
            CREATE TABLE $TABLE_SHARES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TOKEN TEXT NOT NULL,
                $COLUMN_SHARE_WITH TEXT NOT NULL,
                $COLUMN_FILE_NAME TEXT NOT NULL,
                $COLUMN_FILE_SIZE INTEGER NOT NULL,
                $COLUMN_USER_ID INTEGER NOT NULL,
                $COLUMN_EXPIRATION INTEGER NOT NULL,
                $COLUMN_SHARE_URL TEXT NOT NULL,
                $COLUMN_CREATED INTEGER NOT NULL,
                $COLUMN_DOWNLOAD_COUNT INTEGER DEFAULT 0,
                $COLUMN_IS_ACTIVE INTEGER DEFAULT 1
            )
        """.trimIndent()
        
        val createFilesTable = """
            CREATE TABLE $TABLE_FILES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FILE_NAME TEXT NOT NULL,
                $COLUMN_FILE_PATH TEXT NOT NULL,
                $COLUMN_FILE_SIZE INTEGER NOT NULL,
                $COLUMN_MIME_TYPE TEXT NOT NULL,
                $COLUMN_USER_ID INTEGER NOT NULL,
                $COLUMN_UPLOAD_DATE INTEGER NOT NULL,
                $COLUMN_IS_SHARED INTEGER DEFAULT 0,
                $COLUMN_SHARE_TOKEN TEXT
            )
        """.trimIndent()
        
        db.execSQL(createSharesTable)
        db.execSQL(createFilesTable)
        
        // Insert some realistic sample data
        insertSampleData(db)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SHARES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FILES")
        onCreate(db)
    }
    
    private fun insertSampleData(db: SQLiteDatabase) {
        // Sample shares with realistic bcrypt hashes
        val shares = listOf(
            ContentValues().apply {
                put(COLUMN_TOKEN, "a7f8b92c-4e3d-4a9f-b1c2-8e5f6d4a9b7c")
                put(COLUMN_SHARE_WITH, "\$2a\$12\$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqfq7wdf06.DUOdJJNWZZ9S") // "password123"
                put(COLUMN_FILE_NAME, "Project_Proposal_2024.pdf")
                put(COLUMN_FILE_SIZE, 2847392L)
                put(COLUMN_USER_ID, 1001)
                put(COLUMN_EXPIRATION, System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L))
                put(COLUMN_SHARE_URL, "https://cloudshare.pro/s/a7f8b92c")
                put(COLUMN_CREATED, System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L))
                put(COLUMN_DOWNLOAD_COUNT, 0)
                put(COLUMN_IS_ACTIVE, 1)
            },
            ContentValues().apply {
                put(COLUMN_TOKEN, "b3e9c4d7-2f5a-4b8e-9c1d-7a6b4e8f2c5d")
                put(COLUMN_SHARE_WITH, "\$2a\$12\$N8GqTlQmf1jAT.u5WvJqWeFJJqJf4N2CmYLAoZK1zE1vQ2WcJqD6a") // "admin"
                put(COLUMN_FILE_NAME, "Financial_Report_Q3.xlsx")
                put(COLUMN_FILE_SIZE, 1943823L)
                put(COLUMN_USER_ID, 1002)
                put(COLUMN_EXPIRATION, System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L))
                put(COLUMN_SHARE_URL, "https://cloudshare.pro/s/b3e9c4d7")
                put(COLUMN_CREATED, System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L))
                put(COLUMN_DOWNLOAD_COUNT, 3)
                put(COLUMN_IS_ACTIVE, 1)
            },
            ContentValues().apply {
                put(COLUMN_TOKEN, "f2d8a5b9-7c4e-4d1f-8a6b-3e9c7f1d4a8b")
                put(COLUMN_SHARE_WITH, "\$2a\$12\$fW4lEKHWwqYqQjmL4l8C7ONkHGJVL8f8YBbO1BgALKFPJ8DfXNZNS") // "123456"
                put(COLUMN_FILE_NAME, "Team_Meeting_Recording.mp4")
                put(COLUMN_FILE_SIZE, 89472651L)
                put(COLUMN_USER_ID, 1003)
                put(COLUMN_EXPIRATION, System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L))
                put(COLUMN_SHARE_URL, "https://cloudshare.pro/s/f2d8a5b9")
                put(COLUMN_CREATED, System.currentTimeMillis() - (4 * 60 * 60 * 1000L))
                put(COLUMN_DOWNLOAD_COUNT, 0)
                put(COLUMN_IS_ACTIVE, 1)
            }
        )
        
        shares.forEach { values ->
            db.insert(TABLE_SHARES, null, values)
        }
    }
    
    fun getAllShares(): Cursor {
        return readableDatabase.query(
            TABLE_SHARES,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CREATED DESC"
        )
    }
    
    fun insertShare(shareData: ShareData): Long {
        val values = ContentValues().apply {
            put(COLUMN_TOKEN, shareData.token)
            put(COLUMN_SHARE_WITH, shareData.shareWith)
            put(COLUMN_FILE_NAME, shareData.fileName)
            put(COLUMN_FILE_SIZE, shareData.fileSize)
            put(COLUMN_USER_ID, shareData.userId)
            put(COLUMN_EXPIRATION, shareData.expiration)
            put(COLUMN_SHARE_URL, shareData.shareUrl)
            put(COLUMN_CREATED, shareData.created)
            put(COLUMN_DOWNLOAD_COUNT, shareData.downloadCount)
            put(COLUMN_IS_ACTIVE, if (shareData.isActive) 1 else 0)
        }
        return writableDatabase.insert(TABLE_SHARES, null, values)
    }
}

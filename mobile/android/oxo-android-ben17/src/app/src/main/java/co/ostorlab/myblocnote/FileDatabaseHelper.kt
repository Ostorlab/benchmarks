package co.ostorlab.myblocnote

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FileDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, 
    "files.db", 
    null, 
    1
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(FileTableMeta.CREATE_TABLE_FILES)
        db.execSQL(FileTableMeta.CREATE_TABLE_CAPABILITIES)
        db.execSQL(FileTableMeta.CREATE_TABLE_UPLOADS)
        db.execSQL(FileTableMeta.CREATE_TABLE_CAMERA_UPLOADS_SYNC)
        db.execSQL(FileTableMeta.CREATE_TABLE_USER_QUOTAS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${FileTableMeta.FILE_TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${FileTableMeta.CAPABILITIES_TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${FileTableMeta.UPLOADS_TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${FileTableMeta.CAMERA_UPLOADS_SYNC_TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${FileTableMeta.USER_QUOTAS_TABLE_NAME}")
        onCreate(db)
    }
}

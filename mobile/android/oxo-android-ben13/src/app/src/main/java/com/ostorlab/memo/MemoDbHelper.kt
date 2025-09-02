package com.ostorlab.memo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class MemoDbHelper(context: Context) : SQLiteOpenHelper(context, "memo_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE memos (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS memos")
        onCreate(db)
    }

    fun insertMemo(title: String, content: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("title", title)
        values.put("content", content)
        db.insert("memos", null, values)
    }

    fun getAllMemos(): List<Memo> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM memos", null)
        val memos = mutableListOf<Memo>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val title = cursor.getString(1)
            val content = cursor.getString(2)
            memos.add(Memo(id, title, content))
        }
        cursor.close()
        return memos
    }

    fun getMemo(id: Int): Memo? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM memos WHERE id = ?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            val memo = Memo(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
            cursor.close()
            memo
        } else {
            cursor.close()
            null
        }
    }

    fun updateMemo(id: Int, title: String, content: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("title", title)
        values.put("content", content)
        db.update("memos", values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteMemo(id: Int) {
        val db = writableDatabase
        db.delete("memos", "id = ?", arrayOf(id.toString()))
    }
}

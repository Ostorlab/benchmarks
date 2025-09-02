package com.ostorlab.memo

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MemoAidlService : Service() {

    private lateinit var dbHelper: MemoDbHelper

    private val binder = object : IMemoService.Stub() {
        override fun getMemos(): MutableList<Memo> {
            return dbHelper.getAllMemos().toMutableList()
        }

        override fun deleteMemo(id: Int) {
            dbHelper.deleteMemo(id)
        }

        override fun createMemo(title: String, content: String) {
            dbHelper.insertMemo(title, content)
        }

        override fun updateMemo(id: Int, title: String, content: String) {
            dbHelper.updateMemo(id, title, content)
        }
    }

    override fun onCreate() {
        super.onCreate()
        dbHelper = MemoDbHelper(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}

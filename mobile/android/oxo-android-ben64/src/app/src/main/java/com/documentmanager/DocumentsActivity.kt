package com.documentmanager

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DocumentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)
        
        loadDocuments()
    }
    
    private fun loadDocuments() {
        try {
            val uri = Uri.parse("content://com.documentmanager.secure/documents")
            val cursor = contentResolver.query(uri, null, null, null, null)
            
            if (cursor != null) {
                val adapter = SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    arrayOf("filename", "created"),
                    intArrayOf(android.R.id.text1, android.R.id.text2),
                    0
                )
                
                findViewById<ListView>(R.id.documents_list).adapter = adapter
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading documents", Toast.LENGTH_SHORT).show()
        }
    }
}

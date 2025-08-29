package co.ostorlab.cloudshare

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.ostorlab.cloudshare.database.DatabaseHelper

class FilesActivity : AppCompatActivity() {
    
    private lateinit var filesList: ListView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Files"
        
        setupUI()
        loadFiles()
    }
    
    private fun setupUI() {
        filesList = findViewById(R.id.lv_files)
        findViewById<TextView>(R.id.tv_files_count).text = "12 files"
        findViewById<TextView>(R.id.tv_total_size).text = "94.2 MB"
    }
    
    private fun loadFiles() {
        try {
            // Query the content provider for shares (this demonstrates how the app uses its own provider)
            val uri = Uri.parse("content://co.ostorlab.cloudshare.shares/shares")
            val cursor = contentResolver.query(uri, null, null, null, null)
            
            cursor?.let {
                val adapter = SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    it,
                    arrayOf(DatabaseHelper.COLUMN_FILE_NAME, DatabaseHelper.COLUMN_FILE_SIZE),
                    intArrayOf(android.R.id.text1, android.R.id.text2),
                    0
                )
                filesList.adapter = adapter
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading files", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

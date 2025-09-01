package com.documentmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.documentmanager.data.DocumentDataManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize document data on app startup
        DocumentDataManager.initializeDocuments(this)
        
        setupUI()
    }
    
    private fun setupUI() {
        findViewById<Button>(R.id.btn_my_documents).setOnClickListener {
            startActivity(Intent(this, DocumentsActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_share_documents).setOnClickListener {
            startActivity(Intent(this, ShareActivity::class.java))
        }
        
        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}

package com.documentmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        
        setupUI()
    }
    
    private fun setupUI() {
        val shareButton = findViewById<Button>(R.id.btn_share_document)
        val documentName = findViewById<EditText>(R.id.et_document_name)
        val recipientEmail = findViewById<EditText>(R.id.et_recipient_email)
        
        shareButton.setOnClickListener {
            val docName = documentName.text.toString()
            val email = recipientEmail.text.toString()
            
            if (docName.isNotEmpty() && email.isNotEmpty()) {
                shareDocument(docName, email)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun shareDocument(documentName: String, email: String) {
        // Simulate document sharing
        Toast.makeText(this, "Document '$documentName' shared with $email", Toast.LENGTH_LONG).show()
        
        // In a real app, this would create share links and send emails
        finish()
    }
}

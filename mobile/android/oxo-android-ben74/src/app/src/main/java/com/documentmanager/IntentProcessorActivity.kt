package com.documentmanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity that processes incoming intents from external apps for document sharing
 * and deep link handling. Supports various intent processing workflows.
 */
class IntentProcessorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        handleIncomingIntent(intent)
        finish()
    }
    
    /**
     * Processes various types of incoming intents for document operations
     */
    private fun handleIncomingIntent(intent: Intent) {
        when {
            intent.action == Intent.ACTION_SEND -> {
                handleSendAction(intent)
            }
            intent.action == Intent.ACTION_VIEW -> {
                handleViewAction(intent)
            }
            intent.hasExtra("extra_intent") -> {
                // VULNERABLE: Processes embedded intent without validation
                handleEmbeddedIntent(intent)
            }
            else -> {
                Log.d("DocumentProcessor", "Unknown intent action: ${intent.action}")
            }
        }
    }
    
    /**
     * Handles standard Android SEND actions for document sharing
     */
    private fun handleSendAction(intent: Intent) {
        val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        
        Log.d("DocumentProcessor", "Processing shared content: $uri")
        
        // Process shared documents
        if (uri != null) {
            val docIntent = Intent(this, DocumentsActivity::class.java)
            docIntent.putExtra("shared_uri", uri)
            startActivity(docIntent)
        }
    }
    
    /**
     * Handles deep link VIEW actions with custom scheme
     */
    private fun handleViewAction(intent: Intent) {
        val uri = intent.data
        if (uri?.scheme == "docmanager") {
            Log.d("DocumentProcessor", "Processing deep link: $uri")
            
            val action = uri.host
            when (action) {
                "open" -> {
                    val docId = uri.getQueryParameter("id")
                    val viewIntent = Intent(this, DocumentsActivity::class.java)
                    viewIntent.putExtra("document_id", docId)
                    startActivity(viewIntent)
                }
                "share" -> {
                    val shareIntent = Intent(this, ShareActivity::class.java)
                    shareIntent.putExtra("share_link", uri.toString())
                    startActivity(shareIntent)
                }
            }
        }
    }
    
    /**
     * VULNERABLE: Processes embedded intent without validation
     * This is the core vulnerability that allows bypassing Android export restrictions
     */
    private fun handleEmbeddedIntent(intent: Intent) {
        try {
            val embeddedIntent = intent.getParcelableExtra<Intent>("extra_intent")
            if (embeddedIntent != null) {
                Log.d("DocumentProcessor", "Processing embedded intent: ${embeddedIntent.component}")
                
                // CRITICAL VULNERABILITY: Directly starting embedded intent
                // This bypasses Android's export restrictions and allows access to non-exported components
                startActivity(embeddedIntent)
                Log.d("DocumentProcessor", "Embedded intent executed successfully")
            }
        } catch (e: Exception) {
            Log.e("DocumentProcessor", "Error processing embedded intent", e)
        }
    }
    
    /**
     * Handles complex workflow intents that may contain embedded operations.
     * This is used for advanced document processing workflows and automation.
     */
    private fun handleWorkflowIntent(intent: Intent) {
        val workflowIntent = intent.getParcelableExtra<Intent>("workflow_intent")
        
        if (workflowIntent != null) {
            Log.d("DocumentProcessor", "Processing workflow intent: ${workflowIntent.component}")
            
            try {
                // Execute the workflow intent for document processing automation
                startActivity(workflowIntent)
                Log.d("DocumentProcessor", "Workflow executed successfully")
            } catch (e: Exception) {
                Log.e("DocumentProcessor", "Failed to process workflow", e)
            }
        }
    }
}

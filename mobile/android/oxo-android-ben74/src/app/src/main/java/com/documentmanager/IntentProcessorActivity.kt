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
     * Processes incoming intents - this is the VULNERABLE implementation
     */
    private fun handleIncomingIntent(intent: Intent) {
        when {
            intent.hasExtra("extra_intent") -> {
                // VULNERABLE: Processes embedded intent without validation
                handleEmbeddedIntent(intent)
            }
            intent.hasExtra("admin_command") -> {
                // VULNERABLE: Also trigger embedded intent handling for admin commands
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
            // Method 1: Try to get Parcelable Intent (standard approach)
            var embeddedIntent = intent.getParcelableExtra<Intent>("extra_intent")
            
            // Method 2: If no Parcelable, check for admin command (vulnerability trigger)
            if (embeddedIntent == null) {
                val adminCommand = intent.getStringExtra("admin_command")
                val adminTarget = intent.getStringExtra("admin_target")
                
                // VULNERABLE: Create embedded intent from admin command without validation
                if (adminCommand != null) {
                    Log.d("DocumentProcessor", "Creating embedded intent from admin command: $adminCommand")
                    
                    embeddedIntent = Intent().apply {
                        setClassName("com.documentmanager", "com.documentmanager.AdminPanelActivity")
                        putExtra("admin_command", adminCommand)
                        putExtra("admin_target", adminTarget ?: "unknown")
                        putExtra("bypassed_via_proxy", true)
                    }
                } else {
                    // Method 3: Try to parse from string format
                    val intentString = intent.getStringExtra("extra_intent")
                    if (intentString != null) {
                        Log.d("DocumentProcessor", "Parsing intent from string: $intentString")
                        
                        // Try to parse intent URI format
                        if (intentString.startsWith("intent:")) {
                            embeddedIntent = Intent.parseUri(intentString, Intent.URI_INTENT_SCHEME)
                        }
                    }
                }
            }
            
            if (embeddedIntent != null) {
                Log.d("DocumentProcessor", "Processing embedded intent: ${embeddedIntent.component}")
                
                // CRITICAL VULNERABILITY: Directly starting embedded intent
                // This bypasses Android's export restrictions and allows access to non-exported components
                startActivity(embeddedIntent)
                Log.d("DocumentProcessor", "Embedded intent executed successfully")
                Log.d("VULNERABILITY_EXPLOITED", "Non-exported AdminPanelActivity accessed via proxy!")
            } else {
                Log.d("DocumentProcessor", "No embedded intent found")
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

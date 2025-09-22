package com.documentmanager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import org.json.JSONObject

/**
 * Activity that processes incoming intents from external apps for document sharing
 * and workflow automation. VULNERABLE: Processes URI permission grants without validation.
 */
class IntentProcessorActivity : Activity() {

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
            intent.hasExtra("workflow_intent") -> {
                // VULNERABLE: Processes workflow intents with URI permission grants FIRST
                // This is the critical vulnerability - workflow intents processed regardless of action
                handleWorkflowIntent(intent)
            }
            intent.action == Intent.ACTION_SEND -> {
                handleSendAction(intent)
            }
            intent.action == Intent.ACTION_VIEW -> {
                handleViewAction(intent)
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
     * VULNERABLE: Processes workflow intents with URI permission grants
     * This allows bypassing content provider restrictions through permission escalation
     */
    private fun handleWorkflowIntent(intent: Intent) {
        try {
            val workflowIntentJson = intent.getStringExtra("workflow_intent")
            if (!workflowIntentJson.isNullOrEmpty()) {
                Log.d("DocumentProcessor", "Processing workflow intent JSON")
                handleWorkflowIntent(workflowIntentJson)
            }
        } catch (e: Exception) {
            Log.e("DocumentProcessor", "Error processing workflow intent", e)
        }
    }
    
    /**
     * VULNERABLE: Processes embedded workflow intent from JSON with URI permission flags
     * This is the core vulnerability that enables Grant URI Permission Escalation
     */
    private fun handleWorkflowIntent(intentJson: String) {
        try {
            val jsonObject = JSONObject(intentJson)
            val workflowIntent = Intent().apply {
                action = jsonObject.optString("action", Intent.ACTION_VIEW)
                
                // Parse target URI (content provider URI)
                if (jsonObject.has("data")) {
                    data = Uri.parse(jsonObject.getString("data"))
                }
                
                // CRITICAL VULNERABILITY: URI permission flags processed without validation
                if (jsonObject.has("flags")) {
                    val flagsArray = jsonObject.getJSONArray("flags")
                    for (i in 0 until flagsArray.length()) {
                        when (flagsArray.getString(i)) {
                            "FLAG_GRANT_READ_URI_PERMISSION" -> {
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                Log.w("DocumentProcessor", "⚠️ Adding READ URI permission grant")
                            }
                            "FLAG_GRANT_WRITE_URI_PERMISSION" -> {
                                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                                Log.w("DocumentProcessor", "⚠️ Adding WRITE URI permission grant")
                            }
                            "FLAG_GRANT_PREFIX_URI_PERMISSION" -> {
                                addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
                                Log.w("DocumentProcessor", "⚠️ Adding PREFIX URI permission grant")
                            }
                            "FLAG_GRANT_PERSISTABLE_URI_PERMISSION" -> {
                                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                                Log.w("DocumentProcessor", "⚠️ Adding PERSISTABLE URI permission grant")
                            }
                        }
                    }
                }
                
                // Set target component if specified
                if (jsonObject.has("component")) {
                    val componentStr = jsonObject.getString("component")
                    if (componentStr.isNotEmpty()) {
                        val parts = componentStr.split("/")
                        if (parts.size == 2) {
                            setClassName(parts[0], parts[1])
                        }
                    }
                }
            }
            
            // DANGEROUS: Execute workflow intent with URI permission grants
            Log.d("DocumentProcessor", "Executing workflow intent with URI: ${workflowIntent.data}")
            Log.d("DocumentProcessor", "Intent flags: ${workflowIntent.flags}")
            
            try {
                startActivity(workflowIntent)
                Log.i("DocumentProcessor", "✅ Workflow intent executed - URI permissions granted")
            } catch (e: Exception) {
                Log.e("DocumentProcessor", "Failed to execute workflow intent", e)
                
                // Fallback: Try to grant permissions directly to calling package
                if (workflowIntent.data != null) {
                    try {
                        grantUriPermission(
                            callingActivity?.packageName,
                            workflowIntent.data!!,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                        Log.w("DocumentProcessor", "⚠️ Direct URI permission granted as fallback")
                    } catch (ex: Exception) {
                        Log.e("DocumentProcessor", "Failed to grant URI permission", ex)
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e("DocumentProcessor", "Error parsing workflow intent JSON", e)
        }
    }
}

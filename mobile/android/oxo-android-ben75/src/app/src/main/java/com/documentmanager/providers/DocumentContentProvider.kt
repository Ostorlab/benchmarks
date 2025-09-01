package com.documentmanager.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import java.io.FileNotFoundException

/**
 * Secure Content Provider for document storage and management.
 * VULNERABLE: Non-exported but with grantUriPermissions="true" allowing URI permission escalation.
 */
class DocumentContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.documentmanager.secure"
        const val TABLE_DOCUMENTS = "documents"
        const val TABLE_USERS = "users"
        const val TABLE_SESSIONS = "sessions"
    }

    override fun onCreate(): Boolean {
        Log.d("DocumentProvider", "DocumentContentProvider initialized")
        initializeSecureData()
        return true
    }

    /**
     * Initialize sensitive business data that this provider protects
     */
    private fun initializeSecureData() {
        val context = context ?: return
        val secureDir = File(context.filesDir, "secure")
        if (!secureDir.exists()) {
            secureDir.mkdirs()
        }

        // Create sensitive business documents
        val sensitiveFiles = mapOf(
            "Financial_Report_Q4_2024.pdf" to """
                CONFIDENTIAL - FINANCIAL REPORT Q4 2024
                ========================================
                Total Revenue: $4,250,000
                Net Profit: $1,150,000
                Operating Expenses: $3,100,000
                
                SENSITIVE FINANCIAL DATA:
                Bank Account: 789-456-123-001 (Primary Operations)
                Credit Line: $2,000,000 available
                Tax ID: 94-1234567
                
                Key Performance Indicators:
                - Customer acquisition cost: $89
                - Customer lifetime value: $3,200
                - Monthly recurring revenue: $425,000
                - Churn rate: 2.1%
                
                CONFIDENTIAL - FOR BOARD MEMBERS ONLY
            """.trimIndent(),
            
            "Employee_Database.xlsx" to """
                CONFIDENTIAL - EMPLOYEE RECORDS
                ===============================
                
                EMPLOYEE ID: 12345
                Name: Sarah Johnson
                SSN: 555-67-8901
                Salary: $125,000
                Department: Engineering
                Security Clearance: Level 3
                
                EMPLOYEE ID: 12346  
                Name: Michael Chen
                SSN: 555-78-9012
                Salary: $145,000
                Department: Finance
                Security Clearance: Level 4
                Personal Note: Has access to all financial systems
                
                EMPLOYEE ID: 12347
                Name: Jessica Williams
                SSN: 555-89-0123
                Salary: $165,000
                Department: Executive
                Security Clearance: Level 5
                Note: CEO Assistant - Full system access
                
                *** CONFIDENTIAL HR DATA - RESTRICTED ACCESS ***
            """.trimIndent(),
            
            "Client_Contracts.docx" to """
                CONFIDENTIAL CLIENT CONTRACTS
                ============================
                
                CONTRACT #: CC-2024-789
                Client: TechCorp Industries
                Value: $850,000
                Payment Terms: Net 30
                Contract Period: 2024-2026
                
                SENSITIVE INFORMATION:
                Client Contact: john.doe@techcorp.com
                Direct Phone: +1-555-0123
                Emergency Contact: +1-555-0124
                
                BANKING DETAILS:
                Wire Instructions: Bank of America
                Account: 1234567890
                Routing: 021000021
                
                CONFIDENTIALITY CLAUSE:
                This contract contains proprietary information
                and trade secrets. Unauthorized disclosure is
                strictly prohibited and may result in legal action.
                
                *** LEGAL DEPARTMENT USE ONLY ***
            """.trimIndent()
        )

        sensitiveFiles.forEach { (filename, content) ->
            val file = File(secureDir, filename)
            if (!file.exists()) {
                file.writeText(content)
                Log.d("DocumentProvider", "Created sensitive file: $filename")
            }
        }
    }

    /**
     * VULNERABLE: Query method that exposes sensitive business data
     * Assumes only internal components can access due to exported="false"
     */
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d("DocumentProvider", "Query requested for URI: $uri")
        
        val cursor = MatrixCursor(arrayOf("_id", "filename", "type", "size", "created", "sensitive_data"))
        
        when (uri.lastPathSegment) {
            TABLE_DOCUMENTS -> {
                // Return sensitive document information
                cursor.addRow(arrayOf(
                    1, 
                    "Financial_Report_Q4_2024.pdf", 
                    "financial", 
                    "2.1MB",
                    "2024-12-31",
                    "Revenue: $4.25M, Profit: $1.15M, Bank: 789-456-123-001"
                ))
                cursor.addRow(arrayOf(
                    2, 
                    "Employee_Database.xlsx", 
                    "hr", 
                    "1.8MB",
                    "2024-12-15",
                    "125 employees, SSNs included, salary data, security clearances"
                ))
                cursor.addRow(arrayOf(
                    3, 
                    "Client_Contracts.docx", 
                    "legal", 
                    "950KB",
                    "2024-11-20",
                    "TechCorp: $850K, Banking: BoA 1234567890, Emergency contacts"
                ))
            }
            
            TABLE_USERS -> {
                // Expose user authentication data
                cursor.addRow(arrayOf(
                    1,
                    "admin_user.json",
                    "auth",
                    "512B", 
                    "2024-12-01",
                    "admin: bcrypt_hash_abc123, session_token: xyz789def456"
                ))
                cursor.addRow(arrayOf(
                    2,
                    "finance_users.json", 
                    "auth",
                    "1.2KB",
                    "2024-11-28",
                    "5 finance users, password hashes, 2FA secrets included"
                ))
            }
            
            TABLE_SESSIONS -> {
                // Active user sessions with tokens
                cursor.addRow(arrayOf(
                    1,
                    "active_sessions.json",
                    "session",
                    "2.1KB",
                    "2024-12-31", 
                    "12 active sessions, JWT tokens, admin privileges"
                ))
            }
            
            else -> {
                Log.w("DocumentProvider", "Unknown table requested: ${uri.lastPathSegment}")
                return null
            }
        }
        
        Log.i("DocumentProvider", "Returning ${cursor.count} sensitive records")
        return cursor
    }

    /**
     * VULNERABLE: Provides direct file access to sensitive documents
     */
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        Log.d("DocumentProvider", "File access requested: $uri")
        
        val context = context ?: return null
        val filename = uri.lastPathSegment
        val secureDir = File(context.filesDir, "secure")
        val file = File(secureDir, filename ?: "")
        
        return if (file.exists()) {
            Log.w("DocumentProvider", "⚠️ Providing access to sensitive file: ${file.name}")
            try {
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            } catch (e: FileNotFoundException) {
                Log.e("DocumentProvider", "File not found: ${file.absolutePath}", e)
                null
            }
        } else {
            Log.e("DocumentProvider", "Requested file does not exist: $filename")
            null
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uri.lastPathSegment) {
            TABLE_DOCUMENTS -> "vnd.android.cursor.dir/documents"
            TABLE_USERS -> "vnd.android.cursor.dir/users"  
            TABLE_SESSIONS -> "vnd.android.cursor.dir/sessions"
            else -> "application/octet-stream"
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.w("DocumentProvider", "Insert operation attempted - potentially malicious")
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        Log.w("DocumentProvider", "Delete operation attempted - potentially malicious")
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        Log.w("DocumentProvider", "Update operation attempted - potentially malicious") 
        return 0
    }
}

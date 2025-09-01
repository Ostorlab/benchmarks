package com.documentmanager.data

import android.content.Context
import android.util.Log
import java.io.File

/**
 * Manager for document data initialization and management
 */
object DocumentDataManager {
    
    fun initializeDocuments(context: Context) {
        createSecureDocuments(context)
        Log.d("DocumentManager", "Document storage initialized")
    }
    
    private fun createSecureDocuments(context: Context) {
        val secureDir = File(context.filesDir, "secure")
        if (!secureDir.exists()) {
            secureDir.mkdirs()
        }
        
        // Create sample business documents
        val documents = mapOf(
            "Q4_Financial_Report.pdf" to """
                CONFIDENTIAL FINANCIAL REPORT Q4 2024
                =====================================
                Revenue: $2,450,000
                Expenses: $1,800,000  
                Net Profit: $650,000
                
                Account Details:
                Primary Account: 789456123001
                Secondary Account: 789456123002
                Credit Line: $1,000,000
                
                Key Metrics:
                - Customer acquisition cost: $125
                - Lifetime value: $2,400
                - Churn rate: 3.2%
                - Market share: 12.5%
            """.trimIndent(),
            
            "Employee_Records.xlsx" to """
                EMPLOYEE DATABASE - CONFIDENTIAL
                =================================
                EMP001 | John Smith | john.smith@company.com | $85,000 | Marketing Director
                EMP002 | Jane Doe | jane.doe@company.com | $92,000 | Senior Developer  
                EMP003 | Bob Wilson | bob.wilson@company.com | $78,000 | Operations Manager
                EMP004 | Alice Brown | alice.brown@company.com | $95,000 | VP Engineering
                
                Benefits Information:
                - Health Insurance: Anthem Blue Cross
                - 401k Matching: 6% company match
                - Stock Options: Available for senior level
                - PTO: 20 days annual + 10 sick days
            """.trimIndent(),
            
            "Business_Strategy_2025.pptx" to """
                BUSINESS STRATEGY 2025 - CONFIDENTIAL
                =====================================
                Strategic Initiatives:
                1. Market Expansion - Target 25% growth
                2. Product Innovation - 3 new product launches
                3. Digital Transformation - Cloud migration
                4. Talent Acquisition - Hire 50 new employees
                
                Competitive Analysis:
                - Competitor A: 18% market share, strong in enterprise
                - Competitor B: 15% market share, focused on SMB
                - Our Position: 12% market share, growth opportunity
                
                Financial Projections:
                Year 1: $3.2M revenue target
                Year 2: $4.1M revenue target  
                Year 3: $5.5M revenue target
            """.trimIndent(),
            
            "Client_Contracts.docx" to """
                CLIENT CONTRACTS SUMMARY - LEGAL CONFIDENTIAL
                =============================================
                Active Contracts:
                
                CT-2024-001 | Acme Corporation | $500,000 | NET 30
                Contact: procurement@acme.com | Legal: legal@acme.com
                
                CT-2024-002 | Global Industries | $750,000 | NET 45  
                Contact: sourcing@globalind.com | Legal: contracts@globalind.com
                
                CT-2024-003 | TechStart Inc | $320,000 | NET 15
                Contact: finance@techstart.com | Legal: legal@techstart.com
                
                Payment Terms & Conditions:
                - Late payment penalty: 1.5% monthly
                - Termination clause: 30 days notice
                - IP ownership: Retained by company
                - Confidentiality: 5 year non-disclosure
            """.trimIndent()
        )
        
        documents.forEach { (filename, content) ->
            val file = File(secureDir, filename)
            if (!file.exists()) {
                file.writeText(content)
                Log.d("DocumentManager", "Created document: $filename")
            }
        }
    }
}

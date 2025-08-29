package com.systemmanager

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ReportsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "System Reports"
        
        loadReportsData()
    }
    
    private fun loadReportsData() {
        loadPerformanceReport()
        loadUsageReport()
        loadComplianceReport()
        loadActivityLog()
    }
    
    private fun loadPerformanceReport() {
        val cpuUsage = Random.nextInt(20, 85)
        val memoryUsage = Random.nextInt(30, 90)
        val diskUsage = Random.nextInt(25, 80)
        
        findViewById<TextView>(R.id.tv_cpu_usage).text = "CPU Usage: ${cpuUsage}%"
        findViewById<TextView>(R.id.tv_memory_usage).text = "Memory Usage: ${memoryUsage}%"
        findViewById<TextView>(R.id.tv_disk_usage).text = "Disk Usage: ${diskUsage}%"
        
        // Color coding for performance metrics
        findViewById<TextView>(R.id.tv_cpu_usage).setTextColor(
            if (cpuUsage > 70) Color.RED else if (cpuUsage > 50) Color.YELLOW else Color.GREEN
        )
        findViewById<TextView>(R.id.tv_memory_usage).setTextColor(
            if (memoryUsage > 75) Color.RED else if (memoryUsage > 60) Color.YELLOW else Color.GREEN
        )
        findViewById<TextView>(R.id.tv_disk_usage).setTextColor(
            if (diskUsage > 70) Color.RED else if (diskUsage > 50) Color.YELLOW else Color.GREEN
        )
    }
    
    private fun loadUsageReport() {
        val todayUsage = Random.nextInt(4, 10)
        val weekUsage = Random.nextInt(35, 65)
        val monthUsage = Random.nextInt(150, 280)
        
        findViewById<TextView>(R.id.tv_today_usage).text = "Today: ${todayUsage}h ${Random.nextInt(15, 55)}m"
        findViewById<TextView>(R.id.tv_week_usage).text = "This Week: ${weekUsage}h ${Random.nextInt(10, 50)}m"
        findViewById<TextView>(R.id.tv_month_usage).text = "This Month: ${monthUsage}h ${Random.nextInt(20, 45)}m"
    }
    
    private fun loadComplianceReport() {
        val complianceScore = Random.nextInt(85, 98)
        val policyViolations = Random.nextInt(0, 3)
        val lastAudit = "2024-01-${Random.nextInt(10, 28)}"
        
        findViewById<TextView>(R.id.tv_compliance_score).text = "Compliance Score: ${complianceScore}%"
        findViewById<TextView>(R.id.tv_policy_violations).text = "Policy Violations: $policyViolations"
        findViewById<TextView>(R.id.tv_last_audit).text = "Last Audit: $lastAudit"
        
        // Color coding for compliance
        findViewById<TextView>(R.id.tv_compliance_score).setTextColor(
            if (complianceScore > 90) Color.GREEN else if (complianceScore > 80) Color.YELLOW else Color.RED
        )
        findViewById<TextView>(R.id.tv_policy_violations).setTextColor(
            if (policyViolations == 0) Color.GREEN else if (policyViolations <= 2) Color.YELLOW else Color.RED
        )
    }
    
    private fun loadActivityLog() {
        val activities = listOf(
            "System startup completed",
            "User authentication successful",
            "Configuration updated",
            "Backup process completed",
            "Performance optimization applied",
            "System monitoring activated",
            "Log rotation performed",
            "Network connectivity verified"
        )
        
        val activityLog = StringBuilder()
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        
        for (i in 0..6) {
            val time = dateFormat.format(Date(System.currentTimeMillis() - (i * 1000 * 60 * Random.nextInt(5, 60))))
            val activity = activities.random()
            activityLog.append("$time - $activity\n")
        }
        
        findViewById<TextView>(R.id.tv_activity_log).text = activityLog.toString()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

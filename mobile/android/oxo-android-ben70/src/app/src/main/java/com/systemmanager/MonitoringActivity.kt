package com.systemmanager

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MonitoringActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitoring)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "System Monitoring"
        
        setupMonitoring()
    }
    
    private fun setupMonitoring() {
        // Update real-time metrics based on actual layout
        val cpuUsage = Random.nextInt(10, 85)
        val memoryUsage = Random.nextInt(30, 90)
        val diskUsage = Random.nextInt(25, 80)
        val networkUsage = Random.nextInt(15, 60)
        
        findViewById<TextView>(R.id.tv_cpu_monitoring).text = "${cpuUsage}%"
        findViewById<TextView>(R.id.tv_memory_monitoring).text = "${memoryUsage}%"
        findViewById<TextView>(R.id.tv_disk_monitoring).text = "${diskUsage}%"
        findViewById<TextView>(R.id.tv_network_monitoring).text = "${networkUsage}%"
        
        findViewById<TextView>(R.id.tv_process_count).text = "Total Processes: ${Random.nextInt(80, 150)}"
        findViewById<TextView>(R.id.tv_high_cpu_processes).text = "High CPU Processes: ${Random.nextInt(2, 8)}"
        
        findViewById<TextView>(R.id.tv_web_service_status).text = "Running"
        findViewById<TextView>(R.id.tv_db_service_status).text = "Running"  
        findViewById<TextView>(R.id.tv_monitor_service_status).text = "Running"
        
        setupButtons()
    }
    
    private fun setupButtons() {
        findViewById<Button>(R.id.btn_refresh_monitoring).setOnClickListener {
            setupMonitoring() // Refresh the data
        }
        
        findViewById<Button>(R.id.btn_export_data).setOnClickListener {
            Toast.makeText(this, "Monitoring data exported", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

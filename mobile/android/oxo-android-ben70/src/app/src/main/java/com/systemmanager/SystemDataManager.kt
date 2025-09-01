package com.systemmanager

import android.content.Context
import java.io.File
import java.io.FileWriter

class SystemDataManager(private val context: Context) {
    
    fun createSystemFiles() {
        try {
            createProcessList()
            createUserTokens()
            createSystemLogs()
            createConfigurationData()
        } catch (e: Exception) {
            // Silently handle errors
        }
    }
    
    private fun createProcessList() {
        val file = File(context.filesDir, "system_processes.txt")
        FileWriter(file).use { writer ->
            writer.write("PID\tUSER\tCOMMAND\n")
            writer.write("1\troot\t/sbin/init\n")
            writer.write("2\troot\t[kthreadd]\n")
            writer.write("3\troot\t[rcu_gp]\n")
            writer.write("1000\tsystem\t/system/bin/servicemanager\n")
            writer.write("1001\troot\t/system/bin/vold\n")
            writer.write("1002\tsystem\t/system/bin/surfaceflinger\n")
            writer.write("2000\tshell\t/system/bin/sh\n")
            writer.write("10001\tu0_a1\tcom.android.systemui\n")
            writer.write("10002\tu0_a2\tcom.google.android.gms\n")
        }
    }
    
    private fun createUserTokens() {
        val file = File(context.filesDir, "auth_tokens.dat")
        FileWriter(file).use { writer ->
            writer.write("admin:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFkbWluIFVzZXIiLCJpYXQiOjE1MTYyMzkwMjJ9.abc123\n")
            writer.write("system:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIwOTg3NjU0MzIxIiwibmFtZSI6IlN5c3RlbSBVc2VyIiwiaWF0IjoxNTE2MjM5MDIyfQ.def456\n")
            writer.write("service:ApiKey sk_live_abc123def456ghi789jkl012mno345\n")
            writer.write("operator:SessionId sess_1a2b3c4d5e6f7g8h9i0j\n")
        }
    }
    
    private fun createSystemLogs() {
        val file = File(context.filesDir, "system.log")
        FileWriter(file).use { writer ->
            writer.write("2024-01-15 10:30:15 [INFO] System startup completed\n")
            writer.write("2024-01-15 10:30:16 [INFO] Loading configuration from /etc/systemmanager/config.xml\n")
            writer.write("2024-01-15 10:30:17 [INFO] Database connection established: postgresql://admin:SecretPass123@localhost:5432/systemdb\n")
            writer.write("2024-01-15 10:30:18 [INFO] User authentication service started\n")
            writer.write("2024-01-15 10:30:19 [WARN] Failed login attempt for user 'guest' from 192.168.1.100\n")
            writer.write("2024-01-15 10:30:20 [INFO] Admin user logged in successfully\n")
            writer.write("2024-01-15 10:30:21 [DEBUG] API key validation: sk_live_abc123def456ghi789jkl012mno345\n")
        }
    }
    
    private fun createConfigurationData() {
        val file = File(context.filesDir, "app_config.json")
        FileWriter(file).use { writer ->
            writer.write("""
{
  "database": {
    "host": "localhost",
    "port": 5432,
    "username": "admin",
    "password": "SystemManager2024!",
    "database": "systemmanager"
  },
  "api": {
    "base_url": "https://api.systemmanager.com/v1",
    "secret_key": "sk_live_abc123def456ghi789jkl012mno345",
    "timeout": 30000
  },
  "authentication": {
    "jwt_secret": "super_secret_jwt_key_do_not_share",
    "session_timeout": 3600,
    "admin_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkFkbWluIFVzZXIiLCJpYXQiOjE1MTYyMzkwMjJ9.abc123"
  },
  "system": {
    "debug_mode": false,
    "log_level": "INFO",
    "encryption_key": "AES256_KEY_1234567890ABCDEF",
    "backup_location": "/data/backups/systemmanager"
  }
}
            """.trimIndent())
        }
    }
}

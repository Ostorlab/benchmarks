package com.example.nekoApplication

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = "NekoApplication"
    private lateinit var statusTextView: TextView
    private lateinit var executeButton: Button

    // Use the modern ActivityResultLauncher for permission requests
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Storage permission granted.")
            showToast("Storage permission granted.")
        } else {
            Log.e(TAG, "Storage permission denied.")
            showToast("Storage permission denied.")
        }
    }

    // Launcher for MANAGE_EXTERNAL_STORAGE on Android 11+
    private val allFilesAccessLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Log.d(TAG, "All files access granted.")
                showToast("All files access granted.")
            } else {
                Log.e(TAG, "All files access denied.")
                showToast("All files access denied.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTextView = findViewById(R.id.status_text_view)
        executeButton = findViewById(R.id.execute_button)

        // Check and request storage permissions
        checkStoragePermissions()

        // This is the core of the vulnerability.
        executeButton.setOnClickListener {
            // Check permissions again before attempting the vulnerable action
            if (hasStoragePermissions()) {
                // The new logic: prepare the binary before executing
                prepareAndExecuteBinary()
            } else {
                showToast("Permissions not granted. Cannot execute binary.")
            }
        }
    }

    /**
     * Checks if the app has the necessary storage permissions.
     */
    private fun hasStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED
        }
    }

    /**
     * Requests the necessary storage permissions.
     */
    private fun checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    allFilesAccessLauncher.launch(intent)
                    showToast("Please grant 'All files access' permission.")
                } catch (e: Exception) {
                    showToast("Failed to open permissions screen.")
                    Log.e(TAG, "Failed to open permissions screen: ${e.message}")
                }
            }
        } else {
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permission) != PermissionChecker.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    /**
     * Prepares the binary from external storage by copying it to a location where it can be executed.
     */
    private fun prepareAndExecuteBinary() {
        val externalBinaryFile = File(
            "${Environment.getExternalStorageDirectory().absolutePath}/ext/com.example.neko-application/binary"
        )
        val internalBinaryFile = File(filesDir, "binary")

        if (!externalBinaryFile.exists()) {
            val message = "Binary not found in external storage at: ${externalBinaryFile.absolutePath}"
            Log.e(TAG, message)
            showToast(message)
            return
        }

        try {
            // Copy the binary from the external storage to the app's private files directory
            externalBinaryFile.copyTo(internalBinaryFile, overwrite = true)

            // Set executable permissions on the copied file
            if (internalBinaryFile.setExecutable(true, false)) {
                Log.d(TAG, "Successfully copied and set executable permissions on binary.")
                executeBinaryFromInternalStorage(internalBinaryFile)
            } else {
                val message = "Failed to set executable permissions on the binary."
                Log.e(TAG, message)
                showToast(message)
            }
        } catch (e: IOException) {
            val message = "Error copying binary to internal storage: ${e.message}"
            Log.e(TAG, message)
            showToast(message)
        }
    }

    /**
     * This method contains the vulnerability. It attempts to execute a binary
     * from a trusted location. However, the binary itself came from untrusted external storage.
     */
    private fun executeBinaryFromInternalStorage(binaryFile: File) {
        try {
            // THE VULNERABLE ACTION: Blindly executing the binary.
            // The file's source is external and has not been verified.
            val process = Runtime.getRuntime().exec(binaryFile.absolutePath)
            val exitCode = process.waitFor()
            val message = "Executed binary from internal storage.\nExit code: $exitCode"
            Log.d(TAG, message)
            showToast(message)
        } catch (e: IOException) {
            val message = "Error executing binary: ${e.message}"
            Log.e(TAG, message)
            showToast(message)
        } catch (e: InterruptedException) {
            val message = "Error executing binary: ${e.message}"
            Log.e(TAG, message)
            showToast(message)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

package co.ostorlab.cloudshare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UploadActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Upload Files"
        
        setupUI()
    }
    
    private fun setupUI() {
        findViewById<Button>(R.id.btn_select_files).setOnClickListener {
            // File picker intent
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, "Select Files"), 1001)
        }
        
        findViewById<Button>(R.id.btn_take_photo).setOnClickListener {
            // Camera intent
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 1002)
            }
        }
        
        // Upload status
        findViewById<TextView>(R.id.tv_upload_status).text = "Ready to upload"
        findViewById<ProgressBar>(R.id.progress_upload).progress = 0
        
        // Storage info
        findViewById<TextView>(R.id.tv_available_space).text = "12.6 GB available"
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1001 -> {
                    // File selected
                    findViewById<TextView>(R.id.tv_upload_status).text = "File ready for upload"
                }
                1002 -> {
                    // Photo taken
                    findViewById<TextView>(R.id.tv_upload_status).text = "Photo ready for upload"
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

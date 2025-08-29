package co.ostorlab.cloudshare

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class ShareActivity : AppCompatActivity() {
    
    private lateinit var etFileName: EditText
    private lateinit var etSharePassword: EditText
    private lateinit var switchPasswordProtected: Switch
    private lateinit var btnCreateShare: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Share Files"
        
        setupUI()
    }
    
    private fun setupUI() {
        etFileName = findViewById(R.id.et_file_name)
        etSharePassword = findViewById(R.id.et_share_password)
        switchPasswordProtected = findViewById(R.id.switch_password_protected)
        btnCreateShare = findViewById(R.id.btn_create_share)
        
        switchPasswordProtected.setOnCheckedChangeListener { _, isChecked ->
            etSharePassword.isEnabled = isChecked
            if (!isChecked) {
                etSharePassword.text.clear()
            }
        }
        
        btnCreateShare.setOnClickListener {
            createNewShare()
        }
    }
    
    private fun createNewShare() {
        val fileName = etFileName.text.toString().trim()
        if (fileName.isEmpty()) {
            Toast.makeText(this, "Please enter a file name", Toast.LENGTH_SHORT).show()
            return
        }
        
        val isPasswordProtected = switchPasswordProtected.isChecked
        val password = if (isPasswordProtected) etSharePassword.text.toString() else ""
        
        if (isPasswordProtected && password.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Generate share token and URL
        val shareToken = UUID.randomUUID().toString()
        val shareUrl = "https://cloudshare.pro/s/${shareToken.substring(0, 8)}"
        
        // Create password-protected share with bcrypt hash
        if (isPasswordProtected) {
            val bcryptHash = BCrypt.hashpw(password, BCrypt.gensalt(12))
            val shareData = co.ostorlab.cloudshare.models.ShareData(
                token = shareToken,
                shareWith = bcryptHash,  // Store bcrypt hash
                fileName = fileName,
                fileSize = (Math.random() * 5000000).toLong(),
                userId = 1001, // Current user
                expiration = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), // 7 days
                shareUrl = shareUrl
            )
            
            // Store in database via DatabaseHelper
            val dbHelper = co.ostorlab.cloudshare.database.DatabaseHelper.getInstance(this)
            dbHelper.insertShare(shareData)
            
            Toast.makeText(this, "Password-protected share created: $shareUrl", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Public share created: $shareUrl", Toast.LENGTH_LONG).show()
        }
        
        // Clear form
        etFileName.text.clear()
        etSharePassword.text.clear()
        switchPasswordProtected.isChecked = false
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

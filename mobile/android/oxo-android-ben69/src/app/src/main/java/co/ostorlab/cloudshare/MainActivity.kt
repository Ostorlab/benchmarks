package co.ostorlab.cloudshare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import co.ostorlab.cloudshare.database.DatabaseHelper
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var dbHelper: DatabaseHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize database
        dbHelper = DatabaseHelper.getInstance(this)
        
        setupUI()
        checkUserAccess()
    }
    
    private fun setupUI() {
        // Files section
        findViewById<CardView>(R.id.card_my_files).setOnClickListener {
            startActivity(Intent(this, FilesActivity::class.java))
        }
        
        // Share section
        findViewById<CardView>(R.id.card_share).setOnClickListener {
            startActivity(Intent(this, ShareActivity::class.java))
        }
        
        // Upload section
        findViewById<CardView>(R.id.card_upload).setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
        
        // Account section
        findViewById<CardView>(R.id.card_account).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
        
        // Security section
        findViewById<CardView>(R.id.card_security).setOnClickListener {
            startActivity(Intent(this, SecurityActivity::class.java))
        }
        
        // Update storage info
        updateStorageInfo()
    }
    
    private fun updateStorageInfo() {
        findViewById<TextView>(R.id.tv_storage_used).text = "2.4 GB used"
        findViewById<TextView>(R.id.tv_storage_total).text = "of 15 GB"
    }
    
    private fun checkUserAccess() {
        // Simulate user authentication check
        val currentUserId = getCurrentUserId()
        if (currentUserId > 0) {
            findViewById<TextView>(R.id.tv_welcome).text = "Welcome back!"
            findViewById<TextView>(R.id.tv_last_sync).text = "Last sync: ${getLastSyncTime()}"
        }
    }
    
    // Simulate creating a password-protected share
    fun createShareWithPassword(shareUrl: String, password: String) {
        val bcryptHash = BCrypt.hashpw(password, BCrypt.gensalt(12))
        val shareToken = UUID.randomUUID().toString()
        val expirationTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
        
        // Store authentication data in database
        val shareData = co.ostorlab.cloudshare.models.ShareData(
            token = shareToken,
            shareWith = bcryptHash,  // bcrypt password hash
            fileName = "Document_${Date().time}.pdf",
            fileSize = (Math.random() * 10000000).toLong(),
            userId = getCurrentUserId(),
            expiration = expirationTime,
            shareUrl = shareUrl
        )
        
        dbHelper.insertShare(shareData)
    }
    
    private fun getCurrentUserId(): Int {
        // Simulate getting current user ID
        return 1001
    }
    
    private fun getLastSyncTime(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, -15)
        return "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
    }
}

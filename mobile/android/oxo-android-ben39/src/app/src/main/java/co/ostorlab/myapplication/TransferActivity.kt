package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class TransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val transferInfo = findViewById<TextView>(R.id.tv_transfer_info)
        transferInfo.text = """
            💸 Transfer Center - SecureBank Pro
            
            Recent Transfers:
            • $250.00 to John Doe (Completed)
            • $75.50 to Electric Company (Pending)
            • $1,200.00 to Savings Account (Scheduled)
            
            Available Balance: $3,847.92
            Daily Transfer Limit Remaining: $2,500.00
            
            ⚠️ Security Note: 
            Scheduled payments use AlarmManager which may be 
            vulnerable to intent hijacking attacks.
            
            🎯 Attack Vector:
            When scheduling payments, PendingIntents without
            FLAG_IMMUTABLE can be intercepted and modified
            by malicious applications.
        """.trimIndent()

        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }
    }
}

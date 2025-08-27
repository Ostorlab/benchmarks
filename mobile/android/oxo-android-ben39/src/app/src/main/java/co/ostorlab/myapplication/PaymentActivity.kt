package co.ostorlab.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Extract payment data if launched from hijacked intent
        val amount = intent.getStringExtra("amount") ?: "N/A"
        val accountId = intent.getStringExtra("account_id") ?: "N/A" 
        val recipient = intent.getStringExtra("recipient") ?: "N/A"
        val authToken = intent.getStringExtra("auth_token") ?: "N/A"
        
        val paymentDetails = findViewById<TextView>(R.id.tv_payment_details)
        paymentDetails.text = """
            💳 Payment Center - SecureBank Pro
            
            Current Payment Details:
            Amount: $$amount
            Account ID: $accountId
            Recipient: $recipient
            Auth Token: ${authToken.take(20)}...
            
            📋 This data came from the Intent!
            If hijacked, this would show modified values.
            
            🔒 In a real banking app, this would initiate
            actual payment processing with these values.
        """.trimIndent()

        val backButton = findViewById<Button>(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }
    }
}

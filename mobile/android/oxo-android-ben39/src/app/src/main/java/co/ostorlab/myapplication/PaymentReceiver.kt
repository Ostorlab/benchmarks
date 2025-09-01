package co.ostorlab.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class PaymentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.bankingapp.PROCESS_PAYMENT") {
            // Extract payment details from the intent
            val amount = intent.getStringExtra("amount") ?: "0.00"
            val accountId = intent.getStringExtra("account_id") ?: "unknown"
            val recipient = intent.getStringExtra("recipient") ?: "unknown"
            val authToken = intent.getStringExtra("auth_token") ?: "none"
            val transactionType = intent.getStringExtra("transaction_type") ?: "payment"
            
            // Display payment information (this would normally process the payment)
            val paymentInfo = """
                💰 PAYMENT PROCESSED:
                Amount: $$amount
                Account: $accountId
                Recipient: $recipient
                Token: ${authToken.take(15)}...
                Type: $transactionType
                
                ⚠️ VULNERABLE: This intent was intercepted!
                An attacker could modify these values!
            """.trimIndent()
            
            Toast.makeText(context, paymentInfo, Toast.LENGTH_LONG).show()
            
            // In a real banking app, this would process the payment
            // But since this PendingIntent is mutable, an attacker could modify
            // the amount, recipient, or other sensitive parameters
        }
    }
}

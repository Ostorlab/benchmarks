package co.ostorlab.myapplication

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ID = "banking_channel"
        const val PAYMENT_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        // Schedule Payment Button (VULNERABLE FEATURE)
        val schedulePaymentButton = findViewById<Button>(R.id.btn_schedule_payment)
        schedulePaymentButton.setOnClickListener {
            try {
                schedulePayment()
                Toast.makeText(this, "Payment scheduled successfully for 30 seconds!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error scheduling payment: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Transfer Funds
        val transferButton = findViewById<Button>(R.id.btn_transfer)
        transferButton.setOnClickListener {
            startActivity(Intent(this, TransferActivity::class.java))
        }

        // Payment Center
        val paymentCenterButton = findViewById<Button>(R.id.btn_payment_center)
        paymentCenterButton.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        // Account Settings
        val settingsButton = findViewById<Button>(R.id.btn_settings)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun schedulePayment() {
        // VULNERABLE IMPLEMENTATION: Schedule a payment using AlarmManager
        // This represents a real banking scenario where users can schedule future payments
        
        // Create payment intent with sensitive banking data
        val alarmIntent = Intent("com.bankingapp.PROCESS_PAYMENT")
        alarmIntent.putExtra("amount", "500.00")  // Payment amount
        alarmIntent.putExtra("account_id", "ACC_12345678")  // Source account
        alarmIntent.putExtra("recipient_account", "RECV_87654321")  // Destination account
        alarmIntent.putExtra("recipient_name", "John Smith")
        alarmIntent.putExtra("auth_token", "banking_jwt_token_abc123xyz")  // Sensitive auth token
        alarmIntent.putExtra("transaction_type", "scheduled_payment")
        alarmIntent.putExtra("payment_description", "Monthly rent payment")
        alarmIntent.putExtra("routing_number", "123456789")  // Sensitive banking info
        
        // VULNERABILITY: Missing FLAG_IMMUTABLE allows malicious apps to modify the intent
        // This is a common vulnerability in banking apps that use AlarmManager for scheduled payments
        val alarmPendingIntent = PendingIntent.getBroadcast(
            this, 
            PAYMENT_REQUEST_CODE, 
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT  // Missing FLAG_IMMUTABLE makes this vulnerable!
        )

        // Schedule the payment processing for 30 seconds from now
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 30000,  // 30 seconds delay for demo purposes
            alarmPendingIntent
        )
        
        // Show professional banking notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("SecureBank Pro - Payment Scheduled")
            .setContentText("$500.00 payment to John Smith scheduled for processing")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Your scheduled payment of $500.00 to John Smith will be processed in 30 seconds. You will receive a confirmation once the transaction is complete."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(this)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(2001, notification)
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Notification permission required for payment confirmations", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SecureBank Payment Alerts"
            val descriptionText = "Notifications for scheduled payments and banking alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

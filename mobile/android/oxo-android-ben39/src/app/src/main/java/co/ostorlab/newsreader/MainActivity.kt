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
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ID = "banking_channel"
        const val PAYMENT_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        val schedulePaymentButton = findViewById<ImageButton>(R.id.btn_notification)
        schedulePaymentButton.setOnClickListener {
            try {
                schedulePayment()
                Toast.makeText(this, "Payment scheduled successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        val paymentButton = findViewById<Button>(R.id.btn_profile)
        paymentButton.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }
        val transferButton = findViewById<Button>(R.id.btn_settings)
        transferButton.setOnClickListener {
            startActivity(Intent(this, TransferActivity::class.java))
        }
        val aboutButton = findViewById<Button>(R.id.btn_about)
        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun schedulePayment() {
        // VULNERABLE: Create payment intent with sensitive data
        val alarmIntent = Intent("com.bankingapp.PROCESS_PAYMENT")
        alarmIntent.putExtra("amount", "100.00")
        alarmIntent.putExtra("account_id", "ACC_12345")
        alarmIntent.putExtra("recipient", "trusted_recipient")
        alarmIntent.putExtra("auth_token", "banking_jwt_token_xyz789")
        alarmIntent.putExtra("transaction_type", "scheduled_payment")
        
        // VULNERABLE: Missing FLAG_IMMUTABLE allows intent modification
        val alarmPendingIntent = PendingIntent.getBroadcast(
            this, PAYMENT_REQUEST_CODE, alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT  // Missing FLAG_IMMUTABLE!
        )

        // Schedule payment processing for 30 seconds from now
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 30000,  // 30 seconds delay
            alarmPendingIntent
        )
        
        // Show notification about scheduled payment
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Payment Scheduled")
            .setContentText("$100.00 payment will be processed in 30 seconds")
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(this)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(2001, notification)
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Notification permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Banking Alerts"
            val descriptionText = "Channel for payment and banking notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

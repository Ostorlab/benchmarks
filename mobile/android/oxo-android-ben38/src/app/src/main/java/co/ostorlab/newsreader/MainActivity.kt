package co.ostorlab.newsreader

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
        const val CHANNEL_ID = "news_channel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        val notificationBell = findViewById<ImageButton>(R.id.btn_notification)
        notificationBell.setOnClickListener {
            try {
                showArticleNotification()
                Toast.makeText(this, "New article notification sent!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        val profileButton = findViewById<Button>(R.id.btn_profile)
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        val settingsButton = findViewById<Button>(R.id.btn_settings)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        val aboutButton = findViewById<Button>(R.id.btn_about)
        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun showArticleNotification() {
        val notificationIntent = Intent("com.newsreader.OPEN_ARTICLE")
        notificationIntent.putExtra("user_token", "sensitive_jwt_token_12345")
        notificationIntent.putExtra("user_id", "user_001")
        
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New Article Available")
            .setContentText("Tap to read your personalized content")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(this)
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(1001, notification)
            } else {
                Toast.makeText(this, "Notifications not enabled", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Notification permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "News Updates"
            val descriptionText = "Channel for news article notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

package co.ostorlab.myshopify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

class ExternalAuthLoginActivity : Activity() {
    // Generate a dynamic session cookie
    private val sessionCookie: String
        get() {
            val timestamp = System.currentTimeMillis()
            val randomValue = (1000000000..9999999999).random()
            return "SESSION_COOKIE=${timestamp}_$randomValue"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get redirect URL from Intent
        val redirectUrl = intent.getStringExtra("redirectUrl")

        if (redirectUrl != null) {
            // Launch the URL with the session cookie appended
            launchCustomTabs(redirectUrl)
        } else {
            Log.e("VulnerableApp", "No redirectUrl provided")
        }

        // Close activity
        finish()
    }

    private fun launchCustomTabs(url: String) {
        val urlWithCookie = "$url?cookie=$sessionCookie"

        // Log for demonstration
        Log.i("VulnerableApp", "Launching custom tab with URL: $urlWithCookie")

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(urlWithCookie)
        }

        // Start external browser or custom tab
        startActivity(intent)
    }
}

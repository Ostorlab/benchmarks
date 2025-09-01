package com.example.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val msg = intent.getStringExtra("message") ?: "(no message)"
        val prefs = context.getSharedPreferences("secrets", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("last_incoming_message", msg)
            .apply()

        Toast.makeText(
            context,
            "='$msg'",
            Toast.LENGTH_LONG
        ).show()
    }
}

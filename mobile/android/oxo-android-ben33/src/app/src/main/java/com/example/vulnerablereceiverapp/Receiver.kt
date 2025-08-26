package com.example.receiverapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // No origin checks, no permission checks, trusts all extras blindly
        val msg = intent.getStringExtra("message") ?: "(no message)"
        val amount = intent.getIntExtra("amount", 0)

        // Simulate a sensitive action (e.g., toggling a feature / writing state)
        // Here we store attacker-controlled data in app prefs.
        val prefs = context.getSharedPreferences("secrets", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("last_incoming_message", msg)
            .putInt("last_amount", amount)
            .apply()

        // Visible side-effect for demo
        Toast.makeText(
            context,
            "CriticalReceiver ran: message='$msg', amount=$amount",
            Toast.LENGTH_LONG
        ).show()
    }
}

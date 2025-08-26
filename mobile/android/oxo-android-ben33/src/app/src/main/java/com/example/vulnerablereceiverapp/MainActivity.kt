package com.example.receiverapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSelfTrigger: Button = findViewById(R.id.btnSelfTrigger)
        btnSelfTrigger.setOnClickListener {
            val i = Intent("com.example.receiverapp.TRIGGER").apply {
                setPackage(packageName)  // <-- Make it explicit to your app
                putExtra("message", "Hello from inside the app")
                putExtra("amount", 9999)
            }
            sendBroadcast(i)
        }
    }
}

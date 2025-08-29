package com.example.broadcastreceiver

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSelfTrigger: Button = findViewById(R.id.btnSelfTrigger)
        btnSelfTrigger.setOnClickListener {
            System.out.println("ggggggggg")
            val i = Intent("com.example.receiverapp.TRIGGER").apply {
                setPackage(packageName)
                putExtra("message", "Hello from inside the app")
            }
            sendBroadcast(i)
        }
    }
}

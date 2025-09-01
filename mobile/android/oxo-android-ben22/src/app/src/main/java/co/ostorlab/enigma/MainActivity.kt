package co.ostorlab.enigma

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.app.Activity

class MainActivity : Activity() {

    private lateinit var secretEditText: EditText
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        val greetingText = findViewById<TextView>(R.id.greetingText)
        secretEditText = findViewById(R.id.secretEditText)
        resetButton = findViewById(R.id.resetButton)

        // Set greeting message
        greetingText.text = "Hello Android!"

        // Set up reset button
        resetButton.filterTouchesWhenObscured = true
        resetButton.setOnClickListener {
            secretEditText.text.clear()
        }
    }
}

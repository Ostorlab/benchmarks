package com.example.vulnerablelogger;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText editTextEmail;
    Button buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonReset = findViewById(R.id.buttonReset);

        buttonReset.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();

            // --- SCENARIO: LEAK PII FROM A SENSITIVE WORKFLOW ---
            Log.e("VulnerableApp-Reset", "Password reset requested for email: " + email);

            Toast.makeText(this, "Reset link sent (simulation)", Toast.LENGTH_SHORT).show();
            // Close this screen and go back to the login screen
            finish();
        });
    }
}
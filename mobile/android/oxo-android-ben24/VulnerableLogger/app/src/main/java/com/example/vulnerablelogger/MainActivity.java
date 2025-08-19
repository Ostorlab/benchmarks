package com.example.vulnerablelogger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    Button buttonLogin;
    TextView textForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- SCENARIO: LEAK DATA ON SCREEN LOAD ---
        String deviceId = "dummy-device-id-12345";
        Log.w("VulnerableApp", "MainActivity created. Device ID loaded: " + deviceId);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textForgotPassword = findViewById(R.id.textForgotPassword);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            String sessionToken = "BX4DS-JFD4D-KDNG8-4JDL4 " + username;

            // --- SCENARIO: LEAK DATA WITH DIFFERENT LOG LEVELS ---
            Log.d("VulnerableApp", "DEBUG: Login attempt with password: " + password);
            Log.i("VulnerableApp", "INFO: User session token generated: " + sessionToken);
            Log.e("VulnerableApp", "ERROR: Password for user may be weak: " + password);

            Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
            // Pass the username to the next screen
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        textForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // --- SCENARIO: LEAK DATA AS THE USER TYPES ---
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("VulnerableApp-Keystroke", "User is typing password: " + s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
package com.example.vulnerablelogger;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // --- THIS IS THE VULNERABLE LINE ---
                Log.d("VulnerableApp", "Login attempt with username: " + username + " and password: " + password);

                // --- NEW CODE TO NAVIGATE TO THE NEXT SCREEN ---
                // Create an Intent to open LoggedInActivity
                Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
                // Execute the Intent
                startActivity(intent);
            }
        });
    }
}

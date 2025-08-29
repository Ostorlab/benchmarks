package com.securenotes.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEdit, passwordEdit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        usernameEdit = findViewById(R.id.usernameEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        Button loginBtn = findViewById(R.id.loginBtn);
        Button registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (dbHelper.validateUser(username, password)) {
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    prefs.edit().putString("username", username).putString("password", password).apply();

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, NotesListActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    if (dbHelper.addUser(username, password)) {
                        Toast.makeText(LoginActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
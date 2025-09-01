package com.docuview.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private Button guestButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        
        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        guestButton = findViewById(R.id.guestButton);

        if (prefs.getBoolean("is_logged_in", false)) {
            startDocumentList();
            return;
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAsGuest();
            }
        });
    }

    private void performLogin() {
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("session_token", "tok_" + System.currentTimeMillis());
        editor.putBoolean("is_logged_in", true);
        editor.putBoolean("is_guest", false);
        editor.apply();

        startDocumentList();
    }

    private void loginAsGuest() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", "guest");
        editor.putBoolean("is_logged_in", true);
        editor.putBoolean("is_guest", true);
        editor.apply();

        startDocumentList();
    }

    private void startDocumentList() {
        Intent intent = new Intent(this, DocumentListActivity.class);
        startActivity(intent);
        finish();
    }
}

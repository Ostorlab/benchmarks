package com.ostorlab.your.openai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText tokenEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenEditText = findViewById(R.id.tokenEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(view -> {
            String token = tokenEditText.getText().toString().trim();
            if (!token.isEmpty()) {
                saveTokenToAppExternalStorage(token);
            } else {
                Toast.makeText(LoginActivity.this, "Please enter the OpenAI token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTokenToAppExternalStorage(String token) {
        File dir = getExternalFilesDir(null); // App-private external storage
        if (dir == null) {
            Toast.makeText(this, "Cannot access external storage", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(dir, "openai_token.txt");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(token.getBytes());
            Toast.makeText(this, "Token saved successfully", Toast.LENGTH_SHORT).show();

            // Move to PromptActivity
            Intent intent = new Intent(LoginActivity.this, PromptActivity.class);
            startActivity(intent);
            finish();

        } catch (IOException e) {
            Toast.makeText(this, "Failed to save token: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

package com.securebank.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameText;
    private EditText emailField;
    private EditText phoneField;
    private EditText addressField;
    private Button updateButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameText = findViewById(R.id.usernameText);
        emailField = findViewById(R.id.emailField);
        phoneField = findViewById(R.id.phoneField);
        addressField = findViewById(R.id.addressField);
        updateButton = findViewById(R.id.updateButton);
        backButton = findViewById(R.id.backButton);

        loadProfileData();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("bank_data", MODE_PRIVATE);
        String username = prefs.getString("logged_in_user", "User");
        String email = prefs.getString("user_email", "user@example.com");
        String phone = prefs.getString("user_phone", "+1 (555) 123-4567");
        String address = prefs.getString("user_address", "123 Main St, Anytown, USA");

        usernameText.setText("Username: " + username);
        emailField.setText(email);
        phoneField.setText(phone);
        addressField.setText(address);
    }

    private void saveProfileData() {
        SharedPreferences prefs = getSharedPreferences("bank_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("user_email", emailField.getText().toString());
        editor.putString("user_phone", phoneField.getText().toString());
        editor.putString("user_address", addressField.getText().toString());
        editor.apply();

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }
}

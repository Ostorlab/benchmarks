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
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);

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
        String username = prefs.getString("logged_in_user", "");

        if (!username.isEmpty()) {
            User user = databaseHelper.getUserByUsername(username);
            if (user != null) {
                usernameText.setText("Username: " + user.getUsername());
                emailField.setText(user.getEmail());
                phoneField.setText(user.getPhone());
                addressField.setText(user.getAddress());

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_email", user.getEmail());
                editor.putString("user_phone", user.getPhone());
                editor.putString("user_address", user.getAddress());
                editor.apply();
            }
        } else {
            usernameText.setText("Username: User");
            emailField.setText("user@example.com");
            phoneField.setText("+1 (555) 123-4567");
            addressField.setText("123 Main St, Anytown, USA");
        }
    }

    private void saveProfileData() {
        SharedPreferences prefs = getSharedPreferences("bank_data", MODE_PRIVATE);
        String username = prefs.getString("logged_in_user", "");

        if (username.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = emailField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String address = addressField.getText().toString().trim();

        if (email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.updateUserProfile(username, email, phone, address)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_email", email);
            editor.putString("user_phone", phone);
            editor.putString("user_address", address);
            editor.apply();

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.myapplication3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameText;
    private TextView userEmailText;
    private TextView memberSinceText;
    private Button editProfileButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        loadUserProfile();
        setupClickListeners();
    }

    private void initializeViews() {
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        memberSinceText = findViewById(R.id.memberSinceText);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void loadUserProfile() {
        // Simulate loading user data
        userNameText.setText("John Doe");
        userEmailText.setText("john.doe@example.com");
        memberSinceText.setText("Member since: January 2024");
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
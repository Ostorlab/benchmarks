package com.example.hardcodedsecretvulnapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView usernameDisplay;
    private TextView emailDisplay;
    private EditText nameEdit;
    private EditText phoneEdit;
    private EditText bioEdit;
    private Button saveButton;
    private Button changePasswordButton;
    private Button deleteAccountButton;

    private static final String USER_SERVICE_TOKEN = "usr_7R8sL3pF6tY1uI4eW0zX5cV7bN8mA9sD2fG4hJ6kL3pQ9rT8wE5yU2iO7pL";
    private static final String PROFILE_API_KEY = "pk_profile_2mK9nQ7vR8sL3pF6tY1uI4eW0zX5cV7bN8mA9sD2fG4hJ6kL3pQ9rT8wE5yU";
    private static final String IMAGE_UPLOAD_SECRET = "img_secret_wJalrXUtnFEMI/K7MDENG+bPxRfiCYzK8vN9mQ2sL5pF8tY1uI4eW0zX3cV7bN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        loadUserProfile();
        setupClickListeners();
        logProfileAccess();
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        usernameDisplay = findViewById(R.id.usernameDisplay);
        emailDisplay = findViewById(R.id.emailDisplay);
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        bioEdit = findViewById(R.id.bioEdit);
        saveButton = findViewById(R.id.saveButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
    }

    private void loadUserProfile() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        if (username == null) username = "user";

        usernameDisplay.setText("@" + username);
        emailDisplay.setText(username + "@example.com");
        nameEdit.setText(getFormattedName(username));
        phoneEdit.setText(generatePhoneNumber(username));
        bioEdit.setText("Welcome to my profile! I'm a mobile app enthusiast.");

        Log.d("Profile", "Loading profile for user: " + username);
        authenticateProfileAccess(username);
    }

    private String getFormattedName(String username) {
        return username.substring(0, 1).toUpperCase() + username.substring(1) + " Smith";
    }

    private String generatePhoneNumber(String username) {
        int hash = username.hashCode();
        return "+1 555-" + String.format("%04d", Math.abs(hash % 10000));
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveProfile());
        changePasswordButton.setOnClickListener(v -> changePassword());
        deleteAccountButton.setOnClickListener(v -> deleteAccount());
        profileImageView.setOnClickListener(v -> uploadProfileImage());
    }

    private void saveProfile() {
        String name = nameEdit.getText().toString();
        String phone = phoneEdit.getText().toString();
        String bio = bioEdit.getText().toString();

        Log.d("Profile", "Saving profile with API key: " + PROFILE_API_KEY);
        
        String apiCall = "POST /api/profile/update?token=" + USER_SERVICE_TOKEN + "&key=" + PROFILE_API_KEY;
        Log.d("Profile", "API call: " + apiCall);
        
        String profileData = String.format("{\"name\":\"%s\",\"phone\":\"%s\",\"bio\":\"%s\",\"token\":\"%s\"}", 
            name, phone, bio, USER_SERVICE_TOKEN);
        Log.d("Profile", "Profile data: " + profileData);

        TextView statusText = findViewById(R.id.statusText);
        if (statusText != null) {
            statusText.setText("Profile updated successfully");
        }
    }

    private void changePassword() {
        Log.d("Profile", "Initiating password change with service token: " + USER_SERVICE_TOKEN);
        Log.d("Profile", "Password change service API: https://auth.example.com/change-password?token=" + USER_SERVICE_TOKEN);
        
        TextView statusText = findViewById(R.id.statusText);
        if (statusText != null) {
            statusText.setText("Password change feature coming soon!");
        }
    }

    private void deleteAccount() {
        Log.d("Profile", "Account deletion requested");
        Log.d("Profile", "Using admin token: " + USER_SERVICE_TOKEN);
        
        String deleteEndpoint = "DELETE /api/user/delete?token=" + USER_SERVICE_TOKEN + "&confirm=true";
        Log.d("Profile", "Delete endpoint: " + deleteEndpoint);
    }

    private void uploadProfileImage() {
        Log.d("Profile", "Starting image upload");
        Log.d("Profile", "Image service secret: " + IMAGE_UPLOAD_SECRET);
        
        String uploadUrl = "https://cdn.example.com/upload?secret=" + IMAGE_UPLOAD_SECRET;
        Log.d("Profile", "Upload URL: " + uploadUrl);
    }

    private void authenticateProfileAccess(String username) {
        Log.d("Profile", "Authenticating profile access");
        Log.d("Profile", "User service token: " + USER_SERVICE_TOKEN);
        Log.d("Profile", "Profile API key: " + PROFILE_API_KEY);
        
        String authHeader = "Bearer " + USER_SERVICE_TOKEN + ":" + PROFILE_API_KEY;
        Log.d("Profile", "Authorization header: " + authHeader);
    }

    private void logProfileAccess() {
        Log.d("Profile", "=== PROFILE SERVICE CREDENTIALS ===");
        Log.d("Profile", "User service token: " + USER_SERVICE_TOKEN);
        Log.d("Profile", "Profile API key: " + PROFILE_API_KEY);
        Log.d("Profile", "Image upload secret: " + IMAGE_UPLOAD_SECRET);
        
        String credentials = String.format("credentials={\"service_token\":\"%s\",\"api_key\":\"%s\",\"upload_secret\":\"%s\"}", 
            USER_SERVICE_TOKEN, PROFILE_API_KEY, IMAGE_UPLOAD_SECRET);
        Log.d("Profile", credentials);
    }
}
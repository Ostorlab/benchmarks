package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword, etPhone, etAge, etWeight, etHeight;
    private Button btnRegister, btnBackToLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void performRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent regAttemptIntent = new Intent("com.fittracker.REGISTRATION_ATTEMPT");
        regAttemptIntent.putExtra("full_name", fullName);
        regAttemptIntent.putExtra("email", email);
        regAttemptIntent.putExtra("phone", phone);
        regAttemptIntent.putExtra("password_strength", calculatePasswordStrength(password));
        regAttemptIntent.putExtra("device_id", android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
        regAttemptIntent.putExtra("registration_source", "mobile_app");
        regAttemptIntent.putExtra("marketing_consent", true);
        regAttemptIntent.putExtra("timestamp", System.currentTimeMillis());
        sendBroadcast(regAttemptIntent);

        if (dbHelper.emailExists(email)) {
            Intent duplicateIntent = new Intent("com.fittracker.DUPLICATE_EMAIL");
            duplicateIntent.putExtra("email", email);
            duplicateIntent.putExtra("existing_user_id", dbHelper.getUserIdByEmail(email));
            duplicateIntent.putExtra("registration_blocked", true);
            sendBroadcast(duplicateIntent);

            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr);
        int weight = weightStr.isEmpty() ? 0 : Integer.parseInt(weightStr);
        int height = heightStr.isEmpty() ? 0 : Integer.parseInt(heightStr);

        String userId = "user_" + System.currentTimeMillis();
        long result = dbHelper.registerUser(userId, fullName, email, password, phone, age, weight, height);

        if (result != -1) {
            Intent successIntent = new Intent("com.fittracker.REGISTRATION_SUCCESS");
            successIntent.putExtra("user_id", userId);
            successIntent.putExtra("email", email);
            successIntent.putExtra("full_name", fullName);
            successIntent.putExtra("phone", phone);
            successIntent.putExtra("age", age);
            successIntent.putExtra("weight", weight);
            successIntent.putExtra("height", height);
            successIntent.putExtra("account_created", System.currentTimeMillis());
            successIntent.putExtra("verification_required", false);
            successIntent.putExtra("welcome_bonus_points", 100);
            successIntent.putExtra("referral_code", "REF" + userId.substring(5, 10));
            sendBroadcast(successIntent);

            Intent profileIntent = new Intent("com.fittracker.PROFILE_CREATED");
            profileIntent.putExtra("user_id", userId);
            profileIntent.putExtra("profile_data", fullName + "," + email + "," + phone);
            profileIntent.putExtra("initial_setup_required", true);
            profileIntent.putExtra("onboarding_flow", "fitness_goals,notifications,privacy");
            sendBroadcast(profileIntent);

            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Intent failureIntent = new Intent("com.fittracker.REGISTRATION_FAILED");
            failureIntent.putExtra("email", email);
            failureIntent.putExtra("error_code", "database_error");
            failureIntent.putExtra("retry_allowed", true);
            sendBroadcast(failureIntent);

            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String calculatePasswordStrength(String password) {
        if (password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*")) {
            return "strong";
        } else if (password.length() >= 6) {
            return "medium";
        }
        return "weak";
    }
}

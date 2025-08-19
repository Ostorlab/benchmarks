package com.atlanticbank.mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPhone;
    private EditText editTextSSN;
    private EditText editTextPIN;
    private Button buttonSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Initialize all the UI components
     */
    private void initializeViews() {
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextSSN = findViewById(R.id.editTextSSN);
        editTextPIN = findViewById(R.id.editTextPIN);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);
    }

    /**
     * Set up click listeners
     */
    private void setupClickListeners() {
        buttonSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });
    }

    /**
     * THE VULNERABILITY: Save profile changes without authentication
     */
    private void saveProfileChanges() {
        // Get all the entered data
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String ssn = editTextSSN.getText().toString().trim();
        String pin = editTextPIN.getText().toString().trim();

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "First and last name are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, "Valid email address is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ssn.length() < 9) {
            Toast.makeText(this, "Valid SSN is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pin.length() != 4) {
            Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // THE VULNERABILITY: No authentication check before saving sensitive data
        showSaveConfirmation(firstName, lastName, email, phone, ssn, pin);
    }

    /**
     * Show confirmation dialog before saving changes
     */
    private void showSaveConfirmation(String firstName, String lastName, String email,
                                      String phone, String ssn, String pin) {
        String message = "Profile Update Summary:\n\n" +
                "Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n" +
                "Phone: " + phone + "\n" +
                "SSN: " + ssn + "\n" +
                "PIN: " + pin + "\n\n" +
                "Save these changes to your profile?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Profile Update")
                .setMessage(message)
                .setPositiveButton("SAVE CHANGES", (dialog, which) -> {
                    executeSaveProfile(firstName, lastName, email, phone, ssn, pin);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Execute the profile save (simulation)
     */
    private void executeSaveProfile(String firstName, String lastName, String email,
                                    String phone, String ssn, String pin) {
        Toast.makeText(this, "Saving profile changes...", Toast.LENGTH_SHORT).show();

        // Simulate saving to database
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String successMessage = "Profile Updated Successfully!\n\n" +
                    "Your information has been saved:\n" +
                    "• Name: " + firstName + " " + lastName + "\n" +
                    "• Email: " + email + "\n" +
                    "• Phone: " + phone + "\n" +
                    "• SSN: " + ssn + "\n" +
                    "• PIN: " + pin;

            new AlertDialog.Builder(this)
                    .setTitle("Profile Update Complete")
                    .setMessage(successMessage)
                    .setPositiveButton("OK", null)
                    .show();

            Toast.makeText(this, "Profile information updated successfully", Toast.LENGTH_SHORT).show();

        }, 1500);
    }

    /**
     * THE VULNERABILITY: Process external data without validation
     * This shows how external apps could inject malicious data
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Accept data from intent extras without validation
        if (getIntent().hasExtra("malicious_ssn")) {
            String maliciousSSN = getIntent().getStringExtra("malicious_ssn");
            editTextSSN.setText(maliciousSSN);
        }

        if (getIntent().hasExtra("malicious_email")) {
            String maliciousEmail = getIntent().getStringExtra("malicious_email");
            editTextEmail.setText(maliciousEmail);
        }

        if (getIntent().hasExtra("malicious_pin")) {
            String maliciousPin = getIntent().getStringExtra("malicious_pin");
            editTextPIN.setText(maliciousPin);
        }

        if (getIntent().hasExtra("first_name")) {
            String firstName = getIntent().getStringExtra("first_name");
            editTextFirstName.setText(firstName);
        }

        if (getIntent().hasExtra("last_name")) {
            String lastName = getIntent().getStringExtra("last_name");
            editTextLastName.setText(lastName);
        }
    }
}
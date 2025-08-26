package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadUserProfile();
    }

    private void loadUserProfile() {
        Intent profileIntent = new Intent("com.fittracker.PROFILE_LOADED");
        profileIntent.putExtra("user_id", "user_12345");
        profileIntent.putExtra("full_name", "John Doe");
        profileIntent.putExtra("email", "john.doe@email.com");
        profileIntent.putExtra("phone", "+1-555-0123");
        profileIntent.putExtra("date_of_birth", "1995-03-15");
        profileIntent.putExtra("gender", "male");
        profileIntent.putExtra("weight", 75);
        profileIntent.putExtra("height", 180);
        profileIntent.putExtra("medical_conditions", "none");
        profileIntent.putExtra("emergency_contact", "Jane Doe +1-555-0124");
        profileIntent.putExtra("insurance_number", "INS123456789");
        profileIntent.putExtra("billing_address", "123 Main St, NYC, NY 10001");
        profileIntent.putExtra("payment_method", "****1234");
        sendBroadcast(profileIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent sessionIntent = new Intent("com.fittracker.PROFILE_SESSION");
        sessionIntent.putExtra("user_id", "user_12345");
        sessionIntent.putExtra("session_end", System.currentTimeMillis());
        sessionIntent.putExtra("profile_changes", "none");
        sessionIntent.putExtra("time_spent", 120);
        sendBroadcast(sessionIntent);
    }
}

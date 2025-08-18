package com.ostorlab.insecure.storage.sharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileInfo = findViewById(R.id.profile_info);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = sharedPreferences.getString("email", "user@example.com");

        String profileText = "User Profile:\nName: John Doe\nEmail: " + email;
        profileInfo.setText(profileText);
    }
}

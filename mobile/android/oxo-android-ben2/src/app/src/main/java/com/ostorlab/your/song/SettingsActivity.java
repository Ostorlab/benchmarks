package com.ostorlab.your.song;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat notificationsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationsSwitch = findViewById(R.id.notifications_switch);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Load saved preference
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", false);
        notificationsSwitch.setChecked(notificationsEnabled);

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
        });
    }
}
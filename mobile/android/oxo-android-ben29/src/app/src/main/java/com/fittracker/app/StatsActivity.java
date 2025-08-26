package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadUserStatistics();
    }

    private void loadUserStatistics() {
        Intent statsIntent = new Intent("com.fittracker.STATS_ACCESSED");
        statsIntent.putExtra("user_id", "user_12345");
        statsIntent.putExtra("total_workouts", 25);
        statsIntent.putExtra("total_distance", 150.5);
        statsIntent.putExtra("total_calories", 12500);
        statsIntent.putExtra("weekly_goal_progress", 80);
        statsIntent.putExtra("access_timestamp", System.currentTimeMillis());
        statsIntent.putExtra("user_preferences", "metric_units,public_profile");
        statsIntent.putExtra("subscription_type", "premium");
        statsIntent.putExtra("last_login", "2024-01-15 14:30:00");
        sendBroadcast(statsIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent viewIntent = new Intent("com.fittracker.SCREEN_VIEWED");
        viewIntent.putExtra("screen_name", "statistics");
        viewIntent.putExtra("user_id", "user_12345");
        viewIntent.putExtra("session_duration", 45);
        viewIntent.putExtra("previous_screen", "main_menu");
        sendBroadcast(viewIntent);
    }
}

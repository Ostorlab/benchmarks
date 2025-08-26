package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WorkoutActivity extends AppCompatActivity {

    private TextView tvCalories, tvDistance, tvTime;
    private int calories = 0;
    private double distance = 0.0;
    private int seconds = 0;
    private Handler handler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        tvCalories = findViewById(R.id.tvCalories);
        tvDistance = findViewById(R.id.tvDistance);
        tvTime = findViewById(R.id.tvTime);
        Button btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        Button btnShareProgress = findViewById(R.id.btnShareProgress);
        Button btnBack = findViewById(R.id.btnBack);

        startWorkoutTimer();

        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkoutData();
            }
        });

        btnShareProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWorkoutProgress();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startWorkoutTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                seconds++;
                calories += 2;
                distance += 0.05;

                updateDisplay();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timerRunnable);
    }

    private void updateDisplay() {
        tvCalories.setText("Calories Burned: " + calories);
        tvDistance.setText(String.format("Distance: %.2f km", distance));

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        tvTime.setText(String.format("Time: %02d:%02d:%02d", hours, minutes, secs));
    }

    private void saveWorkoutData() {
        Intent broadcastIntent = new Intent("com.fittracker.WORKOUT_SAVED");
        broadcastIntent.putExtra("user_id", "user_12345");
        broadcastIntent.putExtra("workout_type", "running");
        broadcastIntent.putExtra("calories", calories);
        broadcastIntent.putExtra("distance", distance);
        broadcastIntent.putExtra("duration", seconds);
        broadcastIntent.putExtra("heart_rate", "145 bpm");
        broadcastIntent.putExtra("location", "Central Park, NYC");
        broadcastIntent.putExtra("user_email", "john.doe@email.com");
        broadcastIntent.putExtra("session_token", "auth_token_xyz789");
        sendBroadcast(broadcastIntent);
    }

    private void shareWorkoutProgress() {
        Intent progressIntent = new Intent("com.fittracker.PROGRESS_SHARED");
        progressIntent.putExtra("user_id", "user_12345");
        progressIntent.putExtra("achievement", "Personal Best: " + String.format("%.2f km", distance));
        progressIntent.putExtra("calories_burned", calories);
        progressIntent.putExtra("workout_duration", seconds);
        progressIntent.putExtra("user_profile", "john.doe@email.com");
        progressIntent.putExtra("privacy_level", "public");
        progressIntent.putExtra("gps_coordinates", "40.7829,-73.9654");
        progressIntent.putExtra("device_id", "device_android_567890");
        sendBroadcast(progressIntent);

        Intent goalIntent = new Intent("com.fittracker.GOAL_UPDATE");
        goalIntent.putExtra("user_id", "user_12345");
        goalIntent.putExtra("weekly_progress", 75);
        goalIntent.putExtra("monthly_target", "100km");
        goalIntent.putExtra("current_streak", 12);
        goalIntent.putExtra("user_weight", "75kg");
        goalIntent.putExtra("user_age", 28);
        goalIntent.putExtra("health_data", "BP: 120/80, HR: 65");
        sendBroadcast(goalIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }
}

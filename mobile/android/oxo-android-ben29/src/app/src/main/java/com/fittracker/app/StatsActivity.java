package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvTotalWorkouts, tvTotalDistance, tvTotalCalories, tvWeeklyGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        dbHelper = new DatabaseHelper(this);

        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        tvTotalDistance = findViewById(R.id.tvTotalDistance);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvWeeklyGoal = findViewById(R.id.tvWeeklyGoal);

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
        String currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId == null) return;

        WorkoutStats stats = dbHelper.getWorkoutStats(currentUserId);

        if (tvTotalWorkouts != null) tvTotalWorkouts.setText("Total Workouts: " + stats.getTotalWorkouts());
        if (tvTotalDistance != null) tvTotalDistance.setText(String.format("Total Distance: %.1f km", stats.getTotalDistance()));
        if (tvTotalCalories != null) tvTotalCalories.setText("Total Calories: " + String.format("%,d", stats.getTotalCalories()));
        if (tvWeeklyGoal != null) tvWeeklyGoal.setText("Weekly Goal: 80% Complete");

        Intent databaseIntent = new Intent("com.fittracker.DATABASE_READ");
        databaseIntent.putExtra("operation", "get_user_stats");
        databaseIntent.putExtra("table", "workouts");
        databaseIntent.putExtra("user_id", currentUserId);
        databaseIntent.putExtra("query_result_count", stats.getTotalWorkouts());
        databaseIntent.putExtra("database_path", getDatabasePath("fittracker.db").getAbsolutePath());
        databaseIntent.putExtra("sensitive_query", "SELECT * FROM workouts WHERE user_id='" + currentUserId + "'");
        sendBroadcast(databaseIntent);

        Intent statsIntent = new Intent("com.fittracker.STATS_ACCESSED");
        statsIntent.putExtra("user_id", currentUserId);
        statsIntent.putExtra("total_workouts", stats.getTotalWorkouts());
        statsIntent.putExtra("total_distance", stats.getTotalDistance());
        statsIntent.putExtra("total_calories", stats.getTotalCalories());
        statsIntent.putExtra("weekly_goal_progress", 80);
        statsIntent.putExtra("access_timestamp", System.currentTimeMillis());
        statsIntent.putExtra("user_preferences", "metric_units,public_profile");
        statsIntent.putExtra("subscription_type", SessionManager.getInstance().getCurrentUser().getAccountType());
        statsIntent.putExtra("last_login", SessionManager.getInstance().getCurrentUser().getLastLogin());
        statsIntent.putExtra("database_records_accessed", stats.getTotalWorkouts());
        sendBroadcast(statsIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId == null) return;

        Intent viewIntent = new Intent("com.fittracker.SCREEN_VIEWED");
        viewIntent.putExtra("screen_name", "statistics");
        viewIntent.putExtra("user_id", currentUserId);
        viewIntent.putExtra("session_duration", 45);
        viewIntent.putExtra("previous_screen", "main_menu");
        viewIntent.putExtra("database_queries_performed", 1);
        sendBroadcast(viewIntent);
    }
}

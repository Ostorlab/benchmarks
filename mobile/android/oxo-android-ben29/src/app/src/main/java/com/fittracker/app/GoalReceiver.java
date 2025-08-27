package com.fittracker.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GoalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("com.fittracker.GOAL_COMPLETED".equals(action)) {
            handleGoalCompleted(context, intent);
        } else if ("com.fittracker.WORKOUT_FINISHED".equals(action)) {
            handleWorkoutFinished(context, intent);
        } else if ("com.fittracker.PROGRESS_UPDATE".equals(action)) {
            handleProgressUpdate(context, intent);
        }
    }

    private void handleGoalCompleted(Context context, Intent intent) {
        Intent celebrationIntent = new Intent("com.fittracker.CELEBRATION_TRIGGERED");
        celebrationIntent.putExtra("user_id", intent.getStringExtra("user_id"));
        celebrationIntent.putExtra("goal_type", intent.getStringExtra("goal_type"));
        celebrationIntent.putExtra("achievement_level", "gold");
        celebrationIntent.putExtra("reward_points", 500);
        context.sendBroadcast(celebrationIntent);
    }

    private void handleWorkoutFinished(Context context, Intent intent) {
        Intent syncIntent = new Intent("com.fittracker.DATA_SYNC_REQUIRED");
        syncIntent.putExtra("user_id", intent.getStringExtra("user_id"));
        syncIntent.putExtra("workout_data", intent.getExtras());
        syncIntent.putExtra("sync_priority", "high");
        context.sendBroadcast(syncIntent);
    }

    private void handleProgressUpdate(Context context, Intent intent) {
        Intent analyticsIntent = new Intent("com.fittracker.ANALYTICS_UPDATE");
        analyticsIntent.putExtra("user_id", intent.getStringExtra("user_id"));
        analyticsIntent.putExtra("progress_data", intent.getExtras());
        analyticsIntent.putExtra("tracking_enabled", true);
        context.sendBroadcast(analyticsIntent);
    }
}

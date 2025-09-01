package com.fittracker.app;

public class WorkoutStats {
    private int totalWorkouts;
    private int totalCalories;
    private double totalDistance;
    private int weeklyGoalProgress;

    public WorkoutStats() {}

    public int getTotalWorkouts() { return totalWorkouts; }
    public void setTotalWorkouts(int totalWorkouts) { this.totalWorkouts = totalWorkouts; }

    public int getTotalCalories() { return totalCalories; }
    public void setTotalCalories(int totalCalories) { this.totalCalories = totalCalories; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public int getWeeklyGoalProgress() { return weeklyGoalProgress; }
    public void setWeeklyGoalProgress(int weeklyGoalProgress) { this.weeklyGoalProgress = weeklyGoalProgress; }
}

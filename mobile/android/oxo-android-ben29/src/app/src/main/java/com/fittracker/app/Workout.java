package com.fittracker.app;

public class Workout {
    private String userId;
    private String workoutType;
    private int calories;
    private double distance;
    private int duration;
    private String heartRate;
    private String location;
    private String gpsCoords;
    private long timestamp;

    public Workout() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getWorkoutType() { return workoutType; }
    public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getHeartRate() { return heartRate; }
    public void setHeartRate(String heartRate) { this.heartRate = heartRate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getGpsCoords() { return gpsCoords; }
    public void setGpsCoords(String gpsCoords) { this.gpsCoords = gpsCoords; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

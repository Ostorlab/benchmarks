package com.fittracker.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import androidx.annotation.Nullable;

public class WorkoutService extends Service {

    private Handler handler = new Handler();
    private Runnable trackingRunnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationTracking();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationTracking() {
        trackingRunnable = new Runnable() {
            @Override
            public void run() {
                broadcastLocationUpdate();
                handler.postDelayed(this, 30000);
            }
        };
        handler.post(trackingRunnable);
    }

    private void broadcastLocationUpdate() {
        Intent locationIntent = new Intent("com.fittracker.LOCATION_UPDATE");
        locationIntent.putExtra("user_id", "user_12345");
        locationIntent.putExtra("latitude", 40.7829);
        locationIntent.putExtra("longitude", -73.9654);
        locationIntent.putExtra("accuracy", 5.2);
        locationIntent.putExtra("altitude", 15.7);
        locationIntent.putExtra("speed", 12.5);
        locationIntent.putExtra("timestamp", System.currentTimeMillis());
        locationIntent.putExtra("address", "Central Park, New York, NY");
        locationIntent.putExtra("device_id", "device_android_567890");
        sendBroadcast(locationIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (trackingRunnable != null) {
            handler.removeCallbacks(trackingRunnable);
        }
    }
}

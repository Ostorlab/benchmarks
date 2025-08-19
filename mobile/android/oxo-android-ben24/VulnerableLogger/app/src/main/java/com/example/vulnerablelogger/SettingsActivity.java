package com.example.vulnerablelogger;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switchAnalytics;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchAnalytics = findViewById(R.id.switchAnalytics);
        buttonBack = findViewById(R.id.buttonBack);

        switchAnalytics.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.w("VulnerableApp-Settings", "Analytics preference changed to: " + isChecked);
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
package com.documentmanager.pro;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        TextView welcomeText = findViewById(R.id.settings_welcome);
        Switch securitySwitch = findViewById(R.id.security_toggle);
        Switch syncSwitch = findViewById(R.id.sync_toggle);
        
        welcomeText.setText("Document Manager Settings");
        
        securitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Security settings logic would go here
        });
        
        syncSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Sync settings logic would go here
        });
    }
}
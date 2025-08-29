package com.docuview.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText serverUrlEdit;
    private CheckBox cacheEnabledCheck;
    private CheckBox analyticsEnabledCheck;
    private Button saveButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        
        serverUrlEdit = findViewById(R.id.serverUrl);
        cacheEnabledCheck = findViewById(R.id.cacheEnabled);
        analyticsEnabledCheck = findViewById(R.id.analyticsEnabled);
        saveButton = findViewById(R.id.saveButton);

        loadSettings();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void loadSettings() {
        serverUrlEdit.setText(prefs.getString("server_url", "https://api.docuview.com"));
        cacheEnabledCheck.setChecked(prefs.getBoolean("cache_enabled", true));
        analyticsEnabledCheck.setChecked(prefs.getBoolean("analytics_enabled", false));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("server_url", serverUrlEdit.getText().toString());
        editor.putBoolean("cache_enabled", cacheEnabledCheck.isChecked());
        editor.putBoolean("analytics_enabled", analyticsEnabledCheck.isChecked());
        editor.putString("last_updated", String.valueOf(System.currentTimeMillis()));
        editor.apply();

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }
}

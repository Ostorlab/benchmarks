package com.securenotes.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText serverEdit, portEdit, apiKeyEdit;
    private Switch syncSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        serverEdit = findViewById(R.id.serverEdit);
        portEdit = findViewById(R.id.portEdit);
        apiKeyEdit = findViewById(R.id.apiKeyEdit);
        syncSwitch = findViewById(R.id.syncSwitch);
        Button saveBtn = findViewById(R.id.saveSettingsBtn);

        loadSettings();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        serverEdit.setText(prefs.getString("server", "api.securenotes.com"));
        portEdit.setText(prefs.getString("port", "8443"));
        apiKeyEdit.setText(prefs.getString("api_key", ""));
        syncSwitch.setChecked(prefs.getBoolean("sync_enabled", true));
    }

    private void saveSettings() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        prefs.edit()
            .putString("server", serverEdit.getText().toString())
            .putString("port", portEdit.getText().toString())
            .putString("api_key", apiKeyEdit.getText().toString())
            .putBoolean("sync_enabled", syncSwitch.isChecked())
            .apply();
    }
}
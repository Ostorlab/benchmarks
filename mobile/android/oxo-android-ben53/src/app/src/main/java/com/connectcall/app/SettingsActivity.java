package com.connectcall.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private Switch notificationsSwitch;
    private Switch autoAnswerSwitch;
    private Switch hdVideoSwitch;
    private Spinner cameraSpinner;
    private Button saveSettingsButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        initializeViews();
        loadSettings();
    }

    private void initializeViews() {
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        autoAnswerSwitch = findViewById(R.id.autoAnswerSwitch);
        hdVideoSwitch = findViewById(R.id.hdVideoSwitch);
        cameraSpinner = findViewById(R.id.cameraSpinner);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);

        String[] cameraOptions = {"Front Camera", "Back Camera"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cameraOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cameraSpinner.setAdapter(adapter);

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void loadSettings() {
        notificationsSwitch.setChecked(preferences.getBoolean("notifications_enabled", true));
        autoAnswerSwitch.setChecked(preferences.getBoolean("auto_answer", false));
        hdVideoSwitch.setChecked(preferences.getBoolean("hd_video", true));
        cameraSpinner.setSelection(preferences.getInt("default_camera", 0));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notifications_enabled", notificationsSwitch.isChecked());
        editor.putBoolean("auto_answer", autoAnswerSwitch.isChecked());
        editor.putBoolean("hd_video", hdVideoSwitch.isChecked());
        editor.putInt("default_camera", cameraSpinner.getSelectedItemPosition());

        if (editor.commit()) {
            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving settings", Toast.LENGTH_SHORT).show();
        }
    }
}

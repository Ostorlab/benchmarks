package com.example.myapplication3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private Switch notificationSwitch;
    private Switch darkModeSwitch;
    private CheckBox autoSaveCheckBox;
    private CheckBox syncDataCheckBox;
    private RadioGroup languageGroup;
    private SeekBar volumeSeekBar;
    private TextView volumeText;
    
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        
        initializeViews();
        loadSettings();
        setupListeners();
    }

    private void initializeViews() {
        notificationSwitch = findViewById(R.id.notificationSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        autoSaveCheckBox = findViewById(R.id.autoSaveCheckBox);
        syncDataCheckBox = findViewById(R.id.syncDataCheckBox);
        languageGroup = findViewById(R.id.languageGroup);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        volumeText = findViewById(R.id.volumeText);
    }

    private void loadSettings() {
        notificationSwitch.setChecked(prefs.getBoolean("notifications", true));
        darkModeSwitch.setChecked(prefs.getBoolean("dark_mode", false));
        autoSaveCheckBox.setChecked(prefs.getBoolean("auto_save", true));
        syncDataCheckBox.setChecked(prefs.getBoolean("sync_data", false));
        
        int language = prefs.getInt("language", R.id.englishRadio);
        languageGroup.check(language);
        
        int volume = prefs.getInt("volume", 50);
        volumeSeekBar.setProgress(volume);
        volumeText.setText("Volume: " + volume + "%");
    }

    private void setupListeners() {
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("notifications", isChecked);
            Toast.makeText(this, "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("dark_mode", isChecked);
            Toast.makeText(this, "Dark mode " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        autoSaveCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("auto_save", isChecked);
        });

        syncDataCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("sync_data", isChecked);
        });

        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            savePreference("language", checkedId);
            RadioButton selectedRadio = findViewById(checkedId);
            Toast.makeText(this, "Language: " + selectedRadio.getText(), Toast.LENGTH_SHORT).show();
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volumeText.setText("Volume: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                savePreference("volume", seekBar.getProgress());
            }
        });
    }

    private void savePreference(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    private void savePreference(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }
}
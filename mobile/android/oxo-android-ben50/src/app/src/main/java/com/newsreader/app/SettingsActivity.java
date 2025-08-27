package com.newsreader.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private CheckBox notificationsCheckBox;
    private CheckBox autoRefreshCheckBox;
    private CheckBox darkModeCheckBox;
    private SeekBar refreshIntervalSeekBar;
    private TextView refreshIntervalTextView;
    private Spinner fontSizeSpinner;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");

        setupViews();
        loadSettings();
    }

    private void setupViews() {
        notificationsCheckBox = findViewById(R.id.notificationsCheckBox);
        autoRefreshCheckBox = findViewById(R.id.autoRefreshCheckBox);
        darkModeCheckBox = findViewById(R.id.darkModeCheckBox);
        refreshIntervalSeekBar = findViewById(R.id.refreshIntervalSeekBar);
        refreshIntervalTextView = findViewById(R.id.refreshIntervalTextView);
        fontSizeSpinner = findViewById(R.id.fontSizeSpinner);

        sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE);

        setupListeners();
    }

    private void setupListeners() {
        notificationsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBooleanSetting("notifications_enabled", isChecked);
            if (isChecked) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
        });

        autoRefreshCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBooleanSetting("auto_refresh_enabled", isChecked);
            refreshIntervalSeekBar.setEnabled(isChecked);
        });

        darkModeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveBooleanSetting("dark_mode_enabled", isChecked);
            Toast.makeText(this, "Dark mode " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        refreshIntervalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int minutes = (progress + 1) * 15;
                refreshIntervalTextView.setText(minutes + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int minutes = (seekBar.getProgress() + 1) * 15;
                saveIntSetting("refresh_interval", minutes);
            }
        });
    }

    private void loadSettings() {
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        boolean autoRefreshEnabled = sharedPreferences.getBoolean("auto_refresh_enabled", true);
        boolean darkModeEnabled = sharedPreferences.getBoolean("dark_mode_enabled", false);
        int refreshInterval = sharedPreferences.getInt("refresh_interval", 30);

        notificationsCheckBox.setChecked(notificationsEnabled);
        autoRefreshCheckBox.setChecked(autoRefreshEnabled);
        darkModeCheckBox.setChecked(darkModeEnabled);

        int seekBarProgress = (refreshInterval / 15) - 1;
        refreshIntervalSeekBar.setProgress(Math.max(0, seekBarProgress));
        refreshIntervalSeekBar.setEnabled(autoRefreshEnabled);
        refreshIntervalTextView.setText(refreshInterval + " minutes");
    }

    private void saveBooleanSetting(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void saveIntSetting(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

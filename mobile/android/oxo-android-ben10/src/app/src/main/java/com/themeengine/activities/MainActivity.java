package com.themeengine.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.themeengine.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        Button themeManagerBtn = findViewById(R.id.theme_manager_button);
        Button settingsBtn = findViewById(R.id.settings_button);
        Button storeBtn = findViewById(R.id.store_button);
        Button profileBtn = findViewById(R.id.profile_button);

        themeManagerBtn.setOnClickListener(v -> 
            startActivity(new Intent(this, ThemeManagerActivity.class)));
        
        settingsBtn.setOnClickListener(v -> 
            startActivity(new Intent(this, UserSettingsActivity.class)));
        
        storeBtn.setOnClickListener(v -> 
            startActivity(new Intent(this, ThemeStoreActivity.class)));
        
        profileBtn.setOnClickListener(v -> 
            startActivity(new Intent(this, UserProfileActivity.class)));
    }
}

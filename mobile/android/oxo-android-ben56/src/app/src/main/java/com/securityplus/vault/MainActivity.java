package com.securityplus.vault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SecureVaultPrefs";
    private static final String KEY_AUTHENTICATED = "is_authenticated";
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAuthenticationStatus();
            }
        }, SPLASH_DELAY);
    }
    
    private void checkAuthenticationStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isAuthenticated = prefs.getBoolean(KEY_AUTHENTICATED, false);
        
        Intent intent;
        if (isAuthenticated) {
            intent = new Intent(MainActivity.this, VaultActivity.class);
        } else {
            intent = new Intent(MainActivity.this, AuthActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}
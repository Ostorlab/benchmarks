package com.example.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
        
        setupMockApp();
    }
    
    private void setupMockApp() {
        androidx.constraintlayout.widget.ConstraintLayout constraintLayout = 
            (androidx.constraintlayout.widget.ConstraintLayout) findViewById(R.id.main);
        
        TextView welcomeText = new TextView(this);
        welcomeText.setText("Welcome to MyApp");
        welcomeText.setTextSize(20);
        welcomeText.setGravity(android.view.Gravity.CENTER);
        
        Button profileButton = new Button(this);
        profileButton.setText("View Profile");
        profileButton.setOnClickListener(v -> showProfile());
        
        Button settingsButton = new Button(this);
        settingsButton.setText("Settings");
        settingsButton.setOnClickListener(v -> showSettings());
        
        Button aboutButton = new Button(this);
        aboutButton.setText("About");
        aboutButton.setOnClickListener(v -> showAbout());
        
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams welcomeParams = 
            new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        welcomeParams.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        welcomeParams.leftToLeft = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        welcomeParams.rightToRight = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        welcomeParams.topMargin = 100;
        
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams profileParams = 
            new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        profileParams.topToBottom = welcomeText.getId();
        profileParams.leftToLeft = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        profileParams.rightToRight = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        profileParams.topMargin = 50;
        profileParams.leftMargin = 50;
        profileParams.rightMargin = 50;
        
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams settingsParams = 
            new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        settingsParams.topToBottom = profileButton.getId();
        settingsParams.leftToLeft = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        settingsParams.rightToRight = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        settingsParams.topMargin = 20;
        settingsParams.leftMargin = 50;
        settingsParams.rightMargin = 50;
        
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams aboutParams = 
            new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        aboutParams.topToBottom = settingsButton.getId();
        aboutParams.leftToLeft = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        aboutParams.rightToRight = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        aboutParams.topMargin = 20;
        aboutParams.leftMargin = 50;
        aboutParams.rightMargin = 50;
        
        welcomeText.setLayoutParams(welcomeParams);
        profileButton.setLayoutParams(profileParams);
        settingsButton.setLayoutParams(settingsParams);
        aboutButton.setLayoutParams(aboutParams);
        
        constraintLayout.addView(welcomeText);
        constraintLayout.addView(profileButton);
        constraintLayout.addView(settingsButton);
        constraintLayout.addView(aboutButton);
    }
    
    private void showProfile() {
        Toast.makeText(this, "Profile: Admin User\nEmail: admin@example.com\nStatus: Active", Toast.LENGTH_LONG).show();
    }
    
    private void showSettings() {
        Toast.makeText(this, "Settings: Theme, Language, Notifications", Toast.LENGTH_SHORT).show();
    }
    
    private void showAbout() {
        Toast.makeText(this, "MyApp v1.0\nA simple demo application", Toast.LENGTH_SHORT).show();
    }
}
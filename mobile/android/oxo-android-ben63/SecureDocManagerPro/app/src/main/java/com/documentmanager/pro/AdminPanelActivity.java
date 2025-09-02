package com.documentmanager.pro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    private TextView adminWelcome;
    private Button logoutButton;
    private Button manageUsersButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        prefs = getSharedPreferences("secure_docs", MODE_PRIVATE);
        
        adminWelcome = findViewById(R.id.admin_welcome);
        logoutButton = findViewById(R.id.logout_button);
        manageUsersButton = findViewById(R.id.manage_users_button);

        String username = prefs.getString("username", "User");
        adminWelcome.setText("Admin Panel - Welcome " + username);

        logoutButton.setOnClickListener(v -> performLogout());
        manageUsersButton.setOnClickListener(v -> 
            Toast.makeText(this, "User management features coming soon", Toast.LENGTH_SHORT).show());
    }

    private void performLogout() {
        prefs.edit().clear().apply();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
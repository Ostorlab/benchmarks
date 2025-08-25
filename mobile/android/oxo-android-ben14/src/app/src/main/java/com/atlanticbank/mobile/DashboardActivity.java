package com.atlanticbank.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button buttonTransferMoney;
    private Button buttonEditProfile;
    private Button buttonMakePayment;
    private Button buttonAdminPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI components
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Handle back button with new API
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(DashboardActivity.this, "Press home button to minimize app", Toast.LENGTH_SHORT).show();
            }
        });

        // Welcome message
        Toast.makeText(this, "Welcome to your AtlantaBank Dashboard", Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize all the UI components
     */
    private void initializeViews() {
        buttonTransferMoney = findViewById(R.id.buttonTransferMoney);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonMakePayment = findViewById(R.id.buttonMakePayment);
        buttonAdminPanel = findViewById(R.id.buttonAdminPanel);
    }

    /**
     * Set up click listeners for all dashboard buttons
     */
    private void setupClickListeners() {
        buttonTransferMoney.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransferActivity.class);
            startActivity(intent);
        });

        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileEditActivity.class);
            startActivity(intent);
        });

        buttonMakePayment.setOnClickListener(v -> {
            // Launch payment activity with some sample data
            Intent intent = new Intent(this, PaymentConfirmActivity.class);
            intent.putExtra("merchant", "Netflix Subscription");
            intent.putExtra("amount", "$15.99");
            intent.putExtra("payment_method", "****1234");
            startActivity(intent);
        });

        buttonAdminPanel.setOnClickListener(v -> {
            // In a real app, this would check admin permissions first
            Intent intent = new Intent(this, AdminPanelActivity.class);
            startActivity(intent);
        });
    }
}
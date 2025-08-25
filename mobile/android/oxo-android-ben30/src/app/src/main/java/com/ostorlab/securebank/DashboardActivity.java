package com.ostorlab.securebank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView balanceText;
    private CardView transferCard;
    private CardView paymentCard;
    private CardView supportCard;
    private Button logoutButton;

    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcome_text);
        balanceText = findViewById(R.id.balance_text);
        transferCard = findViewById(R.id.transfer_card);
        paymentCard = findViewById(R.id.payment_card);
        supportCard = findViewById(R.id.support_card);
        logoutButton = findViewById(R.id.logout_button);
    }

    private void loadUserData() {
        currentUser = getIntent().getStringExtra("username");
        if (currentUser != null) {
            welcomeText.setText("Welcome, " + currentUser);
        }

        // Simulate loading account balance
        balanceText.setText("Account Balance: $12,345.67");
    }

    private void setupClickListeners() {
        transferCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTransfer();
            }
        });

        paymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPayment();
            }
        });

        supportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSupport();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });
    }

    private void navigateToTransfer() {
        Intent intent = new Intent(this, TransferActivity.class);
        intent.putExtra("username", currentUser);
        intent.putExtra("balance", "12345.67");
        startActivity(intent);
    }

    private void navigateToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("username", currentUser);
        intent.putExtra("account_number", "1234567890");
        startActivity(intent);
    }

    private void navigateToSupport() {
        Intent intent = new Intent(this, SupportActivity.class);
        intent.putExtra("username", currentUser);
        intent.putExtra("session_id", "SES" + System.currentTimeMillis());
        startActivity(intent);
    }

    private void performLogout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to login without proper logout
        moveTaskToBack(true);
    }
}

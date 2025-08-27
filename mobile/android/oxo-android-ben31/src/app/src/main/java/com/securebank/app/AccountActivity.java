package com.securebank.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity {

    private TextView accountNumberText;
    private TextView accountTypeText;
    private TextView balanceText;
    private TextView holderNameText;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        accountNumberText = findViewById(R.id.accountNumberText);
        accountTypeText = findViewById(R.id.accountTypeText);
        balanceText = findViewById(R.id.balanceText);
        holderNameText = findViewById(R.id.holderNameText);
        backButton = findViewById(R.id.backButton);

        loadAccountInfo();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadAccountInfo() {
        SharedPreferences prefs = getSharedPreferences("bank_data", MODE_PRIVATE);
        String accountNumber = prefs.getString("account_number", "****-****-****-****");
        String balance = prefs.getString("current_balance", "0.00");
        String username = prefs.getString("logged_in_user", "Account Holder");

        accountNumberText.setText("Account Number: " + accountNumber);
        accountTypeText.setText("Account Type: Checking Account");
        balanceText.setText("Available Balance: $" + balance);
        holderNameText.setText("Account Holder: " + username.toUpperCase());
    }
}

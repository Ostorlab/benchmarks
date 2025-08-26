package com.securebank.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TransferActivity extends AppCompatActivity {

    private TextView currentBalanceText;
    private EditText recipientField;
    private EditText amountField;
    private Button transferButton;
    private Button backButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        databaseHelper = new DatabaseHelper(this);

        currentBalanceText = findViewById(R.id.currentBalanceText);
        recipientField = findViewById(R.id.recipientField);
        amountField = findViewById(R.id.amountField);
        transferButton = findViewById(R.id.transferButton);
        backButton = findViewById(R.id.backButton);

        loadBalance();

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = recipientField.getText().toString().trim();
                String amountStr = amountField.getText().toString().trim();

                if (recipient.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(TransferActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        Toast.makeText(TransferActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    processTransfer(recipient, amount);
                } catch (NumberFormatException e) {
                    Toast.makeText(TransferActivity.this, "Invalid amount format", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadBalance() {
        SharedPreferences prefs = getSharedPreferences("bank_data", MODE_PRIVATE);
        String username = prefs.getString("logged_in_user", "");

        if (!username.isEmpty()) {
            User user = databaseHelper.getUserByUsername(username);
            if (user != null) {
                String balance = String.format("%.2f", user.getBalance());
                currentBalanceText.setText("Current Balance: $" + balance);

                prefs.edit().putString("current_balance", balance).apply();
            }
        }
    }

    private void processTransfer(String recipient, double amount) {
        SharedPreferences prefs = getSharedPreferences("bank_data", MODE_PRIVATE);
        String username = prefs.getString("logged_in_user", "");

        if (username.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = databaseHelper.getUserByUsername(username);
        if (user != null) {
            double currentBalance = user.getBalance();

            if (amount <= currentBalance) {
                String description = "Transfer to " + recipient;

                if (databaseHelper.transferMoney(username, recipient, amount, description)) {
                    User updatedUser = databaseHelper.getUserByUsername(username);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("current_balance", String.format("%.2f", updatedUser.getBalance()));
                    editor.apply();

                    Toast.makeText(this, "Transfer successful! $" + String.format("%.2f", amount) + " sent to account " + recipient, Toast.LENGTH_LONG).show();
                    loadBalance();
                    recipientField.setText("");
                    amountField.setText("");
                } else {
                    User recipientUser = databaseHelper.getUserByAccountNumber(recipient);
                    if (recipientUser == null) {
                        Toast.makeText(this, "Recipient account not found. Please check the account number.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Transfer failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Insufficient funds", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error loading account information", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.ostorlab.securebank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TransferActivity extends AppCompatActivity {

    private TextView accountBalanceText;
    private EditText recipientAccountField;
    private EditText transferAmountField;
    private EditText transferMemoField;
    private Button transferButton;
    private Button backButton;

    private String currentUser;
    private String accountBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        initializeViews();
        loadTransferData();
        setupClickListeners();
    }

    private void initializeViews() {
        accountBalanceText = findViewById(R.id.account_balance_text);
        recipientAccountField = findViewById(R.id.recipient_account_field);
        transferAmountField = findViewById(R.id.transfer_amount_field);
        transferMemoField = findViewById(R.id.transfer_memo_field);
        transferButton = findViewById(R.id.transfer_button);
        backButton = findViewById(R.id.back_button);
    }

    private void loadTransferData() {
        currentUser = getIntent().getStringExtra("username");
        accountBalance = getIntent().getStringExtra("balance");

        if (accountBalance != null) {
            accountBalanceText.setText("Available Balance: $" + accountBalance);
        }
    }

    private void setupClickListeners() {
        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTransfer();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToDashboard();
            }
        });
    }

    private void processTransfer() {
        String recipientAccount = recipientAccountField.getText().toString().trim();
        String transferAmount = transferAmountField.getText().toString().trim();
        String memo = transferMemoField.getText().toString().trim();

        if (recipientAccount.isEmpty() || transferAmount.isEmpty()) {
            Toast.makeText(this, "Please fill in required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(transferAmount);
            double balance = Double.parseDouble(accountBalance);

            if (amount > balance) {
                Toast.makeText(this, "Insufficient funds", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate transfer processing
            simulateTransferProcess(recipientAccount, amount, memo);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    private void simulateTransferProcess(String recipient, double amount, String memo) {
        // Simulate network delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Toast.makeText(this, "Transfer of $" + amount + " to " + recipient + " completed", Toast.LENGTH_LONG).show();

        // Clear sensitive fields
        clearTransferFields();

        // Return to dashboard after successful transfer
        returnToDashboard();
    }

    private void clearTransferFields() {
        recipientAccountField.setText("");
        transferAmountField.setText("");
        transferMemoField.setText("");
    }

    private void returnToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("username", currentUser);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The activity becomes vulnerable when paused - sensitive data remains in memory
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Task can be hijacked here while sensitive transfer data is still loaded
    }
}

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        currentBalanceText = findViewById(R.id.currentBalanceText);
        recipientField = findViewById(R.id.recipientField);
        amountField = findViewById(R.id.amountField);
        transferButton = findViewById(R.id.transferButton);
        backButton = findViewById(R.id.backButton);

        loadBalance();

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = recipientField.getText().toString();
                String amountStr = amountField.getText().toString();

                if (!recipient.isEmpty() && !amountStr.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        processTransfer(recipient, amount);
                    } catch (NumberFormatException e) {
                        Toast.makeText(TransferActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransferActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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
        String balance = prefs.getString("current_balance", "0.00");
        currentBalanceText.setText("Current Balance: $" + balance);
    }

    private void processTransfer(String recipient, double amount) {
        SharedPreferences prefs = getSharedPreferences("bank_data", MODE_PRIVATE);
        String currentBalanceStr = prefs.getString("current_balance", "0.00").replace(",", "");

        try {
            double currentBalance = Double.parseDouble(currentBalanceStr);
            if (amount <= currentBalance) {
                double newBalance = currentBalance - amount;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current_balance", String.format("%.2f", newBalance));
                editor.apply();

                Toast.makeText(this, "Transfer successful! $" + amount + " sent to " + recipient, Toast.LENGTH_LONG).show();
                loadBalance();
                recipientField.setText("");
                amountField.setText("");
            } else {
                Toast.makeText(this, "Insufficient funds", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error processing transfer", Toast.LENGTH_SHORT).show();
        }
    }
}

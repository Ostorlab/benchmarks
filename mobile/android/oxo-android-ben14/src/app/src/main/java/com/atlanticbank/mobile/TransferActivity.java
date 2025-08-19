package com.atlanticbank.mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TransferActivity extends AppCompatActivity {

    private Spinner spinnerFromAccount;
    private EditText editTextToAccount;
    private EditText editTextAmount;
    private EditText editTextNote;
    private Button buttonTransfer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        // Initialize UI components
        initializeViews();

        // Set up the account spinner
        setupAccountSpinner();

        // Set up click listeners
        setupClickListeners();
    }

    /**
     * Initialize all the UI components
     */
    private void initializeViews() {
        spinnerFromAccount = findViewById(R.id.spinnerFromAccount);
        editTextToAccount = findViewById(R.id.editTextToAccount);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextNote = findViewById(R.id.editTextNote);
        buttonTransfer = findViewById(R.id.buttonTransfer);
    }

    /**
     * Set up the account spinner with demo account data
     */
    private void setupAccountSpinner() {
        String[] accounts = {
                "Checking Account (****1234) - $5,245.67",
                "Savings Account (****5678) - $12,890.34",
                "Business Account (****9012) - $45,123.89"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, accounts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFromAccount.setAdapter(adapter);
    }

    /**
     * Set up click listeners
     */
    private void setupClickListeners() {
        buttonTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTransfer();
            }
        });
    }

    /**
     * Process the money transfer
     * THE VULNERABILITY: No authentication check - any app can trigger transfers!
     */
    private void processTransfer() {
        String toAccount = editTextToAccount.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();
        String note = editTextNote.getText().toString().trim();

        // Basic validation
        if (toAccount.isEmpty()) {
            editTextToAccount.setError("Account number is required");
            return;
        }

        if (amountStr.isEmpty()) {
            editTextAmount.setError("Amount is required");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                editTextAmount.setError("Amount must be greater than 0");
                return;
            }

            if (amount > 50000) {
                editTextAmount.setError("Transfer limit exceeded ($50,000)");
                return;
            }

            // THE VULNERABILITY: Processing transfer without auth verification
            showTransferConfirmation(toAccount, amount, note);

        } catch (NumberFormatException e) {
            editTextAmount.setError("Invalid amount format");
        }
    }

    /**
     * Show transfer confirmation dialog
     */
    private void showTransferConfirmation(String toAccount, double amount, String note) {
        String message = "Transfer Details:\n\n" +
                "From: " + spinnerFromAccount.getSelectedItem().toString() + "\n" +
                "To: " + toAccount + "\n" +
                "Amount: $" + amount + "\n" +
                "Note: " + (note.isEmpty() ? "None" : note) + "\n\n" +
                "Confirm this transfer?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Transfer")
                .setMessage(message)
                .setPositiveButton("CONFIRM", (dialog, which) -> {
                    executeTransfer(toAccount, amount, note);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Execute the transfer (simulation)
     */
    private void executeTransfer(String toAccount, double amount, String note) {
        Toast.makeText(this, "Processing transfer...", Toast.LENGTH_SHORT).show();

        // Simulate processing time
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String successMessage = "Transfer Completed!\n\n" +
                    "Amount: $" + amount + "\n" +
                    "To Account: " + toAccount + "\n" +
                    "Transaction ID: TXN-" + (System.currentTimeMillis() % 1000000);

            new AlertDialog.Builder(this)
                    .setTitle("Transfer Successful")
                    .setMessage(successMessage)
                    .setPositiveButton("OK", null)
                    .show();

        }, 2000);
    }
}
package com.atlanticbank.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentConfirmActivity extends AppCompatActivity {

    private TextView textViewMerchant;
    private TextView textViewAmount;
    private TextView textViewPaymentMethod;
    private TextView textViewTransactionId;
    private Button buttonCancelPayment;
    private Button buttonConfirmPayment;

    // Payment data - can be injected by external apps
    private String merchant = "Amazon.com";
    private String amount = "$299.99";
    private String paymentMethod = "****1234";
    private String transactionId = "TXN-2024-8901234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirm);

        // Initialize UI components
        initializeViews();

        // THE VULNERABILITY: Process intent data without validation
        processIntentData();

        // Set up click listeners
        setupClickListeners();

        // Display payment data
        displayPaymentData();
    }

    /**
     * Initialize all the UI components
     */
    private void initializeViews() {
        textViewMerchant = findViewById(R.id.textViewMerchant);
        textViewAmount = findViewById(R.id.textViewAmount);
        textViewPaymentMethod = findViewById(R.id.textViewPaymentMethod);
        textViewTransactionId = findViewById(R.id.textViewTransactionId);
        buttonCancelPayment = findViewById(R.id.buttonCancelPayment);
        buttonConfirmPayment = findViewById(R.id.buttonConfirmPayment);
    }

    /**
     * THE VULNERABILITY: Accept payment data from external apps without validation
     */
    private void processIntentData() {
        Intent intent = getIntent();

        // Accept payment data from intent without proper validation
        if (intent.hasExtra("merchant")) {
            merchant = intent.getStringExtra("merchant");
        }

        if (intent.hasExtra("amount")) {
            amount = intent.getStringExtra("amount");
        }

        if (intent.hasExtra("payment_method")) {
            paymentMethod = intent.getStringExtra("payment_method");
        }

        if (intent.hasExtra("transaction_id")) {
            transactionId = intent.getStringExtra("transaction_id");
        }

        // Accept raw payment amounts - extremely dangerous
        if (intent.hasExtra("raw_amount")) {
            double rawAmount = intent.getDoubleExtra("raw_amount", 0.0);
            amount = "$" + rawAmount;
        }

        // Accept merchant URLs or other dangerous data
        if (intent.hasExtra("merchant_url")) {
            String merchantUrl = intent.getStringExtra("merchant_url");
            merchant = merchant + " (" + merchantUrl + ")";
        }
    }

    /**
     * Display payment data in the UI
     */
    private void displayPaymentData() {
        textViewMerchant.setText(merchant);
        textViewAmount.setText(amount);
        textViewPaymentMethod.setText(paymentMethod);
        textViewTransactionId.setText(transactionId);
    }

    /**
     * Set up click listeners
     */
    private void setupClickListeners() {
        buttonCancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPayment();
            }
        });

        buttonConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPayment();
            }
        });
    }

    /**
     * Cancel payment
     */
    private void cancelPayment() {
        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * THE VULNERABILITY: Process payments without authentication
     */
    private void confirmPayment() {
        // THE VULNERABILITY: No authentication check before processing payment!
        // In a secure app, this would verify:
        // 1. User is logged in and authorized
        // 2. Payment session is valid and secure
        // 3. Payment data hasn't been tampered with
        // 4. User has sufficient funds
        // 5. 2FA verification for large amounts

        showPaymentConfirmationDialog();
    }

    /**
     * Show payment confirmation dialog
     */
    private void showPaymentConfirmationDialog() {
        String message = "Payment Confirmation\n\n" +
                "Merchant: " + merchant + "\n" +
                "Amount: " + amount + "\n" +
                "Payment Method: " + paymentMethod + "\n" +
                "Transaction ID: " + transactionId + "\n\n" +
                "Proceed with this payment?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage(message)
                .setPositiveButton("CONFIRM PAYMENT", (dialog, which) -> {
                    executePayment();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Execute the payment (simulation)
     */
    private void executePayment() {
        Toast.makeText(this, "Processing payment...", Toast.LENGTH_SHORT).show();

        // Simulate payment processing
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String successMessage = "Payment Processed Successfully!\n\n" +
                    "Payment Details:\n" +
                    "• Merchant: " + merchant + "\n" +
                    "• Amount: " + amount + "\n" +
                    "• Method: " + paymentMethod + "\n" +
                    "• Transaction: " + transactionId + "\n" +
                    "• Status: COMPLETED\n\n" +
                    "Thank you for using AtlantaBank!\n" +
                    "A confirmation email has been sent.";

            new AlertDialog.Builder(this)
                    .setTitle("Payment Complete")
                    .setMessage(successMessage)
                    .setPositiveButton("OK", null)
                    .show();

            Toast.makeText(this, "Payment of " + amount + " completed successfully", Toast.LENGTH_LONG).show();

        }, 2000);
    }

    /**
     * THE VULNERABILITY: Accept more payment manipulation via intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Process new payment data if received
        processIntentData();
        displayPaymentData();
    }
}
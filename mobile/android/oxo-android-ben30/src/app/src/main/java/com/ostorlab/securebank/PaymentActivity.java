package com.ostorlab.securebank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView userAccountText;
    private Spinner payeeSpinner;
    private EditText paymentAmountField;
    private EditText paymentReferenceField;
    private Button payButton;
    private Button backButton;

    private String currentUser;
    private String accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initializeViews();
        loadPaymentData();
        setupPayeeSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        userAccountText = findViewById(R.id.user_account_text);
        payeeSpinner = findViewById(R.id.payee_spinner);
        paymentAmountField = findViewById(R.id.payment_amount_field);
        paymentReferenceField = findViewById(R.id.payment_reference_field);
        payButton = findViewById(R.id.pay_button);
        backButton = findViewById(R.id.back_button);
    }

    private void loadPaymentData() {
        currentUser = getIntent().getStringExtra("username");
        accountNumber = getIntent().getStringExtra("account_number");

        if (accountNumber != null) {
            userAccountText.setText("From Account: ****" + accountNumber.substring(accountNumber.length() - 4));
        }
    }

    private void setupPayeeSpinner() {
        String[] payees = {
            "Select Payee",
            "Electric Company (ACC: 9876543210)",
            "Water Utility (ACC: 8765432109)",
            "Internet Provider (ACC: 7654321098)",
            "Credit Card (ACC: 6543210987)",
            "Mortgage Company (ACC: 5432109876)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, payees);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payeeSpinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToDashboard();
            }
        });
    }

    private void processPayment() {
        String selectedPayee = payeeSpinner.getSelectedItem().toString();
        String paymentAmount = paymentAmountField.getText().toString().trim();
        String reference = paymentReferenceField.getText().toString().trim();

        if ("Select Payee".equals(selectedPayee)) {
            Toast.makeText(this, "Please select a payee", Toast.LENGTH_SHORT).show();
            return;
        }

        if (paymentAmount.isEmpty()) {
            Toast.makeText(this, "Please enter payment amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(paymentAmount);

            if (amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate payment processing
            simulatePaymentProcess(selectedPayee, amount, reference);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    private void simulatePaymentProcess(String payee, double amount, String reference) {
        // Show processing message
        Toast.makeText(this, "Processing payment...", Toast.LENGTH_SHORT).show();

        // Simulate network delay
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Extract payee name for confirmation
        String payeeName = payee.split(" \\(")[0];
        
        Toast.makeText(this, "Payment of $" + amount + " to " + payeeName + " completed", Toast.LENGTH_LONG).show();

        // Clear sensitive payment data
        clearPaymentFields();

        // Return to dashboard
        returnToDashboard();
    }

    private void clearPaymentFields() {
        payeeSpinner.setSelection(0);
        paymentAmountField.setText("");
        paymentReferenceField.setText("");
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
        // Payment activity becomes vulnerable when paused - sensitive payment data exposed
    }

    @Override
    public void onBackPressed() {
        returnToDashboard();
    }
}

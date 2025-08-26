package com.securebank.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TextView historyText;
    private Button backButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        databaseHelper = new DatabaseHelper(this);

        historyText = findViewById(R.id.historyText);
        backButton = findViewById(R.id.backButton);

        loadTransactionHistory();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadTransactionHistory() {
        SharedPreferences prefs = getSharedPreferences("bank_data", MODE_PRIVATE);
        String username = prefs.getString("logged_in_user", "");

        if (username.isEmpty()) {
            historyText.setText("No transaction history available. Please login.");
            return;
        }

        List<Transaction> transactions = databaseHelper.getTransactionHistory(username);

        if (transactions.isEmpty()) {
            historyText.setText("No transactions found for your account.");
            return;
        }

        StringBuilder history = new StringBuilder();
        history.append("Recent Transactions:\n\n");

        for (Transaction transaction : transactions) {
            String formattedDate = formatDate(transaction.getTransactionDate());
            String formattedAmount = String.format("%.2f", transaction.getAmount());

            history.append(formattedDate);
            history.append(" - ");
            history.append(transaction.getTransactionType());

            if ("TRANSFER".equals(transaction.getTransactionType())) {
                User recipient = databaseHelper.getUserByAccountNumber(transaction.getToAccount());
                if (recipient != null) {
                    history.append(" to ").append(recipient.getUsername());
                } else {
                    history.append(" to ").append(transaction.getToAccount());
                }
            } else if ("WITHDRAWAL".equals(transaction.getTransactionType())) {
                history.append(" from ").append(transaction.getToAccount());
            } else if ("PURCHASE".equals(transaction.getTransactionType())) {
                history.append(" at ").append(transaction.getToAccount());
            }

            history.append(" - $").append(formattedAmount);

            if (transaction.getDescription() != null && !transaction.getDescription().isEmpty()) {
                history.append("\n   ").append(transaction.getDescription());
            }

            history.append("\n   Status: ").append(transaction.getStatus());
            history.append("\n\n");
        }

        historyText.setText(history.toString());
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }
}

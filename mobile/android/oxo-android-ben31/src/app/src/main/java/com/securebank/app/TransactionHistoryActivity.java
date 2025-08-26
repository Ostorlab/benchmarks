package com.securebank.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TextView historyText;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

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
        StringBuilder history = new StringBuilder();
        history.append("Recent Transactions:\n\n");
        history.append("08/25/2025 - Transfer to John Smith - $250.00\n");
        history.append("08/24/2025 - ATM Withdrawal - $100.00\n");
        history.append("08/23/2025 - Online Purchase - $45.99\n");
        history.append("08/22/2025 - Deposit - $1,200.00\n");
        history.append("08/21/2025 - Transfer to Sarah Johnson - $75.00\n");
        history.append("08/20/2025 - Bill Payment - $89.50\n");
        history.append("08/19/2025 - ATM Withdrawal - $60.00\n");
        history.append("08/18/2025 - Direct Deposit - $2,500.00\n");

        historyText.setText(history.toString());
    }
}

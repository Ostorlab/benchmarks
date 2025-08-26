package com.ostorlab.securebank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    private TextView sessionInfoText;
    private TextView chatHistoryText;
    private EditText messageField;
    private Button sendButton;
    private Button backButton;
    private ScrollView chatScrollView;

    private String currentUser;
    private String sessionId;
    private StringBuilder chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        initializeViews();
        loadSupportData();
        setupClickListeners();
        initializeChatHistory();
    }

    private void initializeViews() {
        sessionInfoText = findViewById(R.id.session_info_text);
        chatHistoryText = findViewById(R.id.chat_history_text);
        messageField = findViewById(R.id.message_field);
        sendButton = findViewById(R.id.send_button);
        backButton = findViewById(R.id.back_button);
        chatScrollView = findViewById(R.id.chat_scroll_view);
    }

    private void loadSupportData() {
        currentUser = getIntent().getStringExtra("username");
        sessionId = getIntent().getStringExtra("session_id");

        if (sessionId != null) {
            sessionInfoText.setText("Support Session: " + sessionId + " | User: " + currentUser);
        }
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToDashboard();
            }
        });
    }

    private void initializeChatHistory() {
        chatHistory = new StringBuilder();
        
        // Add welcome message
        addSystemMessage("Welcome to SecureBank Customer Support");
        addSystemMessage("How can we help you today?");
        addSystemMessage("Your session ID is: " + sessionId);
    }

    private void sendMessage() {
        String message = messageField.getText().toString().trim();
        
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message to chat
        addUserMessage(message);
        
        // Clear message field
        messageField.setText("");
        
        // Simulate support response
        simulateSupportResponse(message);
    }

    private void addUserMessage(String message) {
        chatHistory.append("\n[").append(getCurrentTime()).append("] You: ").append(message);
        updateChatDisplay();
    }

    private void addSystemMessage(String message) {
        chatHistory.append("\n[").append(getCurrentTime()).append("] Support: ").append(message);
        updateChatDisplay();
    }

    private void simulateSupportResponse(String userMessage) {
        // Simulate typing delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String response;
        String lowerMessage = userMessage.toLowerCase();

        if (lowerMessage.contains("balance") || lowerMessage.contains("account")) {
            response = "I can help you with account inquiries. Your current balance information is available in the dashboard.";
        } else if (lowerMessage.contains("transfer") || lowerMessage.contains("payment")) {
            response = "For transfer and payment issues, please check your transaction history or try the operation again.";
        } else if (lowerMessage.contains("password") || lowerMessage.contains("login")) {
            response = "For security reasons, I cannot reset passwords through chat. Please visit a branch or use the mobile app reset feature.";
        } else if (lowerMessage.contains("card") || lowerMessage.contains("debit")) {
            response = "Card services are available 24/7. Would you like me to connect you to our card support team?";
        } else {
            response = "Thank you for contacting us. A support representative will assist you shortly. Is there anything specific I can help you with?";
        }

        addSystemMessage(response);
    }

    private void updateChatDisplay() {
        chatHistoryText.setText(chatHistory.toString());
        
        // Scroll to bottom
        chatScrollView.post(new Runnable() {
            @Override
            public void run() {
                chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private String getCurrentTime() {
        return java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date());
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
        // Support chat contains sensitive user information and session data
    }

    @Override
    public void onBackPressed() {
        returnToDashboard();
    }
}

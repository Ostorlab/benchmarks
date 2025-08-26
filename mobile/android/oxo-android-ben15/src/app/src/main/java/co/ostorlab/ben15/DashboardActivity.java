package co.ostorlab.ben15;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sessionstayactive.R;

public class DashboardActivity extends AppCompatActivity {
    
    private TextView welcomeText;
    private TextView balanceAmount;
    private Button changePasswordButton;
    private Button logoutButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        sessionManager = new SessionManager(this);
        
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        
        initViews();
        setupListeners();
        updateUI();
    }
    
    private void initViews() {
        welcomeText = findViewById(R.id.welcomeText);
        balanceAmount = findViewById(R.id.balanceAmount);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
    }
    
    private void setupListeners() {
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        
        logoutButton.setOnClickListener(v -> {
            sessionManager.logout();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
    
    private void updateUI() {
        String username = sessionManager.getUsername();
        welcomeText.setText("Welcome, " + username + "!");
    }
    
    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        
        EditText currentPasswordInput = dialogView.findViewById(R.id.currentPasswordInput);
        EditText newPasswordInput = dialogView.findViewById(R.id.newPasswordInput);
        EditText confirmPasswordInput = dialogView.findViewById(R.id.confirmPasswordInput);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button changeButton = dialogView.findViewById(R.id.changeButton);
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        changeButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "New passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            changeButton.setEnabled(false);
            
            sessionManager.changePassword(currentPassword, newPassword, new SessionManager.PasswordChangeCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(DashboardActivity.this, "Password changed successfully!", Toast.LENGTH_LONG).show();
                        changeButton.setEnabled(true);
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        changeButton.setEnabled(true);
                        Toast.makeText(DashboardActivity.this, "Password change failed: " + error, Toast.LENGTH_SHORT).show();
                        currentPasswordInput.setText("");
                    });
                }
            });
        });
        
        dialog.show();
    }
}
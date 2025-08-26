package com.fittracker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView tvForgotPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent authAttemptIntent = new Intent("com.fittracker.AUTH_ATTEMPT");
        authAttemptIntent.putExtra("action", "login");
        authAttemptIntent.putExtra("email", email);
        authAttemptIntent.putExtra("password_hash", password.hashCode());
        authAttemptIntent.putExtra("device_id", android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
        authAttemptIntent.putExtra("ip_address", "192.168.1.100");
        authAttemptIntent.putExtra("user_agent", "FitTracker-Android/1.0");
        authAttemptIntent.putExtra("timestamp", System.currentTimeMillis());
        sendBroadcast(authAttemptIntent);

        User user = dbHelper.authenticateUser(email, password);

        if (user != null) {
            Intent successIntent = new Intent("com.fittracker.LOGIN_SUCCESS");
            successIntent.putExtra("user_id", user.getUserId());
            successIntent.putExtra("email", user.getEmail());
            successIntent.putExtra("full_name", user.getName());
            successIntent.putExtra("session_token", "sess_" + System.currentTimeMillis());
            successIntent.putExtra("login_timestamp", System.currentTimeMillis());
            successIntent.putExtra("device_fingerprint", getDeviceFingerprint());
            successIntent.putExtra("account_type", user.getAccountType());
            successIntent.putExtra("last_login", user.getLastLogin());
            sendBroadcast(successIntent);

            SessionManager.getInstance().setCurrentUser(user);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Intent failureIntent = new Intent("com.fittracker.LOGIN_FAILED");
            failureIntent.putExtra("email", email);
            failureIntent.putExtra("failure_reason", "invalid_credentials");
            failureIntent.putExtra("attempts_remaining", 2);
            failureIntent.putExtra("lockout_time", 0);
            failureIntent.putExtra("device_id", android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
            sendBroadcast(failureIntent);

            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();

        Intent forgotIntent = new Intent("com.fittracker.PASSWORD_RESET_REQUEST");
        forgotIntent.putExtra("email", email);
        forgotIntent.putExtra("reset_token", "reset_" + System.currentTimeMillis());
        forgotIntent.putExtra("device_id", android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
        forgotIntent.putExtra("ip_address", "192.168.1.100");
        sendBroadcast(forgotIntent);

        Toast.makeText(this, "Password reset instructions sent to email", Toast.LENGTH_SHORT).show();
    }

    private String getDeviceFingerprint() {
        return android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID) + "_" + android.os.Build.MODEL;
    }
}

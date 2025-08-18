package com.insecure.ostorlab.sharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> storeSensitiveDataInSharedPreferences());
    }

    private void storeSensitiveDataInSharedPreferences() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }
}

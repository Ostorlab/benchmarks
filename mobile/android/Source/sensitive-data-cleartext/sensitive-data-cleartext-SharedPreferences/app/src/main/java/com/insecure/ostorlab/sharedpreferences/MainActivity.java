package com.insecure.ostorlab.sharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Fake AWS token (sensitive data)
    private static final String FAKE_AWS_ACCESS_KEY = "AKIAT3G7X2Q9L1P8W0RZ";
    private static final String FAKE_AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG+bPxRfiCY7NQ3T9W5X4Z";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storeSensitiveDataInSharedPreferences();
    }

    private void storeSensitiveDataInSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Intentionally storing sensitive data in plaintext
        editor.putString("aws_access_key_id", FAKE_AWS_ACCESS_KEY);
        editor.putString("aws_secret_access_key", FAKE_AWS_SECRET_KEY);
        editor.apply(); // or .commit()
    }
}
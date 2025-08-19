package com.ostorlab.your.song;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText name, number, expiry, cvv, type;
    Button saveBtn, viewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        number = findViewById(R.id.cardNumber);
        expiry = findViewById(R.id.expiry);
        cvv = findViewById(R.id.cvv);
        type = findViewById(R.id.type);
        saveBtn = findViewById(R.id.saveBtn);
        viewBtn = findViewById(R.id.viewBtn);

        saveBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SaveActivity.class);
            intent.putExtra("name", name.getText().toString());
            intent.putExtra("number", number.getText().toString());
            intent.putExtra("expiry", expiry.getText().toString());
            intent.putExtra("cvv", cvv.getText().toString());
            intent.putExtra("type", type.getText().toString());
            startActivity(intent);
        });

        viewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewActivity.class);
            startActivity(intent);
        });
    }
}
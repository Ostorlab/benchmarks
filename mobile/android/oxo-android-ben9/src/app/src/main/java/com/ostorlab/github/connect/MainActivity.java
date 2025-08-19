package com.ostorlab.github.connect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGoApi = findViewById(R.id.btnGoApi);
        btnGoApi.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ApiActivity.class));
        });
    }
}

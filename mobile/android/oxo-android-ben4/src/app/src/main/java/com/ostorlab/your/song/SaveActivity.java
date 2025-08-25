package com.ostorlab.your.song;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;

public class SaveActivity extends AppCompatActivity {
    private final String FILENAME = "credit_card_data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        String name = getIntent().getStringExtra("name");
        String number = getIntent().getStringExtra("number");
        String expiry = getIntent().getStringExtra("expiry");
        String cvv = getIntent().getStringExtra("cvv");
        String type = getIntent().getStringExtra("type");

        String data = "Name: " + name + "\nCard Number: " + number + "\nExpiry: " + expiry +
                "\nCVV: " + cvv + "\nType: " + type;

        try {
            FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.status)).setText("Data saved");
    }
}
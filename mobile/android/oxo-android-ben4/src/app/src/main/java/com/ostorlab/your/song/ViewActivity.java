package com.ostorlab.your.song;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class ViewActivity extends AppCompatActivity {
    private final String FILENAME = "credit_card_data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        TextView output = findViewById(R.id.output);

        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            output.setText(builder.toString());
        } catch (Exception e) {
            output.setText("No data provided");
        }
    }
}

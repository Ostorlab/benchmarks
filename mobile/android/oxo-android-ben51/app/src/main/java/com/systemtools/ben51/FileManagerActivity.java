package com.systemtools.ben51;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileManagerActivity extends AppCompatActivity {
    private EditText searchPatternInput;
    private EditText targetDirectoryInput;
    private TextView fileListDisplay;
    private Button searchButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        searchPatternInput = findViewById(R.id.searchPatternInput);
        targetDirectoryInput = findViewById(R.id.targetDirectoryInput);
        fileListDisplay = findViewById(R.id.fileListDisplay);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFiles();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchFiles() {
        String pattern = searchPatternInput.getText().toString().trim();
        String directory = targetDirectoryInput.getText().toString().trim();

        if (pattern.isEmpty()) {
            Toast.makeText(this, "Please enter a search pattern", Toast.LENGTH_SHORT).show();
            return;
        }

        if (directory.isEmpty()) {
            directory = "/data/data/com.systemtools.ben51";
        }

        try {
            String command = "find " + directory + " -name \"" + pattern + "\"";
            executeSearchCommand(command);
        } catch (Exception e) {
            fileListDisplay.setText("Error: " + e.getMessage());
        }
    }

    private void executeSearchCommand(String command) {
        try {
            String[] cmd = {"/system/bin/sh", "-c", command};
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                output.append("Error: ").append(line).append("\n");
            }

            process.waitFor();

            if (output.length() == 0) {
                output.append("No files found matching the pattern");
            }

            fileListDisplay.setText(output.toString());

        } catch (IOException | InterruptedException e) {
            fileListDisplay.setText("Failed to execute search: " + e.getMessage());
        }
    }
}

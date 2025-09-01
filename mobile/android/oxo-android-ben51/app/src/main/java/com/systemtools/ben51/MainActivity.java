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

public class MainActivity extends AppCompatActivity {
    private EditText filePathInput;
    private EditText pingHostInput;
    private TextView outputDisplay;
    private Button listFilesButton;
    private Button pingHostButton;
    private Button systemInfoButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filePathInput = findViewById(R.id.filePathInput);
        pingHostInput = findViewById(R.id.pingHostInput);
        outputDisplay = findViewById(R.id.outputDisplay);
        listFilesButton = findViewById(R.id.listFilesButton);
        pingHostButton = findViewById(R.id.pingHostButton);
        systemInfoButton = findViewById(R.id.systemInfoButton);
        clearButton = findViewById(R.id.clearButton);

        listFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listFiles();
            }
        });

        pingHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pingHost();
            }
        });

        systemInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSystemInfo();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputDisplay.setText("");
            }
        });
    }

    private void listFiles() {
        String path = filePathInput.getText().toString().trim();
        if (path.isEmpty()) {
            path = "/data/data/com.systemtools.ben51";
        }

        try {
            String command = "ls -la " + path;
            executeCommand(command);
        } catch (Exception e) {
            outputDisplay.setText("Error: " + e.getMessage());
        }
    }

    private void pingHost() {
        String host = pingHostInput.getText().toString().trim();
        if (host.isEmpty()) {
            Toast.makeText(this, "Please enter a host to ping", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String command = "ping -c 3 " + host;
            executeCommand(command);
        } catch (Exception e) {
            outputDisplay.setText("Error: " + e.getMessage());
        }
    }

    private void showSystemInfo() {
        try {
            String command = "uname -a";
            executeCommand(command);
        } catch (Exception e) {
            outputDisplay.setText("Error: " + e.getMessage());
        }
    }

    private void executeCommand(String command) {
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
                output.append("Command executed successfully (no output)");
            }

            outputDisplay.setText(output.toString());

        } catch (IOException | InterruptedException e) {
            outputDisplay.setText("Failed to execute command: " + e.getMessage());
        }
    }
}

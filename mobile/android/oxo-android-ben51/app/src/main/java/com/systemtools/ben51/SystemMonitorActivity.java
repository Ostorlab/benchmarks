package com.systemtools.ben51;

import android.content.Intent;
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

public class SystemMonitorActivity extends AppCompatActivity {
    private EditText processNameInput;
    private TextView monitorOutput;
    private Button processListButton;
    private Button killProcessButton;
    private Button diskUsageButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_monitor);

        processNameInput = findViewById(R.id.processNameInput);
        monitorOutput = findViewById(R.id.monitorOutput);
        processListButton = findViewById(R.id.processListButton);
        killProcessButton = findViewById(R.id.killProcessButton);
        diskUsageButton = findViewById(R.id.diskUsageButton);
        backButton = findViewById(R.id.backButton);

        processListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listProcesses();
            }
        });

        killProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                killProcess();
            }
        });

        diskUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiskUsage();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void listProcesses() {
        try {
            String command = "ps aux";
            executeMonitorCommand(command);
        } catch (Exception e) {
            monitorOutput.setText("Error: " + e.getMessage());
        }
    }

    private void killProcess() {
        String processName = processNameInput.getText().toString().trim();

        if (processName.isEmpty()) {
            Toast.makeText(this, "Please enter a process name", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String command = "pkill " + processName;
            executeMonitorCommand(command);
        } catch (Exception e) {
            monitorOutput.setText("Error: " + e.getMessage());
        }
    }

    private void showDiskUsage() {
        try {
            String command = "df -h";
            executeMonitorCommand(command);
        } catch (Exception e) {
            monitorOutput.setText("Error: " + e.getMessage());
        }
    }

    private void executeMonitorCommand(String command) {
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
                output.append("Command executed successfully");
            }

            monitorOutput.setText(output.toString());

        } catch (IOException | InterruptedException e) {
            monitorOutput.setText("Failed to execute monitor command: " + e.getMessage());
        }
    }
}

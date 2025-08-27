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

public class NetworkToolsActivity extends AppCompatActivity {
    private EditText portInput;
    private EditText hostnameInput;
    private TextView networkOutput;
    private Button portScanButton;
    private Button tracerouteButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_tools);

        portInput = findViewById(R.id.portInput);
        hostnameInput = findViewById(R.id.hostnameInput);
        networkOutput = findViewById(R.id.networkOutput);
        portScanButton = findViewById(R.id.portScanButton);
        tracerouteButton = findViewById(R.id.tracerouteButton);
        backButton = findViewById(R.id.backButton);

        portScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanPort();
            }
        });

        tracerouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performTraceroute();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void scanPort() {
        String hostname = hostnameInput.getText().toString().trim();
        String port = portInput.getText().toString().trim();

        if (hostname.isEmpty()) {
            Toast.makeText(this, "Please enter a hostname", Toast.LENGTH_SHORT).show();
            return;
        }

        if (port.isEmpty()) {
            port = "80";
        }

        try {
            String command = "nc -z -v " + hostname + " " + port;
            executeNetworkCommand(command);
        } catch (Exception e) {
            networkOutput.setText("Error: " + e.getMessage());
        }
    }

    private void performTraceroute() {
        String hostname = hostnameInput.getText().toString().trim();

        if (hostname.isEmpty()) {
            Toast.makeText(this, "Please enter a hostname", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String command = "traceroute " + hostname;
            executeNetworkCommand(command);
        } catch (Exception e) {
            networkOutput.setText("Error: " + e.getMessage());
        }
    }

    private void executeNetworkCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
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
                output.append("Command completed (no output)");
            }

            networkOutput.setText(output.toString());

        } catch (IOException | InterruptedException e) {
            networkOutput.setText("Failed to execute network command: " + e.getMessage());
        }
    }
}

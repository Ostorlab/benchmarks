package com.ostorlab.unzipper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.*;

public class FileDownloadActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 2001;

    private TextView tvFilePath, tvStatus;
    private Button btnDownload;

    private String extractionPath;
    private String selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);

        tvFilePath = findViewById(R.id.tvFilePath);
        tvStatus = findViewById(R.id.tvStatus);
        btnDownload = findViewById(R.id.btnDownload);

        extractionPath = getIntent().getStringExtra("extraction_path");
        selectedFile = getIntent().getStringExtra("selected_file");

        tvFilePath.setText("File to download:\n" + selectedFile);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndDownload();
            }
        });
    }

    private void checkPermissionAndDownload() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            downloadFile();
        }
    }

    private void downloadFile() {
        File srcFile = new File(extractionPath, selectedFile);
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!downloadDir.exists()) {
            if (!downloadDir.mkdirs()) {
                tvStatus.setText("Failed to create Download directory");
                return;
            }
        }

        File destFile = new File(downloadDir, new File(selectedFile).getName());

        try {
            copyFile(srcFile, destFile);
            tvStatus.setText("Downloaded to: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            tvStatus.setText("Error: " + e.getMessage());
        }
    }

    private void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadFile();
            } else {
                Toast.makeText(this, "Permission required to download file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

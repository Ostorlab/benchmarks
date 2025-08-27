package com.sideload.installer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements APKAdapter.OnAPKClickListener {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int MANAGE_EXTERNAL_STORAGE_REQUEST_CODE = 101;

    private RecyclerView recyclerView;
    private APKAdapter adapter;
    private List<APKFile> apkFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        requestPermissions();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apkFiles = new ArrayList<>();
        adapter = new APKAdapter(apkFiles, this);
        recyclerView.setAdapter(adapter);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_REQUEST_CODE);
            } else {
                scanForAPKFiles();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            } else {
                scanForAPKFiles();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanForAPKFiles();
            } else {
                Toast.makeText(this, "Storage permission required to scan APK files", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MANAGE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                scanForAPKFiles();
            } else {
                Toast.makeText(this, "Storage permission required to scan APK files", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void scanForAPKFiles() {
        apkFiles.clear();

        // Scan common directories for APK files
        List<File> searchDirectories = new ArrayList<>();

        // Add external storage directories
        searchDirectories.add(Environment.getExternalStorageDirectory());
        searchDirectories.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

        // Add additional storage locations
        File[] externalDirs = getExternalFilesDirs(null);
        for (File dir : externalDirs) {
            if (dir != null) {
                searchDirectories.add(dir.getParentFile().getParentFile());
            }
        }

        // Scan each directory
        for (File directory : searchDirectories) {
            if (directory != null && directory.exists()) {
                scanDirectory(directory);
            }
        }

        adapter.notifyDataSetChanged();

        if (apkFiles.isEmpty()) {
            Toast.makeText(this, "No APK files found on device", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Found " + apkFiles.size() + " APK files", Toast.LENGTH_SHORT).show();
        }
    }

    private void scanDirectory(File directory) {
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && !file.getName().startsWith(".")) {
                        // Recursively scan subdirectories (with depth limit)
                        if (getDirectoryDepth(file) < 5) {
                            scanDirectory(file);
                        }
                    } else if (file.isFile() && file.getName().toLowerCase().endsWith(".apk")) {
                        APKFile apkFile = new APKFile(file.getName(), file.getAbsolutePath(), file.length());
                        apkFiles.add(apkFile);
                    }
                }
            }
        } catch (SecurityException e) {
            // Ignore directories we can't access
        }
    }

    private int getDirectoryDepth(File directory) {
        int depth = 0;
        File parent = directory.getParentFile();
        while (parent != null) {
            depth++;
            parent = parent.getParentFile();
        }
        return depth;
    }

    @Override
    public void onAPKClick(APKFile apkFile) {
        installAPK(apkFile.getPath());
    }

    private void installAPK(String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            Toast.makeText(this, "APK file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(this,
                    "com.sideload.installer.fileprovider", apkFile);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(this, "Error installing APK: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

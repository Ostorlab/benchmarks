package com.ostorlab.unzipper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 123;
    private static final int PICK_ZIP_REQUEST = 1001;

    private Button btnSelectZip;
    private TextView tvStatus;

    private File extractionDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyWebviewAssetsToInternalStorage();

        btnSelectZip = findViewById(R.id.btnSelectZip);
        tvStatus = findViewById(R.id.tvStatus);

        extractionDir = new File(getFilesDir(), "extracted");

        btnSelectZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndOpenFilePicker();
            }
        });

        Button btnOpenWebView = findViewById(R.id.btnOpenWebView);
        btnOpenWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkPermissionAndOpenFilePicker() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            openFilePicker();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select ZIP file"), PICK_ZIP_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Permission required to select ZIP file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_ZIP_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    tvStatus.setText("Status: Unzipping...");
                    try {
                        File zipFile = copyUriToFile(uri);
                        unsafeUnzip(zipFile, extractionDir);
                        tvStatus.setText("Status: Unzipped to " + extractionDir.getAbsolutePath());

                        Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                        intent.putExtra("extraction_path", extractionDir.getAbsolutePath());
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        tvStatus.setText("Status: Error - " + e.getMessage());
                    }
                }
            }
        }
    }

    private File copyUriToFile(Uri uri) throws IOException {
        InputStream in = getContentResolver().openInputStream(uri);
        File tempFile = new File(getCacheDir(), "temp.zip");
        FileOutputStream out = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
        return tempFile;
    }

    private void unsafeUnzip(File zipFile, File targetDir) throws IOException {
        if (targetDir.exists()) {
            deleteRecursively(targetDir);
        }
        targetDir.mkdirs();

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {
            File outFile = new File(targetDir, entry.getName());

            if (entry.isDirectory()) {
                outFile.mkdirs();
            } else {
                File parent = outFile.getParentFile();
                if (!parent.exists()) parent.mkdirs();

                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[4096];
                int count;
                while ((count = zipIn.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
            }
            zipIn.closeEntry();
        }
        zipIn.close();
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if(children != null){
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

    private void copyWebviewAssetsToInternalStorage() {
        File webviewDir = new File(getFilesDir(), "webview");
        if (!webviewDir.exists()) {
            webviewDir.mkdirs();
        }

        String[] assetFiles = {"index.html", "style.css", "script.js"};

        for (String filename : assetFiles) {
            File outFile = new File(webviewDir, filename);
            if (outFile.exists()) continue;

            try (InputStream in = getAssets().open("webview/" + filename);
                 FileOutputStream out = new FileOutputStream(outFile)) {

                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}



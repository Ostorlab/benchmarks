package com.ostorlab.zip.viewer;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipViewerActivity extends AppCompatActivity {

    TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_viewer);
        statusView = findViewById(R.id.statusText);

        String uriStr = getSharedPreferences("prefs", MODE_PRIVATE).getString("zipUri", null);
        if (uriStr == null) {
            statusView.setText("No ZIP selected.");
            return;
        }

        try {
            unzipVulnerable(Uri.parse(uriStr));
            statusView.setText("ZIP extracted");
        } catch (Exception e) {
            statusView.setText("Error: " + e.getMessage());
        }
    }

    private void unzipVulnerable(Uri zipUri) throws IOException {
        try (InputStream inputStream = getContentResolver().openInputStream(zipUri);
             ZipInputStream zis = new ZipInputStream(inputStream)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(getFilesDir(), entry.getName());
                File parent = file.getParentFile();
                if (!parent.exists()) parent.mkdirs();

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zis.closeEntry();
            }
        }
    }
}

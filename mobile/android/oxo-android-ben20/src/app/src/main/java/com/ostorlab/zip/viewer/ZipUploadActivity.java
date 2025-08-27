package com.ostorlab.zip.viewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ZipUploadActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri zipUri = result.getData().getData();

                    if (zipUri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    zipUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            );

                            getSharedPreferences("prefs", MODE_PRIVATE)
                                    .edit()
                                    .putString("zipUri", zipUri.toString())
                                    .apply();

                            Toast.makeText(this, "ZIP selected!", Toast.LENGTH_SHORT).show();
                        } catch (SecurityException e) {
                            Toast.makeText(this, "Failed to take permission for ZIP file", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Invalid ZIP file selected.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "File selection cancelled", Toast.LENGTH_SHORT).show();
                }

                finish();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        filePickerLauncher.launch(intent);
    }
}

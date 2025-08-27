package com.ostorlab.zip.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button uploadBtn = findViewById(R.id.btn_upload);
        Button viewBtn = findViewById(R.id.btn_view);

        uploadBtn.setOnClickListener(v -> startActivity(new Intent(this, ZipUploadActivity.class)));
        viewBtn.setOnClickListener(v -> startActivity(new Intent(this, ZipViewerActivity.class)));
    }
}

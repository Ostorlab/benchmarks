package com.ostorlab.unzipper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity {

    private ListView lvFiles;
    private ArrayList<String> filesList;
    private String extractionPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        lvFiles = findViewById(R.id.lvFiles);

        extractionPath = getIntent().getStringExtra("extraction_path");
        filesList = new ArrayList<>();
        listFilesRecursive(new File(extractionPath), "", filesList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, filesList);
        lvFiles.setAdapter(adapter);

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFile = filesList.get(position);
                Intent intent = new Intent(FileListActivity.this, FileDownloadActivity.class);
                intent.putExtra("extraction_path", extractionPath);
                intent.putExtra("selected_file", selectedFile);
                startActivity(intent);
            }
        });
    }

    private void listFilesRecursive(File dir, String prefix, ArrayList<String> output) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                listFilesRecursive(f, prefix + f.getName() + "/", output);
            } else {
                output.add(prefix + f.getName());
            }
        }
    }
}

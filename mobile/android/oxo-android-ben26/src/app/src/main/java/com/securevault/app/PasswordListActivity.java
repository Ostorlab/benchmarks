package com.securevault.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class PasswordListActivity extends AppCompatActivity {
    private StorageManager storageManager;
    private RecyclerView recyclerView;
    private PasswordAdapter adapter;
    private boolean showingPasswords = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        storageManager = new StorageManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SecureVault");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnMyPasswords = findViewById(R.id.btnMyPasswords);
        Button btnSecureNotes = findViewById(R.id.btnSecureNotes);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnBackup = findViewById(R.id.btnBackup);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        btnMyPasswords.setOnClickListener(v -> {
            showingPasswords = true;
            loadPasswords();
        });

        btnSecureNotes.setOnClickListener(v -> {
            showingPasswords = false;
            loadNotes();
        });

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        btnBackup.setOnClickListener(v -> {
            startActivity(new Intent(this, BackupActivity.class));
        });

        fabAdd.setOnClickListener(v -> {
            if (showingPasswords) {
                startActivity(new Intent(this, AddPasswordActivity.class));
            } else {
                startActivity(new Intent(this, NotesActivity.class));
            }
        });

        loadPasswords();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (showingPasswords) {
            loadPasswords();
        } else {
            loadNotes();
        }
    }

    private void loadPasswords() {
        List<PasswordEntry> passwords = storageManager.getPasswordEntries();
        adapter = new PasswordAdapter(passwords, storageManager.getMasterKey());
        recyclerView.setAdapter(adapter);
    }

    private void loadNotes() {
        List<SecureNote> notes = storageManager.getSecureNotes();
        adapter = new PasswordAdapter(notes, storageManager.getMasterKey());
        recyclerView.setAdapter(adapter);
    }
}

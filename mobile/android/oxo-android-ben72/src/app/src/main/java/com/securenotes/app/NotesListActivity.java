package com.securenotes.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class NotesListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.notesRecyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadNotes();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesListActivity.this, CreateNoteActivity.class));
            }
        });

        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesListActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        List<Note> notes = dbHelper.getAllNotes();
        adapter = new NotesAdapter(notes, this);
        recyclerView.setAdapter(adapter);
    }
}
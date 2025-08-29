package com.securenotes.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText titleEdit, contentEdit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        dbHelper = new DatabaseHelper(this);
        titleEdit = findViewById(R.id.titleEdit);
        contentEdit = findViewById(R.id.contentEdit);
        Button saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEdit.getText().toString();
                String content = contentEdit.getText().toString();

                if (!title.isEmpty() && !content.isEmpty()) {
                    Note note = new Note(title, content, System.currentTimeMillis());
                    if (dbHelper.addNote(note)) {
                        Toast.makeText(CreateNoteActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(CreateNoteActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
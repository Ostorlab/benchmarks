package com.ostorlab.name.show;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editName;
    Button btnSave, btnShow;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);

        editName = findViewById(R.id.editName);
        btnSave = findViewById(R.id.btnSave);
        btnShow = findViewById(R.id.btnShow);

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            if (!name.isEmpty()) {
                db.insertName(name);
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                editName.setText("");
            }
        });

        btnShow.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        });
    }
}

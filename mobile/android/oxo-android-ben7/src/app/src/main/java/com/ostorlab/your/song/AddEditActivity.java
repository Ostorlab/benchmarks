package com.ostorlab.your.song;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {

    EditText etSocial, etMedical;
    Button btnSave;
    DBHelper dbHelper;
    int recordId = -1; // For edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etSocial = findViewById(R.id.etSocial);
        etMedical = findViewById(R.id.etMedical);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            recordId = intent.getIntExtra("id", -1);
            loadRecord(recordId);
        }

        btnSave.setOnClickListener(v -> {
            String social = etSocial.getText().toString().trim();
            String medical = etMedical.getText().toString().trim();

            if (TextUtils.isEmpty(social) || TextUtils.isEmpty(medical)) {
                Toast.makeText(AddEditActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (recordId == -1) {
                long inserted = dbHelper.insertData(social, medical);
                if (inserted != -1) {
                    Toast.makeText(AddEditActivity.this, "Record added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEditActivity.this, "Failed to add record", Toast.LENGTH_SHORT).show();
                }
            } else {
                int updated = dbHelper.updateData(recordId, social, medical);
                if (updated > 0) {
                    Toast.makeText(AddEditActivity.this, "Record updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEditActivity.this, "Failed to update record", Toast.LENGTH_SHORT).show();
                }
            }

            finish();
        });
    }

    private void loadRecord(int id) {
        Cursor cursor = dbHelper.getDataById(id);
        if (cursor.moveToFirst()) {
            String social = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_SOCIAL));
            String medical = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MEDICAL));
            etSocial.setText(social);
            etMedical.setText(medical);
        }
        cursor.close();
    }
}

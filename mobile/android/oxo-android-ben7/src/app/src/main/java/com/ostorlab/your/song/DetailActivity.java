package com.ostorlab.your.song;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    TextView tvSocial, tvMedical;
    Button btnEdit, btnDelete;
    DBHelper dbHelper;
    int recordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvSocial = findViewById(R.id.tvSocial);
        tvMedical = findViewById(R.id.tvMedical);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            recordId = intent.getIntExtra("id", -1);
            loadRecord(recordId);
        }

        btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(DetailActivity.this, AddEditActivity.class);
            editIntent.putExtra("id", recordId);
            startActivity(editIntent);
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(DetailActivity.this)
                    .setTitle("Delete Record")
                    .setMessage("Are you sure you want to delete this record?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int deleted = dbHelper.deleteData(recordId);
                        if (deleted > 0) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void loadRecord(int id) {
        Cursor cursor = dbHelper.getDataById(id);
        if (cursor.moveToFirst()) {
            String social = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_SOCIAL));
            String medical = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MEDICAL));
            tvSocial.setText("Social Number: " + social);
            tvMedical.setText("Medical Condition: " + medical);
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recordId != -1) {
            loadRecord(recordId);
        }
    }
}


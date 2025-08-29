package com.connectcall.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {
    private TextInputEditText profileNameEdit;
    private TextInputEditText profilePhoneEdit;
    private TextInputEditText profileEmailEdit;
    private Button saveProfileButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        loadProfile();
    }

    private void initializeViews() {
        profileNameEdit = findViewById(R.id.profileNameEdit);
        profilePhoneEdit = findViewById(R.id.profilePhoneEdit);
        profileEmailEdit = findViewById(R.id.profileEmailEdit);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PROFILE, null,
                DatabaseHelper.COLUMN_ID + "=?", new String[]{"1"}, null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE));
            String email = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL));

            profileNameEdit.setText(name);
            profilePhoneEdit.setText(phone);
            profileEmailEdit.setText(email);
        }
        cursor.close();
    }

    private void saveProfile() {
        String name = profileNameEdit.getText().toString().trim();
        String phone = profilePhoneEdit.getText().toString().trim();
        String email = profileEmailEdit.getText().toString().trim();

        if (name.isEmpty()) {
            profileNameEdit.setError("Name is required");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, 1);
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);

        long result = db.insertWithOnConflict(DatabaseHelper.TABLE_PROFILE, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);

        if (result != -1) {
            Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
        }
    }
}

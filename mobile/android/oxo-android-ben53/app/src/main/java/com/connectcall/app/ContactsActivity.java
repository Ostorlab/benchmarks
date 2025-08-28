package com.connectcall.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<Contact> contacts;
    private DatabaseHelper dbHelper;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.contactsRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        FloatingActionButton addContactFab = findViewById(R.id.addContactFab);

        contacts = new ArrayList<>();
        adapter = new ContactsAdapter(contacts, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadContacts();

        addContactFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSampleContact();
            }
        });
    }

    private void loadContacts() {
        contacts.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                contact.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)));
                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (contacts.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    private void addSampleContact() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, "John Doe");
        values.put(DatabaseHelper.COLUMN_PHONE, "+1234567890");
        values.put(DatabaseHelper.COLUMN_EMAIL, "john.doe@example.com");
        db.insert(DatabaseHelper.TABLE_CONTACTS, null, values);
        loadContacts();
    }

    public void startVideoCall(Contact contact) {
        Intent intent = new Intent(this, VideoCallActivity.class);
        intent.putExtra("contact_name", contact.getName());
        intent.putExtra("contact_phone", contact.getPhone());
        startActivity(intent);
    }
}

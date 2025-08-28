package com.connectcall.app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CallHistoryActivity extends AppCompatActivity {
    private RecyclerView historyRecyclerView;
    private CallHistoryAdapter adapter;
    private List<CallRecord> callHistory;
    private DatabaseHelper dbHelper;
    private TextView emptyHistoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        dbHelper = new DatabaseHelper(this);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        emptyHistoryView = findViewById(R.id.emptyHistoryView);

        callHistory = new ArrayList<>();
        adapter = new CallHistoryAdapter(callHistory);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(adapter);

        loadCallHistory();
    }

    private void loadCallHistory() {
        callHistory.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CALL_HISTORY, null, null, null, null, null,
                DatabaseHelper.COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                CallRecord record = new CallRecord();
                record.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                record.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
                record.setPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE)));
                record.setTimestamp(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP)));
                record.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DURATION)));
                record.setType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE)));
                callHistory.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (callHistory.isEmpty()) {
            emptyHistoryView.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
        } else {
            emptyHistoryView.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }
}

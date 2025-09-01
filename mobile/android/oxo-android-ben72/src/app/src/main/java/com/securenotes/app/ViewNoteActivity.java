package com.securenotes.app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewNoteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        TextView titleView = findViewById(R.id.titleView);
        TextView contentView = findViewById(R.id.contentView);
        TextView dateView = findViewById(R.id.dateView);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        long timestamp = getIntent().getLongExtra("timestamp", 0);

        titleView.setText(title);
        contentView.setText(content);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        dateView.setText(sdf.format(new Date(timestamp)));
    }
}
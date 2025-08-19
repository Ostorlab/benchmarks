package com.ostorlab.our.slackware;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class SlackwareInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setPadding(32, 32, 32, 32);
        textView.setText("Slackware Linux is one of the oldest actively maintained Linux distributions. "
                + "It aims for simplicity, stability, and a UNIX-like experience. "
                + "It uses plain text files for configuration and avoids dependency-heavy package managers, "
                + "preferring a clean and minimal base system.");

        setContentView(textView);
    }
}

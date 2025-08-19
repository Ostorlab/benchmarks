package com.ostorlab.our.slackware;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_webview).setOnClickListener(v ->
                startActivity(new Intent(this, SlackwareWebViewActivity.class)));

        findViewById(R.id.button_info).setOnClickListener(v ->
                startActivity(new Intent(this, SlackwareInfoActivity.class)));
    }
}
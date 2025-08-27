package com.example.myapplication1;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.Serializable;

public class DataProcessorActivity extends AppCompatActivity {
    private static final String TAG = "DataProcessorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("data")) {
            Serializable s = getIntent().getSerializableExtra("data");
            Log.d(TAG, "Processing data object: " + String.valueOf(s));

            if (s instanceof NativeDataHandler) {
                ((NativeDataHandler) s).invokeNativeFree();
            }
        } else {
            Log.d(TAG, "No data to process");
        }
    }
}

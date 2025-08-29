package com.connectcall.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class VideoCallActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceView localVideoView;
    private SurfaceView remoteVideoView;
    private TextView contactNameText;
    private Button muteButton;
    private Button videoToggleButton;
    private Button endCallButton;
    private Camera camera;
    private boolean isMuted = false;
    private boolean isVideoOn = true;
    private DatabaseHelper dbHelper;
    private String contactName;
    private String contactPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        handleIntent(getIntent());
        setupCamera();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void initializeViews() {
        localVideoView = findViewById(R.id.localVideoView);
        remoteVideoView = findViewById(R.id.remoteVideoView);
        contactNameText = findViewById(R.id.contactNameText);
        muteButton = findViewById(R.id.muteButton);
        videoToggleButton = findViewById(R.id.videoToggleButton);
        endCallButton = findViewById(R.id.endCallButton);

        localVideoView.getHolder().addCallback(this);

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMute();
            }
        });

        videoToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVideo();
            }
        });

        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            contactName = intent.getStringExtra("contact_name");
            contactPhone = intent.getStringExtra("contact_phone");

            if (contactName == null && intent.getData() != null) {
                String data = intent.getData().toString();
                if (data.startsWith("connectcall://call/")) {
                    contactPhone = data.substring("connectcall://call/".length());
                    contactName = "Unknown Contact";
                }
            }

            if (contactName != null) {
                contactNameText.setText(contactName);
                startCall();
            }
        }
    }

    private void setupCamera() {
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCall() {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(localVideoView.getHolder());
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        recordCallHistory();
    }

    private void toggleMute() {
        isMuted = !isMuted;
        muteButton.setBackgroundResource(isMuted ? android.R.drawable.ic_lock_silent_mode : android.R.drawable.ic_btn_speak_now);
    }

    private void toggleVideo() {
        isVideoOn = !isVideoOn;
        if (camera != null) {
            if (isVideoOn) {
                camera.startPreview();
                localVideoView.setVisibility(View.VISIBLE);
            } else {
                camera.stopPreview();
                localVideoView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void endCall() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        finish();
    }

    private void recordCallHistory() {
        if (contactName != null && contactPhone != null) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, contactName);
            values.put(DatabaseHelper.COLUMN_PHONE, contactPhone);
            values.put(DatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());
            values.put(DatabaseHelper.COLUMN_DURATION, 0);
            values.put(DatabaseHelper.COLUMN_TYPE, "Video Call");
            db.insert(DatabaseHelper.TABLE_CALL_HISTORY, null, values);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}

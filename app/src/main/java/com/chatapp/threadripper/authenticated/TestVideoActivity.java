package com.chatapp.threadripper.authenticated;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.chatapp.threadripper.R;
import com.chatapp.threadripper.utils.Constants;
import com.chatapp.threadripper.utils.ShowToast;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import net.majorkernelpanic.streaming.video.VideoQuality;

public class TestVideoActivity extends BaseMainActivity
        implements View.OnClickListener, Session.Callback, SurfaceHolder.Callback {


    static final String TAG = "TEST_LOG";


    private Button mButton1, mButton2;
    private SurfaceView mSurfaceView;
    private EditText mEditText;
    private Session mSession;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_video);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mEditText = (EditText) findViewById(R.id.editText1);

        handleCamera();

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.M) // >= 23
    void handleCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PERMISSION_IMAGE_CAPTURE);
        } else {
            mSession = SessionBuilder.getInstance()
                    .setCallback(this)
                    .setSurfaceView(mSurfaceView)
                    .setPreviewOrientation(90)
                    .setContext(getApplicationContext())
                    .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                    .setAudioQuality(new AudioQuality(16000, 32000))
                    .setVideoEncoder(SessionBuilder.VIDEO_H264)
                    .setVideoQuality(new VideoQuality(320, 240, 20, 500000))
                    .build();

            mSurfaceView.getHolder().addCallback(this);
        }
    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mSession.isStreaming()) {
            mButton1.setText("Stop");
        } else {
            mButton1.setText("Start");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSession.release();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button1) {
            // Starts/stops streaming
            mSession.setDestination(mEditText.getText().toString());
            if (!mSession.isStreaming()) {
                mSession.configure();
            } else {
                mSession.stop();
            }
            mButton1.setEnabled(false);
        } else {
            // Switch between the two cameras
            mSession.switchCamera();
        }
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        Log.d(TAG, "Bitrate: " + bitrate);
    }

    @Override
    public void onSessionError(int message, int streamType, Exception e) {
        mButton1.setEnabled(true);
        if (e != null) {
            logError(e.getMessage());
        }
    }

    @Override

    public void onPreviewStarted() {
        Log.d(TAG, "Preview started.");
    }

    @Override
    public void onSessionConfigured() {
        Log.d(TAG, "Preview configured.");
        // Once the stream is configured, you can get a SDP formated session description
        // that you can send to the receiver of the stream.
        // For example, to receive the stream in VLC, store the session description in a .sdp file
        // and open it with VLC while streming.
        Log.d(TAG, mSession.getSessionDescription());
        mSession.start();
    }

    @Override
    public void onSessionStarted() {
        Log.d(TAG, "Session started.");
        mButton1.setEnabled(true);
        mButton1.setText("Stop");
    }

    @Override
    public void onSessionStopped() {
        Log.d(TAG, "Session stopped.");
        mButton1.setEnabled(true);
        mButton1.setText("Start");
    }

    /**
     * Displays a popup to report the eror to the user
     */
    private void logError(final String msg) {
        final String error = (msg == null) ? "Error unknown" : msg;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSession.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSession.stop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_PERMISSION_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleCamera();
            } else {
                // the fucking user!!!
                ShowToast.lengthLong(this, "Camera perrimssion denied");

                // reset UI
                // edtMessage.setVisibility(View.VISIBLE);
                // rivImageIsPickedOrCaptured.setVisibility(View.GONE);
            }
        }
    }
}

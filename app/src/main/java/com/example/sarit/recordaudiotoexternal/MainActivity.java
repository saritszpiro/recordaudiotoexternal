package com.example.sarit.recordaudiotoexternal;

//import com.example.sarit.recordaudiotoexternal.*;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private ToggleButton startRecording, playRecording;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static final String LOG_TAG = "AudioRecording";
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean isRecording = false;
    boolean isPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sarit.recordaudiotoexternal.R.layout.activity_main);
        startRecording = findViewById(com.example.sarit.recordaudiotoexternal.R.id.startRecording);
        playRecording = findViewById(com.example.sarit.recordaudiotoexternal.R.id.playRecording);
        playRecording.setEnabled(false);

        //mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getAbsolutePath();

        Toast.makeText(MainActivity.this,mFileName.toString(),Toast.LENGTH_SHORT).show();
        mFileName += "/AudioRecording.3gp";

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length> 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    public void startPlaying(View view) {
        if (!isPlaying){
            startRecording.setEnabled(true);
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mPlayer.start();
                Toast.makeText(getApplicationContext(), "Recording Started Playing", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }

        }
        else if (isPlaying){
            mPlayer.release();
            mPlayer = null;
            Toast.makeText(getApplicationContext(),"Playing Audio Stopped", Toast.LENGTH_SHORT).show();
        }
    }
    public void startRecording(View view){
        if(CheckPermissions()) {
            if (!isRecording) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile(mFileName);
                Toast.makeText(MainActivity.this, mFileName.toString(), Toast.LENGTH_SHORT).show();
                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
                mRecorder.start();
                isRecording = true;
                Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
                playRecording.setEnabled(true);

            } else if (isRecording) {
                Toast.makeText(MainActivity.this, "Stopped recording", Toast.LENGTH_SHORT).show();
                 mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
            }
        }
        else {
                RequestPermissions();
        }
    }

    public void findButton(View v){
        switch (v.getId()) {

            case R.id.good_y:
                Toast.makeText(MainActivity.this, "good_y clicked", Toast.LENGTH_SHORT).show();

                break;

            case R.id.great_b:
                Toast.makeText(MainActivity.this, "great_b clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.knewit_x:
                Toast.makeText(MainActivity.this, "knewit_x clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.notuseful_a:
                Toast.makeText(MainActivity.this, "notuseful_a clicked", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }


    }

    public String updateTime() {
        //java.util.Date() method is used to get the current time
        String time = new Date().toString();
        Toast.makeText(getApplicationContext(),
                time, Toast.LENGTH_SHORT).show();
        return time;
    }
}


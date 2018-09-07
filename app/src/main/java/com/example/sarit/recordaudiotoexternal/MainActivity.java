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

import java.text.SimpleDateFormat;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.Format;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private ToggleButton startRecording, playRecording;
    private Button great_b, good_y, notuseful_a, knewit_x;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static final String LOG_TAG = "AudioRecording";
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean isRecording = false;
    boolean isPlaying = false;
    private static String mFileName = null;
    private static String dataFile = null;
    private String[] args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sarit.recordaudiotoexternal.R.layout.activity_main);
        startRecording = findViewById(com.example.sarit.recordaudiotoexternal.R.id.startRecording);
        playRecording = findViewById(com.example.sarit.recordaudiotoexternal.R.id.playRecording);
        great_b = findViewById(R.id.great_b);
        good_y = findViewById(R.id.good_y);
        notuseful_a = findViewById(R.id.notuseful_a);
        knewit_x = findViewById(R.id.knewit_x);
        great_b.setEnabled(false);
        good_y.setEnabled(false);
        notuseful_a.setEnabled(false);
        knewit_x.setEnabled(false);
        playRecording.setEnabled(false);

        String expTime = updateTime();
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
        //Toast.makeText(MainActivity.this,mFileName.toString(),Toast.LENGTH_SHORT).show();
        mFileName += "/AudioRecording_" + expTime + "_.3gp";

        dataFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        //Toast.makeText(MainActivity.this,dataFile.toString(),Toast.LENGTH_SHORT).show();
        dataFile += "/SaritData_" + expTime + ".txt";

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
            if (!isRecording) { // if recording audio
                great_b.setEnabled(true);
                good_y.setEnabled(true);
                notuseful_a.setEnabled(true);
                knewit_x.setEnabled(true);

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

            } else if (isRecording) { //if not recording audio
                great_b.setEnabled(false);
                good_y.setEnabled(false);
                notuseful_a.setEnabled(false);
                knewit_x.setEnabled(false);
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                isRecording = false;
                Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
            }
        }
        else {
                RequestPermissions();
        }
    }

    public void findButton(View v){
        String time = updateTime();
        String button_type = null;
        switch (v.getId()) {
            case R.id.great_b:
                //Toast.makeText(MainActivity.this, "great_b clicked", Toast.LENGTH_SHORT).show();
                MediaPlayer mp4 = MediaPlayer.create(MainActivity.this, R.raw.beep4);
                mp4.start();
                button_type = "great_b";
                break;

            case R.id.good_y:
                //Toast.makeText(MainActivity.this, "good_y clicked", Toast.LENGTH_SHORT).show();
                button_type = "good_y";
                MediaPlayer mp3 = MediaPlayer.create(MainActivity.this, R.raw.beep3);
                mp3.start();
                break;

            case R.id.knewit_x:
                //Toast.makeText(MainActivity.this, "knewit_x clicked", Toast.LENGTH_SHORT).show();
                MediaPlayer mp2 = MediaPlayer.create(MainActivity.this, R.raw.beep2);
                mp2.start();
                button_type = "knewit_x";
                break;

            case R.id.notuseful_a:
                //Toast.makeText(MainActivity.this, "notuseful_a clicked", Toast.LENGTH_SHORT).show();
                MediaPlayer mp1 = MediaPlayer.create(MainActivity.this, R.raw.beep1);
                mp1.start();
                button_type = "notuseful_a";
                break;

            default:
                break;
        }

        String toprint = button_type + "\t" +time+"\n";
        appendData(toprint);
    }

    public void appendData(String toprint){
        try {
            FileOutputStream outputWriter = new FileOutputStream(dataFile, true);
            Toast.makeText(getApplicationContext(), "msg to be written" + toprint, Toast.LENGTH_SHORT).show();

            outputWriter.write(toprint.toString().getBytes());
            PrintWriter pw = new PrintWriter(outputWriter);
            //pw.println("Hi , How are you");
            pw.flush();
            pw.close();
            Log.d(LOG_TAG, toprint);
            outputWriter.close();
            Log.d(LOG_TAG, "file closed");
            //Toast.makeText(getApplicationContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG,"\n\nFile written to");

    }

    public String updateTime() {
        Date time = new Date();
        Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
        String timestamp = formatter.format(time);
        Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();

        return timestamp;
    }


}


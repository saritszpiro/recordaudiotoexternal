package com.example.sarit.recordaudiotoexternal;

//import com.example.sarit.recordaudiotoexternal.*;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.KeyEvent.KEYCODE_BUTTON_A;
import static android.view.KeyEvent.KEYCODE_BUTTON_B;
import static android.view.KeyEvent.KEYCODE_BUTTON_X;
import static android.view.KeyEvent.KEYCODE_BUTTON_Y;

public class MainActivity extends AppCompatActivity {
    private ToggleButton startRecording;
    private Button great_b, good_y, notuseful_a, knewit_x;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private static final String LOG_TAG = "AudioRecording";
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean isRecording = false;
    boolean loaded = false;
    private  String mFileName = null;
    private  String dataFile = null;
    //private String[] args;
    private SoundPool soundPool;
    private int beep1, beep2, beep3,beep4;
    EditText participantName;
    private String text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sarit.recordaudiotoexternal.R.layout.activity_main);
        startRecording = findViewById(com.example.sarit.recordaudiotoexternal.R.id.startRecording);
        great_b = findViewById(R.id.great_b);
        good_y = findViewById(R.id.good_y);
        notuseful_a = findViewById(R.id.notuseful_a);
        knewit_x = findViewById(R.id.knewit_x);
        great_b.setEnabled(false);
        good_y.setEnabled(false);
        notuseful_a.setEnabled(false);
        knewit_x.setEnabled(false);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int mySoundId, int status) {
                loaded = true;
            }
        });
        beep1 = soundPool.load(this, R.raw.beep1, 1);
        beep2 = soundPool.load(this, R.raw.beep2, 1);
        beep3 = soundPool.load(this, R.raw.beep3, 1);
        beep4 = soundPool.load(this, R.raw.beep4, 1);

        participantName = (EditText) findViewById(R.id.participantName);
        participantName.setHint("Name");
        //setContentView(R.layout.activity_main);
        participantName.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Toast.makeText(MainActivity.this,participantName.getText().toString(),Toast.LENGTH_SHORT).show();
                    String text= participantName.getEditableText().toString();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(participantName.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });
        //Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();

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

    public void startRecording(View view){
        text = participantName.getEditableText().toString();
        //Toast.makeText(MainActivity.this, "in startRecording", Toast.LENGTH_SHORT).show();
        String expTime = updateTime();
        mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
        //Toast.makeText(MainActivity.this,mFileName.toString(),Toast.LENGTH_SHORT).show();
        mFileName += "/AudioRecording_" + text + "_" + expTime + "_.3gp";

        dataFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        //Toast.makeText(MainActivity.this,dataFile.toString(),Toast.LENGTH_SHORT).show();
        dataFile += "/SaritData_" + text + "_" + expTime + ".txt";

        if(CheckPermissions()) {
            if (!isRecording) { // if recording audio
                great_b.setEnabled(true);
                good_y.setEnabled(true);
                notuseful_a.setEnabled(true);
                knewit_x.setEnabled(true);
                participantName.setEnabled(false);

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
                //Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();

            } else if (isRecording) { //if not recording audio
                great_b.setEnabled(false);
                good_y.setEnabled(false);
                notuseful_a.setEnabled(false);
                knewit_x.setEnabled(false);
                participantName.setEnabled(true);
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                isRecording = false;
                //Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
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
                //Toast.makeText(MainActivity.this, "find button great_b clicked", Toast.LENGTH_SHORT).show();
                soundPool.play(beep4, 1F, 1F, 1, 0, 1f);
                button_type = "great_b";
                break;

            case R.id.good_y:
                //Toast.makeText(MainActivity.this, "find button good_y clicked", Toast.LENGTH_SHORT).show();
                button_type = "good_y";
                soundPool.play(beep3, 1F, 1F, 1, 0, 1f);
                break;

            case R.id.knewit_x:
                //Toast.makeText(MainActivity.this, "find button knewit_x clicked", Toast.LENGTH_SHORT).show();
                soundPool.play(beep2, 1F, 1F, 1, 0, 1f);
                button_type = "knewit_x";
                break;

            case R.id.notuseful_a:
                //Toast.makeText(MainActivity.this, "find button notuseful_a clicked", Toast.LENGTH_SHORT).show();
                soundPool.play(beep1, 1F, 1F, 1, 0, 1f);
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
            //Toast.makeText(getApplicationContext(), "msg to be written " + dataFile.toString(), Toast.LENGTH_SHORT).show();
            FileOutputStream outputWriter = new FileOutputStream(dataFile, true);
            outputWriter.write(toprint.toString().getBytes());
            //outputWriter.println("Hi , How are you");
            outputWriter.flush();
            outputWriter.close();
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
        //Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();

        return timestamp;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        String time = updateTime();
        String button_type = null;

        if ((event.getSource() & android.view.InputDevice.SOURCE_GAMEPAD)
                == android.view.InputDevice.SOURCE_GAMEPAD) {
            if ((event.getRepeatCount() == 0) & isRecording ){
                switch (keyCode) {
                    case KEYCODE_BUTTON_B:
                        Toast.makeText(MainActivity.this, "key down great_b clicked", Toast.LENGTH_SHORT).show();
                        soundPool.play(beep4, 1F, 1F, 1, 0, 1f);
                        button_type = "great_b";
                        return true;

                    case KEYCODE_BUTTON_Y:
                        Toast.makeText(MainActivity.this, "Key down good_y clicked", Toast.LENGTH_SHORT).show();
                        button_type = "good_y";
                        soundPool.play(beep3, 1F, 1F, 1, 0, 1f);
                        return true;

                    case KEYCODE_BUTTON_X:
                        Toast.makeText(MainActivity.this, "Key Down knewit_x clicked", Toast.LENGTH_SHORT).show();
                        soundPool.play(beep2, 1F, 1F, 1, 0, 1f);
                        button_type = "knewit_x";
                        return true;

                    case KEYCODE_BUTTON_A:
                        Toast.makeText(MainActivity.this, "Key Down notuseful_a clicked", Toast.LENGTH_SHORT).show();
                        soundPool.play(beep1, 1F, 1F, 1, 0, 1f);
                        button_type = "notuseful_a";
                        return true;

                }
                String toprint = button_type + "\t" +time+"\n";
                appendData(toprint);
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(MainActivity.this, "Back pressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        soundPool.release();
        soundPool = null;
    }
}


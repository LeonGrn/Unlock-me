package com.example.unlockme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    private ProgressBar main_progress_bar;
    private Button button_unlock;
    private TextView text_view_progress;

    private int countProgress = 0;
    private boolean batteryMissionComplete = false;
    private boolean touchMissionComplete = false;
    private boolean humadityMissionComplete = false;
    private boolean temperatureMissionComplete = false;
    private boolean lightMissionComplete = false;

    private Security.Sensors sensorsSecurity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        sensorsSecurity = new Security.Sensors(this);

        text_view_progress.setText("0%");
        button_unlock.setOnClickListener(nextLevel);

    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {

            if(Security.getBatteryPercentage(ctxt) < 20 && !batteryMissionComplete)
            {
                updateProgressBar();
                batteryMissionComplete = true;
            }
        }
    };

    @Override
    public boolean onTouchEvent(final MotionEvent event)
    {
        if(event.getPointerCount() == 1 && !touchMissionComplete){
            updateProgressBar();
            touchMissionComplete = true;
            return true;
        }
        return false;
    }

    View.OnClickListener nextLevel = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Toast.makeText(getApplicationContext(), "Ops, to unlock the page reach 100%",
                    Toast.LENGTH_LONG).show();
            login();
            if(countProgress == 100){
                button_unlock.setEnabled(false);
                MyVibrator.vibrate(getApplicationContext());
                nextScreen();
            }
        }
    };

    private void login(){
        if(sensorsSecurity.checkHumidity() && !humadityMissionComplete){
            Toast.makeText(this,String.format(Locale.ENGLISH,
                    "Make sure humidity is between %d%% and %d%%",Security.Sensors.MIN_HUMIDITY,Security.Sensors.MAX_HUMIDITY),Toast.LENGTH_SHORT).show();
            humadityMissionComplete = true;
            updateProgressBar();
            return;
        }
        if(sensorsSecurity.checkTemperature() && !temperatureMissionComplete){
            Toast.makeText(this,String.format(Locale.ENGLISH,
                    "Make sure temperature is between %dC and %dC",Security.Sensors.MIN_TEMP,Security.Sensors.MAX_TEMP),Toast.LENGTH_SHORT).show();
            temperatureMissionComplete = true;
            updateProgressBar();
            return;
        }
        if(sensorsSecurity.checkLight() && !lightMissionComplete){
            Toast.makeText(this,String.format(Locale.ENGLISH,
                    "Make sure temperature is between %dC and %dC",Security.Sensors.MIN_LIGHT,Security.Sensors.MAX_LIGHT),Toast.LENGTH_SHORT).show();
            lightMissionComplete = true;
            updateProgressBar();
            return;
        }
    }

    private void updateProgressBar(){
        main_progress_bar.setProgress(countProgress += 20);
        text_view_progress.setText(countProgress + "%");
    }


    private void nextScreen(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this,SuccessScreen.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    private void init(){
        main_progress_bar = findViewById(R.id.progress_bar);
        button_unlock = findViewById(R.id.button_unlock);
        text_view_progress = findViewById(R.id.text_view_progress);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(this.mBatInfoReceiver);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorsSecurity.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorsSecurity.onPause();
    }
}
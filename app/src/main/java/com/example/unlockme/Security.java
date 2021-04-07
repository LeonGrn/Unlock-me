package com.example.unlockme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.util.Log;

import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;
import static android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static android.hardware.Sensor.TYPE_LIGHT;

public class Security {

    public static final int MAX_BATTERY = 25, MIN_BATTERY = 5;

    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }

    public static class Sensors implements SensorEventListener {
        public static final int MAX_HUMIDITY = 90, MIN_HUMIDITY = 75;
        public static final int MAX_TEMP = 99, MIN_TEMP = 15;
        public static final int MAX_LIGHT = 15000, MIN_LIGHT = 10000;

        private Activity activity;
        private SensorManager sensorManager;
        private Sensor humiditySensor, temperatureSensor, lightSensor;
        private float humidity = 0 , temperature = 0 , light = 0;

        public Sensors(Activity activity) {
            this.activity = activity;
            sensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);

            humiditySensor = sensorManager.getDefaultSensor(TYPE_RELATIVE_HUMIDITY);
            temperatureSensor = sensorManager.getDefaultSensor(TYPE_AMBIENT_TEMPERATURE);
            lightSensor = sensorManager.getDefaultSensor(TYPE_LIGHT);

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                switch (event.sensor.getType()) {
                    case TYPE_RELATIVE_HUMIDITY:
                        humidity = event.values[0];
                        break;
                    case TYPE_AMBIENT_TEMPERATURE:
                        temperature = event.values[0];
                        break;
                    case TYPE_LIGHT:
                        light = event.values[0];
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public boolean checkLight(){
            return light <= MAX_LIGHT && light >= MIN_LIGHT;
        }

        public boolean checkHumidity(){
            return humidity <= MAX_HUMIDITY && humidity >= MIN_HUMIDITY;
        }

        public boolean checkTemperature(){
            return temperature <= MAX_TEMP && temperature >= MIN_TEMP;
        }

        public void onResume() {
        sensorManager.registerListener(this,temperatureSensor,SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, humiditySensor, SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,lightSensor,SENSOR_DELAY_NORMAL);
        }

        public void onPause() {
          sensorManager.unregisterListener(this,temperatureSensor);
          sensorManager.unregisterListener(this, humiditySensor);
          sensorManager.unregisterListener(this,lightSensor);
        }
    }
}

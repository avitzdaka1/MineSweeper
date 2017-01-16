package com.omeryaari.minesweeper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class AccelerometerService extends Service implements SensorEventListener{

    private AccelerometerServiceBinder accelerometerServiceBinder;
    private AccelerometerListener listener;
    private SensorManager sensorManager;

    public class AccelerometerServiceBinder extends Binder {
        public AccelerometerService getService() {
            return AccelerometerService.this;
        }
    }

    public interface AccelerometerListener {
        void onAccelerometerSensorEvent(float[] values);
    }

    public void setListener(AccelerometerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (listener != null)
                listener.onAccelerometerSensorEvent(event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        accelerometerServiceBinder = new AccelerometerServiceBinder();
        return accelerometerServiceBinder;
    }


    public void startListening() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stopListening() {
        sensorManager.unregisterListener(this);
    }
}

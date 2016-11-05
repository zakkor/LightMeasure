package com.eddie.ed.lightmeasure;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import java.util.Vector;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView current;
    private TextView average;
    private int flashes = 0;

    private SensorManager mSensorManager;
    private Sensor mLight;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 1000; //1 second

    private float currentReading = 0;
    private float lastReading = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        current = (TextView)findViewById(R.id.current);
        if (current != null) {
            current.setText("Merge boss");
        }

        average = (TextView)findViewById(R.id.average);
        if (average != null) {
            average.setText("Merge boss");
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        handler.postDelayed(new Runnable() {
            public void run() {
                average.setText(Integer.toString(flashes));

                flashes = 0;

                runnable=this;

                handler.postDelayed(runnable, delay);
            }
        }, delay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            currentReading = event.values[0];

            if (currentReading - lastReading >= 18) {
                flashes++;
            }

            current.setText(Float.toString(currentReading));

            lastReading = currentReading;
        }
    }
}

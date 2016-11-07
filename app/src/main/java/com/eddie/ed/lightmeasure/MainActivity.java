package com.eddie.ed.lightmeasure;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    private Handler barhandler = new Handler();
    private Runnable barrunnable;
    private int bardelay = 100; //1 second

    private float currentReading = 0;
    private float lastReading = 0;

    private ProgressBar bar;

    private EditText editText;

    private int threshold = 150;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        if (editText != null) {
            editText.setText("150");
        }

        current = (TextView)findViewById(R.id.current);
        if (current != null) {
            current.setText("Merge boss");
        }

        average = (TextView)findViewById(R.id.average);
        if (average != null) {
            average.setText("Merge boss");
        }

        bar = (ProgressBar)findViewById(R.id.bar);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        handler.postDelayed(new Runnable() {
            public void run() {
                average.setText(Integer.toString(flashes));

                flashes = 0;

                runnable=this;

                bar.setProgress(0);

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        barhandler.postDelayed(new Runnable() {
            public void run() {
                barrunnable=this;

                bar.setProgress(bar.getProgress() + 10);

                barhandler.postDelayed(barrunnable, bardelay);
            }
        }, bardelay);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    threshold = Integer.valueOf(editText.getText().toString());

                    if(getCurrentFocus()!=null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
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

            if (currentReading - lastReading >= threshold) {
                flashes++;
            }

            current.setText(Float.toString(currentReading));

            lastReading = currentReading;
        }
    }
}

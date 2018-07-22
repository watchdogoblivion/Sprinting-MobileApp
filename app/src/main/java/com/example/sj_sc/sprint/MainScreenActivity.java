package com.example.sj_sc.sprint;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainScreenActivity extends AppCompatActivity implements SensorEventListener{

    private TextView time ;
    private Button start, stop, reset;
    private long milliSecondsTime, startClock, timeBuffer, updateTime = 0L ;
    private int minutes, seconds, milliSeconds;
    private ListView lapTimesView;
    private List<String> lapTimesList;
    private LapTimesAdapter adapter ;
    private boolean startStop = true;
    private boolean startNoStop = false;
    private Handler stopWatchHandler;

    private long internalMSTime, internalStartClock, internalTimeBuffer, internalUpdateTime = 0L ;
    private int  internalMinutes,internalSeconds, internalMS;
    private Handler internalTimeHandler;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Handler sensorHandler;

    static float triggerStart = 5.0f; //determines acceleration value that starts stopwatch
    static float triggerStop = 4.0f;  //determines acceleration value that stops stopwatch

    //determines delay between acceleration readings, use to modify how long
    //it takes until the stop reaction is triggered in onSensorChanged
    static int delayMilliSeconds = 0;
    static int delaySeconds = 1;
    static int delayMinutes = 0;

    //For high pass filter
    private final float[] gravity = new float[]{ 0, 0, 0 };
    private final float[] linearAcceleration = new float[]{ 0, 0, 0 };


    private final Runnable processSensors = new Runnable() {
        @Override
        public void run() {
            // Determines how long until sensor processes again
            int delayRead = 1000;
            sensorHandler.postDelayed(this, delayRead);
        }
    };

    private final Runnable internalTime = new Runnable() {
        @Override
        public void run() {
            internalMSTime = SystemClock.uptimeMillis() - internalStartClock;
            internalUpdateTime = internalTimeBuffer + internalMSTime;
            internalSeconds = (int) (internalUpdateTime / 1000);
            internalMinutes = internalSeconds / 60;
            internalSeconds = internalSeconds % 60;
            internalMS = (int) (internalUpdateTime % 1000);
            internalTimeHandler.postDelayed(this, 0);
        }

    };

    private final Runnable displayTime = new Runnable() {
        @Override
        public void run() {
            milliSecondsTime = SystemClock.uptimeMillis() - startClock;
            updateTime = timeBuffer + milliSecondsTime;
            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            milliSeconds = (int) (updateTime % 1000);
            time.setText(String.format(Locale.ENGLISH,"%s:%s:%s",
                    String.format(Locale.ENGLISH, "%02d", minutes),
                    String.format(Locale.ENGLISH, "%02d", seconds),
                    String.format(Locale.ENGLISH,"%03d", milliSeconds)));
            stopWatchHandler.postDelayed(this, 0);
        }

    };

    private void initializeLayoutFields() {

        time = findViewById(R.id.time1);
        start = findViewById(R.id.startT);
        stop = findViewById(R.id.stopT);
        reset = findViewById(R.id.resetT);
        lapTimesView = findViewById(R.id.times);

        setLayoutButtonListeners();
    }

    private void setLayoutButtonListeners() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWatchTimer();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWatchTimer();
            }
        });
    }

    private void initializeFields() {
        //Stopwatch and internal time
        internalTimeHandler = new Handler();
        stopWatchHandler = new Handler() ;

        lapTimesList = new ArrayList<>();
        adapter = new LapTimesAdapter(MainScreenActivity.this, R.layout.sprint_item, lapTimesList);

        TextView textView = new TextView(this);
        String recordTimes = "   Recorded Times";
        textView.setTextColor(Color.WHITE);
        textView.setText(recordTimes);
        textView.setGravity(Gravity.START);
        textView.setTextSize(22);
        lapTimesView.addHeaderView(textView);
        lapTimesView.setAdapter(adapter);

        //Acceleration sensor
        sensorHandler = new Handler();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }else {
            Toast.makeText(this, "No accelerometer detected", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Drawable drawable = getDrawable(R.drawable.bar_background);
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(drawable);
        }
        initializeLayoutFields();
        initializeFields();
        Provider.populate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                launchSettings();
                break;
            case R.id.action_records:
                launchRecords();
                break;
            case R.id.action_instructions:
                launchInstructions();
                break;

        }

        return true;
    }

    private void launchInstructions() {
        Intent instructionsScreen = new Intent(this, Instructions.class);
        startActivity(instructionsScreen);
    }

    private void launchSettings() {
        Intent settingsScreen = new Intent(this, SettingsActivity.class);
        startActivity(settingsScreen);
    }

    private void launchRecords() {
        Intent recordsScreen = new Intent(this, RecordsActivity.class);
        startActivity(recordsScreen);
    }


    public void startReaction(View view) {
        sensorManager.registerListener(MainScreenActivity.this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorHandler.post(processSensors);

    }

    public void stopReaction(View view) {
        sensorManager.unregisterListener(MainScreenActivity.this);
    }

    private void stopReaction() {
        sensorManager.unregisterListener(MainScreenActivity.this);
    }

    public void startNoStopReaction(View view) {
        sensorManager.registerListener(MainScreenActivity.this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorHandler.post(processSensors);
        startNoStop = true;
    }

    //I created two timers because when the stop watch stops, the user may not want to reset it,
    // which means the variable delaySeconds may always yield true in the onSensorChanged method since it is
    // dependent on the time
    private void startInternalTimer() {
        internalStartClock = SystemClock.uptimeMillis();
        internalTimeHandler.postDelayed(internalTime, 0);
    }

    private void stopInternalTimer() {
        internalTimeBuffer += internalMSTime;
        internalTimeHandler.removeCallbacks(internalTime);
        resetInternalTimer();

        startStop = true;
        stopReaction();
        startNoStop = false;
    }

    private void resetInternalTimer() {
        internalMSTime = 0L ;
        internalStartClock = 0L ;
        internalTimeBuffer = 0L ;
        internalUpdateTime = 0L ;
        internalSeconds = 0 ;
        internalMinutes = 0 ;
        internalMS = 0 ;
    }

    private void startWatchTimer() {
        startInternalTimer();

        startClock = SystemClock.uptimeMillis();
        stopWatchHandler.postDelayed(displayTime, 0);

        reset.setEnabled(false);

        startStop = false;
    }

    private void stopWatchTimer() {
        stopInternalTimer();

        timeBuffer += milliSecondsTime;
        stopWatchHandler.removeCallbacks(displayTime);

        reset.setEnabled(true);

        startStop = true; //for reaction, allows next call of start reaction to work

        stopReaction();
        startNoStop = false; //for manual stop
    }

    public void lapWatchTimer(View view) {
        lapTimesList.add(time.getText().toString());

        adapter.notifyDataSetChanged();
    }

    public void resetWatchTimer(View view) {
        milliSecondsTime = 0L ;
        startClock = 0L ;
        timeBuffer = 0L ;
        updateTime = 0L ;
        seconds = 0 ;
        minutes = 0 ;
        milliSeconds = 0 ;

        time.setText(R.string.time_zero);
    }

    public void removeLaps(View view) {
        lapTimesList.clear();
        adapter.notifyDataSetChanged();
    }

    //the high pass filter is necessary because the phone orientation dictates the xyz axises and
    //the average is also influenced by them, Example from the Android developers documentation
    @Override
    synchronized public void onSensorChanged(SensorEvent event) {

        float alpha = 0.9f;
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linearAcceleration[0] = event.values[0] - gravity[0];
        linearAcceleration[1] = event.values[1] - gravity[1];
        linearAcceleration[2] = event.values[2] - gravity[2];

        float averageAFinal = (Math.abs(linearAcceleration[0]) +
                Math.abs(linearAcceleration[1]) + Math.abs(linearAcceleration[2]))/3;
        float averageAInitial = 0.25f;

        int totalInternalTime = internalMinutes*100000 + internalSeconds*1000 + internalMS;
        int totalDelayTIme = delayMinutes*100000 + delaySeconds*1000 + delayMilliSeconds;

        if(averageAFinal - averageAInitial >= triggerStart && startStop){

            startWatchTimer();

        } else if(averageAFinal - averageAInitial >= triggerStop && !startStop && !startNoStop &&
                totalInternalTime >= totalDelayTIme){

            stopWatchTimer();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void saveSprint(View view){
        RelativeLayout rL = (RelativeLayout)view.getParent();
        TextView lapTimeView = (TextView)rL.getChildAt(1);
        EditText editDistanceText = (EditText)rL.getChildAt(3);

        int distance;
        if(!editDistanceText.getText().toString().isEmpty()){
             distance = Integer.parseInt(editDistanceText.getText().toString());
        }else{
            distance = 0;
        }

        Sprint sprintT = new Sprint(lapTimeView.getText().toString(), distance);

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.SPRINT_TIME, sprintT.getTime());
        values.put(DBOpenHelper.SPRINT_DISTANCE, sprintT.getDistance());
        values.put(DBOpenHelper.SPRINT_SPEED, sprintT.getSpeed());
        values.put(DBOpenHelper.SPRINT_DATE_CREATED, sprintT.getDateCreated());
        values.put(DBOpenHelper.SPRINT_DATE_ONLY, sprintT.getDateOnly());

        Uri sprintUri = getContentResolver().insert(Provider.SPRINTS_URI, values);

        if (sprintUri != null) {
            sprintT.setSprint_ID(Integer.parseInt(sprintUri.getLastPathSegment()));
        }
        Sprint.SPRINT_MAP.put(sprintT.getSprint_ID(), sprintT);
        Button button = (Button)rL.getChildAt(5);
        button.setEnabled(false);
    }

}
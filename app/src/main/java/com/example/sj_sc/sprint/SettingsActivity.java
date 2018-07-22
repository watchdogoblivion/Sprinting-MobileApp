package com.example.sj_sc.sprint;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private EditText startingR;
    private EditText stoppingR;
    private EditText delayM;
    private EditText delayS;
    private EditText delayMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initializeFields();
    }

    private void initializeFields() {
        startingR = findViewById(R.id.editStartingReaction);
        stoppingR = findViewById(R.id.editStoppingReaction);
        delayM = findViewById(R.id.editDelayM);
        delayS = findViewById(R.id.editDelayS);
        delayMS = findViewById(R.id.editDelayMS);

        startingR.setText(String.format(Locale.ENGLISH, "%.2f", MainScreenActivity.triggerStart));
        stoppingR.setText(String.format(Locale.ENGLISH, "%.2f",MainScreenActivity.triggerStop));
        delayM.setText(String.format(Locale.ENGLISH,"%d",MainScreenActivity.delayMinutes));
        delayS.setText(String.format(Locale.ENGLISH,"%d",MainScreenActivity.delaySeconds));
        delayMS.setText(String.format(Locale.ENGLISH,"%d",MainScreenActivity.delayMilliSeconds));
    }


    public void save(View view) {

        String tStart = startingR.getText().toString().trim();
        String tStop = stoppingR.getText().toString().trim();
        String dM = delayM.getText().toString().trim();
        String dS = delayS.getText().toString().trim();
        String dMS = delayMS.getText().toString().trim();

        if(!tStart.isEmpty() && !tStop.isEmpty() && !dM.isEmpty() && !dS.isEmpty() && !dMS.isEmpty()){
            int tempM = Integer.parseInt(dM);
            int tempS = Integer.parseInt(dS);
            int tempMS = Integer.parseInt(dMS);

            if(tempM <= 59 && tempS <= 59 && tempMS <= 999 ){
                MainScreenActivity.triggerStart = Float.parseFloat(tStart);
                MainScreenActivity.triggerStop = Float.parseFloat(tStop);
                MainScreenActivity.delayMinutes = tempM;
                MainScreenActivity.delaySeconds = tempS;
                MainScreenActivity.delayMilliSeconds = tempMS;

                if(Settings.SETTINGS_ARRAY_LIST.isEmpty()){
                    insertData(Float.parseFloat(tStart), Float.parseFloat(tStop),
                            tempM, tempS, tempMS);
                }else{
                    updateData(Float.parseFloat(tStart), Float.parseFloat(tStop),
                            tempM, tempS, tempMS);
                }
                Toast.makeText(this, "Changes have been saved", Toast.LENGTH_SHORT).show();

                finish();
            } else {
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("The maximum minutes are 59, the maximum seconds are 59, " +
                        "and the maximum milliseconds are 999.")
                        .setNeutralButton(getString(android.R.string.ok), dialogClickListener)
                        .show();
            }
        } else {
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                        }
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please fill out everything.")
                    .setNeutralButton(getString(android.R.string.ok), dialogClickListener)
                    .show();
        }

    }

    private void updateData(float tStart, float tStop, int delayM, int delayS, int delayMS) {
        Settings settings = Settings.SETTINGS_ARRAY_LIST.get(0);
        settings.setStartingR(tStart);
        settings.setStoppingR(tStop);
        settings.setDelayM(delayM);
        settings.setDelayS(delayS);
        settings.setDelayMS(delayMS);

        String whereClause = DBOpenHelper.SETTINGS_ID + "=" + settings.getSettingsID();
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.STARTING_REACTION, tStart);
        values.put(DBOpenHelper.STOPPING_REACTION, tStop);
        values.put(DBOpenHelper.DELAY_MINUTES, delayM);
        values.put(DBOpenHelper.DELAY_SECONDS, delayS);
        values.put(DBOpenHelper.DELAY_MILLIS, delayMS);

        getContentResolver().update(Provider.SETTINGS_URI, values, whereClause, null);
    }

    private void insertData(float tStart, float tStop, int delayM, int delayS, int delayMS) {
        Settings settings = new Settings(tStart, tStop, delayM, delayS, delayMS);

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.STARTING_REACTION, tStart);
        values.put(DBOpenHelper.STOPPING_REACTION, tStop);
        values.put(DBOpenHelper.DELAY_MINUTES, delayM);
        values.put(DBOpenHelper.DELAY_SECONDS, delayS);
        values.put(DBOpenHelper.DELAY_MILLIS, delayMS);

        Uri settingsUri = getContentResolver().insert(Provider.SETTINGS_URI, values);


        if (settingsUri != null) {
            settings.setSettingsID(Integer.parseInt(settingsUri.getLastPathSegment()));
        }
        Settings.SETTINGS_ARRAY_LIST.add(settings);
    }


}


package com.example.sj_sc.sprint;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

public class RecordsActivity extends AppCompatActivity {

    private static final int RECORD_REQUEST_CODE = 1000 ;
    private ArrayList<DateData> dates;
    private MCalendarView simpleCalendarView;
    private Button label2;
    private String dateSprint;
    private DateData now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Drawable drawable = getDrawable(R.drawable.bar_background);
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(drawable);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initialize();

        Calendar today = Calendar.getInstance();
        now = new DateData(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH));

        arraySetup();

        simpleCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {

                if(date.getMarkStyle().getColor() != Color.GREEN){
                    simpleCalendarView.unMarkDate(now);
                    date.getMarkStyle().setColor(Color.BLUE);
                    simpleCalendarView.markDate(date);
                    now = date;
                } else{
                    simpleCalendarView.unMarkDate(now);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(date.getYear(), date.getMonth() -1, date.getDay());
                label2.setText(String.format(Locale.ENGLISH, "%1$tA %1$tb %1$td %1$tY", calendar));
                dateSprint = String.format(Locale.ENGLISH, "%1$tm %1$td %1$tY", calendar);
            }
        });
    }

    private void arraySetup() {
        dates = new ArrayList<>();

        for (Sprint s: Sprint.SPRINT_MAP.values()){
            int year = s.getCalDateCreated().get(Calendar.YEAR);
            int month = s.getCalDateCreated().get(Calendar.MONTH) +1; //added one because m_calendar_view calendar automatically subtracts one
            int day = s.getCalDateCreated().get(Calendar.DAY_OF_MONTH);
            DateData dd = new DateData(year, month, day);
            dates.add(dd);

        }

        for (DateData d: dates){
            d.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.GREEN));
            simpleCalendarView.markDate(d);
        }
    }


    private void initialize(){
        simpleCalendarView = findViewById(R.id.simpleCalendarView);
        label2 = findViewById(R.id.label2);
    }

    public void launchRecord(View view) {
        if(dateSprint != null && !dateSprint.isEmpty()) {
            Intent recordScreen = new Intent(RecordsActivity.this, RecordActivity.class);
            recordScreen.putExtra(Provider.SPRINTS_ITEM_TYPE, dateSprint);
            startActivityForResult(recordScreen, RECORD_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK){
            for (DateData d: dates){
                simpleCalendarView.unMarkDate(d);
            }
            dates.clear();
            arraySetup();
        }
    }
}

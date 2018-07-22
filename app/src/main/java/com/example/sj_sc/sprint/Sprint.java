package com.example.sj_sc.sprint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

class Sprint {

    private int sprint_ID = 0;
    private String time;
    private int minutes;
    private int seconds;
    private int millis;
    private int distance;
    private double speed;
    private String dateCreated;
    private Calendar calDateCreated;
    private String dateOnly;


    public final static Map<Integer, Sprint> SPRINT_MAP = new ConcurrentSkipListMap<>();

    Sprint(String time, int distance) {
        this.time = time;
        if(!time.isEmpty()) {
            this.minutes = Integer.parseInt(time.substring(0, time.indexOf(":")));
            this.seconds = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.lastIndexOf(":")));
            this.millis = Integer.parseInt(time.substring(time.lastIndexOf(":") + 1));
        }
        this.distance = distance;
        this.speed = distance/((minutes*60) + seconds + (millis*.001));
        this.calDateCreated = Calendar.getInstance();
        this.dateCreated = String.format(Locale.ENGLISH, "%1$tm %1$td %1$tY %1$tH %1$tM %1$tS", calDateCreated);
        this.dateOnly = String.format(Locale.ENGLISH, "%1$tm %1$td %1$tY", calDateCreated);
    }

    @Override
    public String toString() {
        return "\nSprint ID: " + sprint_ID + "\nTime: " +
                time +  "\nDistance: " + distance + "\nSpeed: " + speed +
                "\nDate Created: " + dateCreated;
    }


    public int getSprint_ID(){ return sprint_ID; }
    public void setSprint_ID(int sprint_ID){ this.sprint_ID = sprint_ID; }

    public String getTime (){
        return time;
    }
    public void setTime(String time){
        this.time = time;
        this.minutes = Integer.parseInt(time.substring(0, time.indexOf(":")));
        this.seconds = Integer.parseInt(time.substring(time.indexOf(":") + 1, time.lastIndexOf(":")));
        this.millis = Integer.parseInt(time.substring(time.lastIndexOf(":") + 1));
    }

    public int getDistance (){
        return distance;
    }
    public void setDistance (int distance){
        this.distance = distance;
        this.speed = distance/((minutes*60) + seconds + (millis*.001));
    }

    public double getSpeed (){
        return speed;
    }
    public void setSpeed (int speed){
        this.speed = speed;
    }

    public String getDateCreated (){ return dateCreated; }
    public String getDateOnly(){return dateOnly;}
    public Calendar getCalDateCreated(){ return calDateCreated; }

    public void setDate(String dateCreated){
        Calendar calDateCreated = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd yyyy HH mm ss", Locale.ENGLISH);
        try {
            calDateCreated.setTime(simpleDateFormat.parse(dateCreated));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.calDateCreated = calDateCreated;
        this.dateCreated = dateCreated;
        this.dateOnly = String.format(Locale.ENGLISH, "%1$tm %1$td %1$tY", calDateCreated);

    }
    public void setDate(Calendar calDateCreated){
        this.dateCreated = String.format(Locale.ENGLISH, "%1$tm %1$td %1$tY %1$tH %1$tM %1$tS", calDateCreated);
        this.dateOnly = String.format(Locale.ENGLISH, "%1$tm %1$td %1$tY", calDateCreated);
        this.calDateCreated = calDateCreated;
    }


}

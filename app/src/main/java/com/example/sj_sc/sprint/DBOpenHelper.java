package com.example.sj_sc.sprint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBOpenHelper extends SQLiteOpenHelper {
    //Constants for db name and version
    private static final String DATABASE_NAME = "sprint_tracker.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying table and columns

    public static final String TABLE_SPRINTS = "sprint";
    public static final String SPRINT_ID = "_id";
    public static final String SPRINT_TIME = "time";
    public static final String SPRINT_DISTANCE = "sprint_distance";
    public static final String SPRINT_SPEED = "sprint_speed";
    public static final String SPRINT_DATE_CREATED = "sprint_date_created";
    public static final String SPRINT_DATE_ONLY = "sprint_date_Only";
    public static final String[] SPRINTS_COLUMNS =
            {SPRINT_ID, SPRINT_TIME, SPRINT_DISTANCE, SPRINT_SPEED, SPRINT_DATE_CREATED, SPRINT_DATE_ONLY};

    public static final String TABLE_SETTINGS = "settings";
    public static final String SETTINGS_ID = "_id";
    public static final String STARTING_REACTION = "starting_reaction";
    public static final String STOPPING_REACTION = "stopping_reaction";
    public static final String DELAY_MINUTES = "delay_minutes";
    public static final String DELAY_SECONDS = "delay_seconds";
    public static final String DELAY_MILLIS = "delay_millis";
    public static final String[] SETTINGS_COLUMNS =
            {SETTINGS_ID, STARTING_REACTION, STOPPING_REACTION, DELAY_MINUTES, DELAY_SECONDS, DELAY_MILLIS};

    //SQL to create table
    private static final String TABLE_CREATE_SPRINTS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SPRINTS + " (" +
                    SPRINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SPRINT_TIME + " TEXT, " +
                    SPRINT_DISTANCE + " INTEGER, " +
                    SPRINT_SPEED + " INTEGER, " +
                    SPRINT_DATE_CREATED + " TEXT, " +
                    SPRINT_DATE_ONLY + " TEXT " +
                    ")";

    private static final String TABLE_CREATE_SETTINGS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS + " (" +
                    SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STARTING_REACTION + " FLOAT, " +
                    STOPPING_REACTION + " FLOAT, " +
                    DELAY_MINUTES + " INTEGER, " +
                    DELAY_SECONDS + " INTEGER, " +
                    DELAY_MILLIS + " INTEGER " +
                    ")";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_CREATE_SPRINTS);
        db.execSQL(TABLE_CREATE_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPRINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }
}

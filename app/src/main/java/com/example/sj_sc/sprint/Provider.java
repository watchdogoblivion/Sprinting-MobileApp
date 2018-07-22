package com.example.sj_sc.sprint;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class Provider extends ContentProvider {

    private static final String AUTHORITY = "com.example.sj_sc.sprint";

    private static final String SPRINTS_PATH = "sprint";
    private static final String SETTINGS_PATH = "settings";

    public static final Uri SPRINTS_URI = Uri.parse("content://" + AUTHORITY + "/" + SPRINTS_PATH);
    public static final Uri SETTINGS_URI = Uri.parse("content://" + AUTHORITY + "/" + SETTINGS_PATH);

    // Constant to identify the requested operation
    private static final int SPRINTS = 1;
    private static final int SPRINTS_ID = 2;
    private static final int SETTINGS = 3;
    private static final int SETTINGS_ID = 4;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, SPRINTS_PATH, SPRINTS);
        uriMatcher.addURI(AUTHORITY, SPRINTS_PATH +  "/#", SPRINTS_ID);
        uriMatcher.addURI(AUTHORITY, SETTINGS_PATH, SETTINGS);
        uriMatcher.addURI(AUTHORITY, SETTINGS_PATH +  "/#", SETTINGS_ID);
        //if uri ends with numeric value it is giving us the specific row with that id
    }

    public static final String SPRINTS_ITEM_TYPE = "sprints";

    private static SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    public static void populate(){

        Sprint.SPRINT_MAP.clear();
        String query1 = "SELECT * FROM " + DBOpenHelper.TABLE_SPRINTS;
        Cursor sCursor = database.rawQuery(query1, null);

        if (sCursor.moveToFirst()) {
            while ( !sCursor.isAfterLast() ) {

                Sprint sprint = new Sprint("", 0);
                int sprint_ID = sCursor.getInt(sCursor.getColumnIndex(DBOpenHelper.SPRINT_ID));
                String time = sCursor.getString(sCursor.getColumnIndex(DBOpenHelper.SPRINT_TIME));
                int distance = sCursor.getInt(sCursor.getColumnIndex(DBOpenHelper.SPRINT_DISTANCE));
                int speed = sCursor.getInt(sCursor.getColumnIndex(DBOpenHelper.SPRINT_SPEED));
                String dateCreated = sCursor.getString(sCursor.getColumnIndex(DBOpenHelper.SPRINT_DATE_CREATED));
                sprint.setSprint_ID(sprint_ID);
                sprint.setTime(time);
                sprint.setDistance(distance);
                sprint.setSpeed(speed);
                sprint.setDate(dateCreated);

                Sprint.SPRINT_MAP.put(sprint.getSprint_ID(), sprint);
                sCursor.moveToNext();
            }
        }
        sCursor.close();

        Settings.SETTINGS_ARRAY_LIST.clear();
        String query2 = "SELECT * FROM " + DBOpenHelper.TABLE_SETTINGS;
        Cursor Cursor = database.rawQuery(query2, null);

        if (Cursor.moveToFirst()) {
            while ( !Cursor.isAfterLast() ) {

                Settings settings = new Settings(0,0,0,0,0);
                int settings_ID = Cursor.getInt(Cursor.getColumnIndex(DBOpenHelper.SETTINGS_ID));
                Float startingR = Cursor.getFloat(Cursor.getColumnIndex(DBOpenHelper.STARTING_REACTION));
                Float stoppingR = Cursor.getFloat(Cursor.getColumnIndex(DBOpenHelper.STOPPING_REACTION));
                int delayM = Cursor.getInt(Cursor.getColumnIndex(DBOpenHelper.DELAY_MINUTES));
                int delayS = Cursor.getInt(Cursor.getColumnIndex(DBOpenHelper.DELAY_SECONDS));
                int delayMS = Cursor.getInt(Cursor.getColumnIndex(DBOpenHelper.DELAY_MILLIS));
                settings.setSettingsID(settings_ID);
                settings.setStartingR(startingR);
                settings.setStoppingR(stoppingR);
                settings.setDelayM(delayM);
                settings.setDelayS(delayS);
                settings.setDelayMS(delayMS);
                Settings.SETTINGS_ARRAY_LIST.add( settings);

                MainScreenActivity.triggerStart = startingR;
                MainScreenActivity.triggerStop = stoppingR;
                MainScreenActivity.delayMinutes = delayM;
                MainScreenActivity.delaySeconds = delayS;
                MainScreenActivity.delayMilliSeconds = delayMS;
                Cursor.moveToNext();
            }
        }
        Cursor.close();

    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (uriMatcher.match(uri)) {
            case SPRINTS:
                return database.query(DBOpenHelper.TABLE_SPRINTS, DBOpenHelper.SPRINTS_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.SPRINT_ID + " ASC");

            case SPRINTS_ID:
                selection = DBOpenHelper.SPRINT_ID + "=" + uri.getLastPathSegment();
                return database.query(DBOpenHelper.TABLE_SPRINTS, DBOpenHelper.SPRINTS_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.SPRINT_ID + " ASC");

            case SETTINGS:
                return database.query(DBOpenHelper.TABLE_SETTINGS, DBOpenHelper.SETTINGS_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.SETTINGS_ID + " ASC");

            case SETTINGS_ID:
                selection = DBOpenHelper.SETTINGS_ID + "=" + uri.getLastPathSegment();
                return database.query(DBOpenHelper.TABLE_SETTINGS, DBOpenHelper.SETTINGS_COLUMNS,
                        selection, null, null, null,
                        DBOpenHelper.SETTINGS_ID + " ASC");

            default:
                throw new IllegalArgumentException();
        }

    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        long id;

        switch (uriMatcher.match(uri)) {
            case SPRINTS:
                id = database.insert(DBOpenHelper.TABLE_SPRINTS,
                        null, values);
                return Uri.parse(SPRINTS_PATH + "/" + id);

            case SETTINGS:
                id = database.insert(DBOpenHelper.TABLE_SETTINGS,
                        null, values);
                return Uri.parse(SETTINGS_PATH + "/" + id);

            default:
                throw new IllegalArgumentException();
        }

    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case SPRINTS:
                return database.delete(DBOpenHelper.TABLE_SPRINTS, selection, selectionArgs);
            case SETTINGS:
                return database.delete(DBOpenHelper.TABLE_SETTINGS, selection, selectionArgs);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case SPRINTS:
                return database.update(DBOpenHelper.TABLE_SPRINTS,
                        values, selection, selectionArgs);

            case SETTINGS:
                return database.update(DBOpenHelper.TABLE_SETTINGS,
                        values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException();
        }

    }
}


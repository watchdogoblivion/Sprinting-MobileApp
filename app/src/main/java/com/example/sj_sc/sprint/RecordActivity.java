package com.example.sj_sc.sprint;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Objects;

public class RecordActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private String whereClauseSprints;
    private CursorAdapter cursorAdapter;
    private String dateSprint;
    private ListView lV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Drawable drawable = getDrawable(R.drawable.bar_background);
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(drawable);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        displaySprint();

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_record_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                onBackPressed();


                break;
        }
        return true;
    }

    private void displaySprint() {
        Intent intent = getIntent();
        dateSprint = intent.getStringExtra(Provider.SPRINTS_ITEM_TYPE);
        whereClauseSprints = DBOpenHelper.SPRINT_DATE_ONLY + "= '" + dateSprint + "'";

        String [] from = {DBOpenHelper.SPRINT_TIME, DBOpenHelper.SPRINT_DISTANCE, DBOpenHelper.SPRINT_SPEED};
        int [] to = {R.id.lapTimeView2, R.id.editDistanceText2, R.id.lapSpeedView2};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.record_list_item,
                null, from, to, 0);

        lV = findViewById(R.id.listViewRecord);
        lV.setAdapter(cursorAdapter);
    }

    private void loaderRestart() {
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Provider.SPRINTS_URI, null, whereClauseSprints,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    public void delete(View view) {
        int position = lV.getPositionForView(view);
        final int sprintID = (int)lV.getItemIdAtPosition(position);
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            String whereClause = DBOpenHelper.SPRINT_ID + "=" + sprintID;
                            getContentResolver().delete(Provider.SPRINTS_URI, whereClause, null);
                            Sprint.SPRINT_MAP.remove(sprintID);
                            Toast.makeText(RecordActivity.this,
                                    getString(R.string.record_deleted),
                                    Toast.LENGTH_SHORT).show();
                            loaderRestart();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    public void update(View view) {
        RelativeLayout rL = (RelativeLayout)view.getParent();
        EditText editDistanceText2 = (EditText)rL.getChildAt(3);
        int position = lV.getPositionForView(view);
        int sprintID = (int)lV.getItemIdAtPosition(position);

        Sprint sprint = Sprint.SPRINT_MAP.get(sprintID);
        int distance;
        if(!editDistanceText2.getText().toString().isEmpty()){
            distance = Integer.parseInt(editDistanceText2.getText().toString());
        }else{
            distance = 0;
        }
        sprint.setDistance(distance);

        String whereClause = DBOpenHelper.SPRINT_ID + "=" + sprintID;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.SPRINT_DISTANCE, sprint.getDistance());
        values.put(DBOpenHelper.SPRINT_SPEED, sprint.getSpeed());

        getContentResolver().update(Provider.SPRINTS_URI, values, whereClause, null);

        loaderRestart();
    }
}

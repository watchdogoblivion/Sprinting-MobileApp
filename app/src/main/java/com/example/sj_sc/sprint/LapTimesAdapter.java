package com.example.sj_sc.sprint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

 class LapTimesAdapter extends ArrayAdapter {

    private final List<String> lapTimesList;

    LapTimesAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        lapTimesList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.sprint_item, parent, false);
        }

        String lapTime = lapTimesList.get(position);
        TextView lapTimeView = convertView.findViewById(R.id.lapTimeView);
        lapTimeView.setText(lapTime);

        return convertView;
    }


 }

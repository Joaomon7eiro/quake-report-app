package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeArrayAdapter extends ArrayAdapter {

    public EarthquakeArrayAdapter(@NonNull Context context,
                                  ArrayList<Earthquake> earthquakeArrayList) {
        super(context, 0, earthquakeArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listViewItem = convertView;

        if (listViewItem == null){
            listViewItem = LayoutInflater.from(getContext())
                    .inflate(R.layout.earthquake_list_item, parent, false);
        }

        Earthquake earthquake = (Earthquake) getItem(position);

        TextView magnitude = (TextView) listViewItem.findViewById(R.id.magnitude);
        magnitude.setText(String.valueOf(earthquake.getMagnitude()));

        TextView location = (TextView) listViewItem.findViewById(R.id.location);
        location.setText(earthquake.getLocation());

        Date dateObject = new Date(earthquake.getTimeInMilliseconds());

        TextView date = (TextView) listViewItem.findViewById(R.id.date);
        date.setText(formatDate(dateObject));

        TextView time = (TextView) listViewItem.findViewById(R.id.time);
        time.setText(formatTime(dateObject));

        return listViewItem;
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}

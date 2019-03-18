package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeArrayAdapter extends ArrayAdapter {

    private static final String LOCATION_SPLITTER = " of ";

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
        magnitude.setText(formatMagnitude(earthquake.getMagnitude()));

        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();
        int magnitudeColor = getMagnitudeColor(earthquake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);

        String place = earthquake.getLocation();
        String locationOffsetString;
        String primaryLocationString;

        if (place.contains(LOCATION_SPLITTER)) {
            String[] placeInParts = place.split(LOCATION_SPLITTER);
            locationOffsetString = placeInParts[0] + LOCATION_SPLITTER;
            primaryLocationString = placeInParts[1];
        } else {
            locationOffsetString = getContext().getString(R.string.near_the);
            primaryLocationString = place;
        }

        TextView locationOffset = (TextView) listViewItem.findViewById(R.id.location_offset);
        locationOffset.setText(locationOffsetString);

        TextView primaryLocation = (TextView) listViewItem.findViewById(R.id.primary_location);
        primaryLocation.setText(primaryLocationString);

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

    private String formatMagnitude(Double magnitude) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(magnitude);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeFloor = (int) Math.floor(magnitude);
        int magnitudeColor;
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColor = R.color.magnitude1;
                break;
            case 2:
                magnitudeColor = R.color.magnitude2;
                break;
            case 3:
                magnitudeColor = R.color.magnitude3;
                break;
            case 4:
                magnitudeColor = R.color.magnitude4;
                break;
            case 5:
                magnitudeColor = R.color.magnitude5;
                break;
            case 6:
                magnitudeColor = R.color.magnitude6;
                break;
            case 7:
                magnitudeColor = R.color.magnitude7;
                break;
            case 8:
                magnitudeColor = R.color.magnitude8;
                break;
            case 9:
                magnitudeColor = R.color.magnitude9;
                break;
            default:
                magnitudeColor = R.color.magnitude10plus;
        }
        return getContext().getResources().getColor(magnitudeColor);
    }
}

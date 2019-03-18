package com.example.android.quakereport;

public class Earthquake {

    private float mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;

    public Earthquake(float magnitude, String location, long timeInMilliseconds) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
    }

    public float getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }
}

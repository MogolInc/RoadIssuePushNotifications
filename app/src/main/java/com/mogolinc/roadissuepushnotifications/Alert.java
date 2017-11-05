package com.mogolinc.roadissuepushnotifications;

import android.location.Location;

import java.util.List;

/**
 * Created by rbaverstock on 11/4/2017.
 */

public class Alert {
    public String mMessage;
    public List<Location> mGeometry;

    public Alert(String message, List<Location> geometry) {
        mMessage = message;
        mGeometry = geometry;
    }

    public String getMessage() {
        return mMessage;
    }

    public List<Location> getGeometry() {
        return mGeometry;
    }
}

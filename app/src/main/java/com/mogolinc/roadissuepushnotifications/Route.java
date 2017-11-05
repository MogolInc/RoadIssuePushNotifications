package com.mogolinc.roadissuepushnotifications;

import android.location.Location;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission_group.CALENDAR;

/**
 * Created by rbaverstock on 11/4/2017.
 */

public class Route {
    protected int mId;
    protected String mName;
    protected int mNotificationHour;
    protected String mPath;
    public Route(int id, String name, int notificationHour, String path) {
        mId = id;
        mName = name;
        mNotificationHour = notificationHour;
        mPath = path;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    // Returns next notification time
    public Date getNotificationTime() {
        // Calculate next notification time.
        Calendar now = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, mNotificationHour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        if (cal.before(now)) {
            cal.add(Calendar.DATE, 1);
        }
        return cal.getTime();
    }

    public String getPath() {
        return mPath;
    }
}

package com.mogolinc.roadissuepushnotifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rbaverstock on 11/4/2017.
 */

public class AlertReceiver extends BroadcastReceiver {
    protected static String MogolApiKey = "YOUR-API-KEY";
    public static String ACTION = "NOTIFY";

    protected PendingIntent mPendingIntent;
    protected Context mContext;
    protected SharedPreferences mSharedPref;

    // Need default constructor for receiver
    public AlertReceiver() {
        super();
    }

    public AlertReceiver(Context context, SharedPreferences sharedPref) {
        super();
        mContext = context;
        mSharedPref = sharedPref;

        Intent intent = new Intent(mContext, AlertReceiver.class);
        intent.setAction(ACTION);

        // See if there is an existing alarm
        int routeId = sharedPref.getInt("AlarmRouteId", -1);
        if(routeId >= 0) {
            intent.setData(Uri.parse(String.format("custom://%d", routeId)));
            mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        }
    }
    public boolean isAlarmEnabled() {
        return mPendingIntent != null;
    }

    public void createAlarm() throws IllegalStateException {
        if(mPendingIntent != null) {
            throw new IllegalStateException("Alarm already exists, cancel first");
        }
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Route r = findNextAlarm();

        Log.d("com.mogolinc", String.format("Creating alarm for %s", r.getNotificationTime().toString()));

        Intent intent = new Intent(mContext, AlertReceiver.class);
        intent.setAction(ACTION);
        intent.setData(Uri.parse(String.format("custom://%d", r.getId())));
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, r.getNotificationTime().getTime(), mPendingIntent);

        // Store route id so we can lookup pendingintent on app restart
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt("AlarmRouteId", r.getId());
        editor.commit();
    }

    public void destroyAlarm() throws IllegalStateException {
        if(mPendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(mPendingIntent);
            mPendingIntent.cancel();
            mPendingIntent = null;
        } else {
            throw new IllegalStateException("No alarm exists, create one first");
        }
    }

    protected Route findNextAlarm() {
        // Pick the next time
        Calendar earliestTime = Calendar.getInstance();
        earliestTime.setTime(RouteListActivity.Routes.get(0).getNotificationTime());
        int earliestIdx = 0;

        for(int i = 1; i < RouteListActivity.Routes.size(); i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(RouteListActivity.Routes.get(i).getNotificationTime());
            if(earliestTime.after(cal)) {
                earliestTime = cal;
                earliestIdx = i;
            }
        }

        return RouteListActivity.Routes.get(earliestIdx);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Determine which route triggered this alarm
        int routeId = Integer.parseInt(intent.getDataString().replace("custom://", ""));

        Route alarmRoute = null;
        for(int i = 0; i < RouteListActivity.Routes.size() && alarmRoute == null; i++) {
            if(RouteListActivity.Routes.get(i).getId() == routeId)
                alarmRoute = RouteListActivity.Routes.get(i);
        }

        // Create next alarm before http request
        createAlarm();

        // Make request to API for route
        requestAndPushNotifications(alarmRoute);
    }


    protected void requestAndPushNotifications(final Route r) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    ApiClient.MogolApiKey = mContext.getString(R.string.mogol_api_key);
                    JSONObject response = ApiClient.fetchRoute(r);

                    String message = String.format("%s: ", r.getName());
                    Map<String, Boolean> uniqueIssuesMap = new HashMap<String, Boolean>();

                    JSONArray features = response.getJSONArray("features");
                    for(int i = 0; i < features.length(); i++) {
                        JSONObject f = features.getJSONObject(i);
                        if(f.getString("type").toLowerCase().compareTo("route") == 0)
                            continue;

                        String condition = f.getJSONObject("properties").getString("condition");

                        uniqueIssuesMap.put(condition, true);
                    }

                    Object[] uniqueIssues = uniqueIssuesMap.keySet().toArray();

                    for(int i = 0; i < uniqueIssues.length; i++) {
                        message += (String)uniqueIssues[i];
                        if(i + 1 < uniqueIssues.length)
                            message += ", ";
                    }

                    // Send push notification as message

                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                    builder.setContentTitle("Mogol Road Warning")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true)
                            .setContentText(message);

                    Intent intent = new Intent(mContext, AlertsActivity.class);
                    intent.setData(Uri.parse(String.format("custom://%s", r.getId())));
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                            1,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);

                    final NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(1, builder.build());
                } catch(Exception ex) {
                /* TODO: Handle */
                    Log.d("com.mogolinc", String.format("OnReceive exception: ", ex.getMessage()));
                }

                return null;
            }
        }.execute();
    }
}

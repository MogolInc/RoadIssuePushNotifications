package com.mogolinc.roadissuepushnotifications;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by rbaverstock on 11/5/2017.
 */

public class ApiClient {
    public static String MogolApiKey = "YOUR-MOGOL-KEY";

    // Make this static so we can re-use it from AlertsActivity
    protected static JSONObject fetchRoute(Route r) throws IOException, JSONException {
        Log.d("com.mogolinc", String.format("Fetching new route for %s", r.getName()));

        URL url = new URL(String.format("https://api.mogolinc.com/conditions/route?path=%s&f=coordinates", URLEncoder.encode(r.getPath(), "UTF-8")));

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestProperty("x-api-key", MogolApiKey);
        urlConnection.setRequestProperty("Content-Type", "application/json");

        int status = urlConnection.getResponseCode();
        InputStream in = null;
        if(status == 200) {
            in = urlConnection.getInputStream();
        } else {
            in = urlConnection.getErrorStream();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        if(status == 200) {
            return new JSONObject(sb.toString());
        } else {
            throw new IOException(String.format("API request failed (%d):\n %s", status, sb.toString()));
        }
    }
}

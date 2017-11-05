package com.mogolinc.roadissuepushnotifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class AlertsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getDataString().replace("custom://", ""));

        Route r = null;
        for(int i = 0; i < RouteListActivity.Routes.size() && r == null; i++) {
            if(RouteListActivity.Routes.get(i).getId() == id)
                r = RouteListActivity.Routes.get(i);
        }

        // Fetch results from Mogol API and load into ListView
        loadRoute(r);
    }

    protected AsyncTask<Object, Void, ArrayList<Warning>> loadRoute(Route r) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.show();

        return new AsyncTask<Object, Void, ArrayList<Warning>>() {
            protected Context mContext;
            @Override
            protected ArrayList<Warning> doInBackground(Object... params) {
                try {
                    mContext = (Context) params[0];
                    Route route = (Route) params[1];
                    ApiClient.MogolApiKey = mContext.getString(R.string.mogol_api_key);
                    JSONObject response = ApiClient.fetchRoute(route);

                    ArrayList<Warning> warningList = new ArrayList<Warning>();

                    // Create the list of warnings and provide to the list view for display.
                    JSONArray features = response.getJSONArray("features");
                    for (int i = 0; i < features.length(); i++) {
                        JSONObject f = features.getJSONObject(i);
                        if (f.getString("type").toLowerCase().compareTo("route") == 0)
                            continue;

                        String condition = f.getJSONObject("properties").getString("condition");
                        String subcondition = f.getJSONObject("properties").getString("subcondition");
                        String details = f.getJSONObject("properties").getString("details");

                        Log.d("com.mogolinc", String.format("Found warning: %s, %s, %s", condition, subcondition, details));
                        warningList.add(new Warning(condition, subcondition, details));
                    }

                    return warningList;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Warning> warnings) {
                WarningListAdapter adapter = new WarningListAdapter(mContext, R.layout.content_alerts_list_entry, warnings);

                ListView lvWarnings = (ListView) findViewById(R.id.lvWarnings);
                lvWarnings.setAdapter(adapter);
                mDialog.dismiss();
            }
        }.execute(this, r);
    }
}

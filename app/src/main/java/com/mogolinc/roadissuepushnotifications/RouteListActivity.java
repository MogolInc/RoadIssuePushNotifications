package com.mogolinc.roadissuepushnotifications;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RouteListActivity extends AppCompatActivity implements View.OnClickListener {

    /* Time to trigger each trip as hour in the day */
    protected static int mRoute0Time = 9;
    protected static int mRoute1Time = 17;

    protected static String mRoute0Polyline = "ijvwF~tubMROHQoGgByCy@{@a@e@Yg@c@sO}JgCeBuA{@y@g@iHwEoAy@a@[aC}AaAs@k@k@c@i@aFmH}@cAw@q@iAy@{FsDkCiB{FuDmJiGeAg@kA]y@MsCQcAOaAY_@QiAu@wCoBoHmEaB_AiBkA_RyLeCaByE_Do@g@cE{Dc@c@yHcF{E_DuCgB{D_CqAk@cBm@mFwAiBi@_G{BaBy@gCsAyEyC}E{DmBiBgBsBwGwIOQM]]e@QYGa@Ag@Do@D_@f@{AfCcIlBcGv@gCrPyh@Ls@?m@c@wCCk@Be@Ju@H[z@oC`A{BbAeB^u@Z}@RaAb@eDRaAjAsDbAwCRc@Xa@\\W^Q^Gz@IVMTYTu@dGqRpCwIfC}HiCeByByAaC{AuJsGnBiGd@yAb@uAdAp@LJRRnCjBjEtClCfBXNVYPi@Ha@T}@xAuEYQIC[GWKq@c@";
    protected static String mRoute1Polyline = "{mouGhhvkV@_UUiCIuAAYPu@HWR[TUd@KhET~APz@TnAf@`@LV@x@`@vFnC|DrBnChBvAhA|A~ArAzA`BxBrBbD`D|Ev@v@tA~@`@Rb@Txj@hYpIdEvCrAbHtCtEbCbCnAjG|CdDpAfCx@TFx@PbI~ArKvBrBb@vP~ClBRbDHvCI~ASlB]fH_CjP{FnBm@|HqCpPaGfKqDvBi@lDo@bDW~F?vH?rJ@nGBpPBzQCvICfUDpPKfQEhLD`DBhCDbHEtOFzNFhCCbCY`AW`Bu@l@_@zAqAzCsDfAyAlA}AHKNEd@q@@Ah@q@T[vIcLvBiC|A}AtEsDrAgApG_FX]|CcC|E}DbFaE~AaAlCsAfC{@zCk@t@KlBGlGIpMCzBB`R\\jFHrERn@Fh@N`Ev@tBj@pBl@t@RhDbAlB`@\\HPBD@r@Jn@JhNvBnBTxABlACt@IzAY`Ae@tAgA`BmAj@Ud@Mb@Cf@@nATn@Xf@^d@d@l@|@zC~FlFpKn@tAhAfDnBjEz@tAh@j@~AhAv@Z|@VxAPZB~BCrBSvBe@hDcAvCo@dAKbDKbBCdDA^EnGFzFF~CFvBLdALt@JrAX|Br@dBv@~AdAfBnAfCdCfAnAdBbClBtCxBnCbA~@bAn@z@^|AVbBJx@AzBOvJq@bCGtBC`J?rD?~E?tBAbCMzBS|Bc@~Bm@|Aa@zIcCt@Mz@E`@@n@Fp@PhAj@f@`@lAdB^r@Z~@Rp@RpALvA?p@AtBMjBoAhMYpDM~BGbEB~BFxBRxCb@vCd@~A\\z@p@dAbBjBhD`DpAzAz@jAdAfBbG~KnBfErCdHlC|Gt@zAx@dBdNfYhE`JnGbNtKdVpHhPvBnFb@pA`@|A`@vBX`DHpCQrKKlGFpE\\vEHz@r@pDNh@vAvD|AdCp@z@~@bAvAfAtEfC`I`E|B`Bl@f@hAnAjAbBrA~Bl@|An@fBb@~Af@nCt@jGf@lE^bCb@dBj@dB`AzB`A~Al@x@n@n@tBfBnM";

    public static List<Route> Routes = new ArrayList<Route>() {{
        add(new Route(0, "NY", mRoute0Time, mRoute0Polyline));
        add(new Route(1, "Portland", mRoute1Time, mRoute1Polyline));
    }};
    Button btnEnable;
    AlertReceiver mAlertReceiver;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        mAlertReceiver = new AlertReceiver(this, getPreferences(Context.MODE_PRIVATE));

        // Add routes to display
        LinearLayout llMaster = (LinearLayout) findViewById(R.id.llMaster);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss");
        for(Route r : Routes) {
            GridLayout gridLayout = new GridLayout(this);
            gridLayout.setColumnCount(2);
            gridLayout.setRowCount(1);

            // Route name
            TextView tvName = new TextView(this);
            tvName.setText(r.getName());

            GridLayout.Spec columnSpec = GridLayout.spec(0, GridLayout.BASELINE);
            GridLayout.Spec rowSpec = GridLayout.spec(0);
            gridLayout.addView(tvName, new GridLayout.LayoutParams(rowSpec, columnSpec));


            // Notification time
            TextView tvTime = new TextView(this);
            tvTime.setText(dateFormat.format(r.getNotificationTime()));
            tvTime.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            columnSpec = GridLayout.spec(1, GridLayout.RIGHT);
            rowSpec = GridLayout.spec(0);
            gridLayout.addView(tvTime, new GridLayout.LayoutParams(rowSpec, columnSpec));

            llMaster.addView(gridLayout);
        }

        // Get current notification state (enabled/disabled)
        boolean enabled = mAlertReceiver.isAlarmEnabled();

        // Add a button to enable/disable notifications
        btnEnable = new Button(this);
        btnEnable.setText(enabled ? "Disable Notifications" : "Enable Notifications");
        btnEnable.setOnClickListener(this);

        llMaster.addView(btnEnable);


        // Test without having to wait for the alrm to trigger
//        new AsyncTask<Context, Void, Void>() {
//            @Override
//            protected Void doInBackground(Context... params) {
//                Intent testIntent = new Intent();
//                testIntent.setAction(AlertReceiver.ACTION);
//                testIntent.setData(Uri.parse(String.format("custom://%d", Routes.get(0).getId())));
//                mAlertReceiver.onReceive(params[0], testIntent);
//
//                return null;
//            }
//        }.execute(this);
    }


    @Override
    public void onClick(View v) {
        if(v == btnEnable) {
           // Toggle notifications
            if(mAlertReceiver.isAlarmEnabled()) {
                mAlertReceiver.destroyAlarm();
                btnEnable.setText("Enable Notifications");
            } else {
                mAlertReceiver.createAlarm();
                btnEnable.setText("Disable Notifications");
            }
        }
    }

    protected static List<Location> locationArrayToList(double[][] coords) {
        List<Location> ll = new ArrayList<>();

        for(int i = 0; i < coords.length; i++) {
            Location l = new Location("app");
            l.setLatitude(coords[i][1]);
            l.setLongitude(coords[i][0]);
        }

        return ll;
    }
}

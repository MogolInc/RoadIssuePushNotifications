package com.mogolinc.roadissuepushnotifications;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rbaverstock on 11/5/2017.
 */

public class WarningListAdapter extends ArrayAdapter<Warning> {
    private Context mContext;
    private List<Warning> mWarnings;

    public WarningListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Warning> objects) {
        super(context, resource, objects);

        mContext = context;
        mWarnings = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.content_alerts_list_entry, parent, false);
        TextView tvCondition = (TextView) rowView.findViewById(R.id.tvWarningCondition);
        TextView tvSubcondition = (TextView) rowView.findViewById(R.id.tvWarningSubcondition);
        TextView tvDetail = (TextView) rowView.findViewById(R.id.tvWarningDetail);

        tvCondition.setText(mWarnings.get(position).getCondition());
        tvSubcondition.setText(mWarnings.get(position).getSubcondition());
        tvDetail.setText(mWarnings.get(position).getDetails());

        return rowView;
    }
}

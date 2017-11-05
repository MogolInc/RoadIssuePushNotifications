package com.mogolinc.roadissuepushnotifications;

/**
 * Created by rbaverstock on 11/5/2017.
 */

public class Warning {
    protected String mCondition;
    protected String mSubcondition;
    protected String mDetails;

    public Warning(String condition, String subcondition, String details) {
        mCondition = condition;
        mSubcondition = subcondition;
        mDetails = details;
    }

    public String getCondition() {
        return mCondition;
    }

    public String getSubcondition() {
        return mSubcondition;
    }

    public String getDetails() {
        return mDetails;
    }
}

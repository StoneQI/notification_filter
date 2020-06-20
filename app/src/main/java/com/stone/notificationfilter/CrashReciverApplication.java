package com.stone.notificationfilter;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;
public class CrashReciverApplication extends Application {

    @Override
    public void onCreate() {
        CrashReport.initCrashReport(getApplicationContext(), "e1a21830b6", false);
        super.onCreate();

    }


}

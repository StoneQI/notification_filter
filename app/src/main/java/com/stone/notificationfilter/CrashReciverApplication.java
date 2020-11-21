package com.stone.notificationfilter;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
//import com.tencent.mmkv.MMKV;

public class CrashReciverApplication extends Application {

    @Override
    public void onCreate() {
        CrashReport.initCrashReport(getApplicationContext(), "e1a21830b6", false);
        super.onCreate();
//        String rootDir = MMKV.initialize(this);
//        Log.e("Application","mmkv root: "+rootDir);

    }


}

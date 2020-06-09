package com.stone.notificationfilter.actioner;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.SpUtil;

import java.util.Timer;
import java.util.TimerTask;

public class RunIntentActioner {
    private static String TAG ="RunIntentActioner";
    private NotificationInfo notificationInfo;
    private Context context;
//    private PendingIntent intent = null;

    public  RunIntentActioner(NotificationInfo notificationInfo, Context context) {
        this.notificationInfo = notificationInfo;
        this.context = context;
    }
    public void  run(){
        Log.e(TAG,TAG);

        long time = Long.parseLong(SpUtil.getString(context,"appSettings","runintenactioner_time","100"));
        if (time <= 0 ) time=0;
        if(notificationInfo.intent !=null){
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        notificationInfo.intent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }

                }
            };
            Timer timer = new Timer();
            timer.schedule(task, time);
        }
    }
}

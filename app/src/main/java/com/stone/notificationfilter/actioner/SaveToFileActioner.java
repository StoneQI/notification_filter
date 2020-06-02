package com.stone.notificationfilter.actioner;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.stone.notificationfilter.entitys.notificationitem.NotificationItemDao;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemDataBase;
import com.stone.notificationfilter.entitys.notificationitem.NotificationItemEntity;
import com.stone.notificationfilter.util.NotificationInfo;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.TimeUtil;
import com.xujiaji.dmlib2.LogUtil;

public class SaveToFileActioner {
    private static String TAG ="FloatingTile";
    private NotificationInfo notificationInfo;
    private Context context;
    private static NotificationItemDao notificationItemDao;
//    private PendingIntent intent = null;

    public  SaveToFileActioner(NotificationInfo notificationInfo, Context context) {
        this.notificationInfo = notificationInfo;
        this.context = context;

    }
    public void  run(){
        if (!SpUtil.getSp(context,"appSettings").getBoolean("save_log_to_file",true)){
          return;
        };
        if(notificationItemDao ==null)
        {
            NotificationItemDataBase db =NotificationItemDataBase.getInstance(context);
            notificationItemDao = db.NotificationItemDao();
        }

        final NotificationItemEntity notificationItemEntity = new NotificationItemEntity();
        notificationItemEntity.packageName =notificationInfo.packageName;
        notificationItemEntity.title = notificationInfo.title;
        notificationItemEntity.content = notificationInfo.content;
        notificationItemEntity.postTime = notificationInfo.postTime;
        notificationItemEntity.tag = notificationInfo.Tag;
        notificationItemEntity.dayOfYear = TimeUtil.getDay(notificationInfo.postTime);
        notificationItemEntity.appName = PackageUtil.getAppNameFromPackname(context,notificationInfo.packageName);
        Intent intent = getIntentByPendingIntent(notificationInfo.intent);
//         =
        //将Intent转换成String，可以存到数据库
//        notificationItemEntity.intentUrl = intent.toUri(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationItemDao.insertOne(notificationItemEntity);
            }
        }).start();


    }



    /**
     * 通过反射从PendingIntent中拿到Intent
     *
     * @param pendingIntent
     * @return intent
     */
    public static Intent getIntentByPendingIntent(PendingIntent pendingIntent) {
        Intent intent = null;
        try {
            intent = (Intent) PendingIntent.class.getDeclaredMethod("getIntent", new Class[0]).invoke(pendingIntent, new Object[0]);
        } catch (Exception paramPendingIntent) {
//            LogUtil.d("PendingIntent getIntent failure!!!");
        }
        return intent;
    }

}

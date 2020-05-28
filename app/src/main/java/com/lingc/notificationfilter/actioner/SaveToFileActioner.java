package com.lingc.notificationfilter.actioner;

import android.content.Context;

import com.lingc.notificationfilter.entitys.notificationitem.NotificationItemDao;
import com.lingc.notificationfilter.entitys.notificationitem.NotificationItemDataBase;
import com.lingc.notificationfilter.entitys.notificationitem.NotificationItemEntity;
import com.lingc.notificationfilter.util.NotificationInfo;
import com.lingc.notificationfilter.util.PackageUtil;
import com.lingc.notificationfilter.util.SpUtil;
import com.lingc.notificationfilter.util.TimeUtil;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationItemDao.insertOne(notificationItemEntity);
            }
        }).start();


    }
}

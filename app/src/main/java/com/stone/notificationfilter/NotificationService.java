package com.stone.notificationfilter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.stone.notificationfilter.actioner.CopyActioner;
import com.stone.notificationfilter.actioner.RunIntentActioner;
import com.stone.notificationfilter.actioner.SaveToFileActioner;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterDataBase;
import com.stone.notificationfilter.util.NotificationInfo;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterEntity;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.actioner.FloatingTile;
import com.stone.notificationfilter.actioner.TileObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Create by LingC on 2019/8/4 21:46
 */
public class NotificationService extends NotificationListenerService {
    private final static  String TAG ="NotificationService";
    private static final String NOTIFICATION_CHANNEL_ID = "FloatWindowService";
    public static final int MANAGER_NOTIFICATION_ID = 0x1001;
    public static final int HANDLER_DETECT_PERMISSION = 0x2001;
    private String content;
    private Icon iconIcon=null;
    private Bitmap iconBitmap = null;
    private ArrayList<NotificationFilterEntity> systemNotificationMatchers= new ArrayList<NotificationFilterEntity>();
    private List<NotificationFilterEntity> customNotificationMatchers= null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setSystemNotificationMatchers();
            customNotificationMatchers.addAll(systemNotificationMatchers);
            Log.e(TAG,"------------> msg.what = " + msg.what);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        if (SpUtil.getSp(getApplicationContext(),"appSettings").getBoolean("notification_show", false)){
            addForegroundNotification();
        }
        Log.e(TAG,"service create");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NotificationFilterDataBase db =NotificationFilterDataBase.getInstance(getApplicationContext());
                customNotificationMatchers = db.NotificationFilterDao().loadAllDESC();
                db.close();
                mHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    private void setSystemNotificationMatchers(){
        NotificationFilterEntity notificationMatcher = new NotificationFilterEntity();
        notificationMatcher.orderID = 0;
        notificationMatcher.name = "默认悬浮通知";
        notificationMatcher.actioner = 0;
        NotificationFilterEntity notificationMatcher2 = new NotificationFilterEntity();

        systemNotificationMatchers.add(notificationMatcher);
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        try {
            if (sbn.getPackageName().equals("android")) {
                return;
            }
            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            if (!powerManager.isScreenOn()) {
                return;
            }

            NotificationInfo notificationInfo = getNotificationInfo(sbn);
            if (notificationInfo.getContent() == null && notificationInfo.getTitle()==null) {
                return;
            }

            for(NotificationFilterEntity notificationMatcher:customNotificationMatchers){
                Log.e(TAG, notificationMatcher.name);
                if(  notificationMatcher.packageNames !=null && !notificationMatcher.packageNames.isEmpty() && !notificationMatcher.packageNames.contains(notificationInfo.getPackageName())){
                    continue;
                }

                if(notificationMatcher.titlePattern != null && !notificationMatcher.titlePattern.isEmpty() ){
                    Pattern p =  Pattern.compile(notificationMatcher.titlePattern);
                    if (!p.matcher(notificationInfo.getTitle()).find()) continue;
                }

                if(notificationMatcher.contextPatter != null  && !notificationMatcher.contextPatter.isEmpty() ){
                    Pattern p =  Pattern.compile(notificationMatcher.contextPatter);
                    if (!p.matcher(notificationInfo.getContent()).find()) continue;
                }

                if( notificationMatcher.titleFiliter != null  &&!notificationMatcher.titleFiliter.isEmpty() ){
                    String new_title = "";
                    if (notificationMatcher.titleFiliterReplace != null  && !notificationMatcher.titleFiliterReplace.isEmpty()){
                        try {
                            new_title = notificationInfo.getTitle();
                            new_title = new_title.replaceAll(notificationMatcher.titleFiliter,notificationMatcher.titleFiliterReplace);
                        }catch (PatternSyntaxException e){
                            new_title = "";
                            e.printStackTrace();
                        }
                    }else {
                        Pattern p =  Pattern.compile(notificationMatcher.titleFiliter);
                        Matcher m = p.matcher(notificationInfo.getTitle());
                        while(m.find()){
                            Log.e(TAG,m.group());
                            new_title = new_title +m.group();
                        }
                    }
                    notificationInfo.setTitle(new_title);

                }
                if(notificationMatcher.contextFiliter != null  && !notificationMatcher.contextFiliter.isEmpty() ){
                    String new_Content = "";
                    if (notificationMatcher.contextFiliterReplace != null && !notificationMatcher.contextFiliterReplace.isEmpty()){
                        try {
                            new_Content = notificationInfo.getTitle();
                            new_Content.replaceAll(notificationMatcher.contextFiliter,notificationMatcher.contextFiliterReplace);
                        }catch (PatternSyntaxException e){
                            new_Content ="";
                            e.printStackTrace();
                        }
                    }else {
                        Pattern p = Pattern.compile(notificationMatcher.contextFiliter);
                        Matcher m = p.matcher(notificationInfo.getContent());
                        while (m.find()) {
                            Log.e(TAG, m.group());
                            new_Content = new_Content + m.group();
                        }
                    }
                    notificationInfo.setContent(new_Content);
                }

                switch (notificationMatcher.actioner){
                    case 0:cancelNotification(notificationInfo.key);floatingTileAction(notificationInfo); break;
                    case 1:super.onNotificationPosted(sbn);break;
                    case 2:cancelNotification(notificationInfo.key); break;
                    case 3:cancelNotification(notificationInfo.key);new CopyActioner(notificationInfo,NotificationService.this).run();break;
                    case 4:cancelNotification(notificationInfo.key);new RunIntentActioner(notificationInfo,NotificationService.this).run();break;
                    case 5:cancelNotification(notificationInfo.key);new SaveToFileActioner(notificationInfo,NotificationService.this).run();break;
//                default:
//                default: cancelAllNotifications();floatingTileAction(notificationInfo); break;
                }
                if(notificationMatcher.breakDown){
                    break;
                }
            }
        }catch (Exception e){
            super.onNotificationPosted(sbn);
            e.printStackTrace();
        }

//        cancelAllNotifications();


//        final Object finalIcon = icon;
//        floatingTileAction(notificationInfo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"service stop");
    }

    private NotificationInfo getNotificationInfo(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;
        NotificationInfo notificationInfo = new NotificationInfo(sbn.getId(),sbn.getKey(),sbn.getPostTime());
        notificationInfo.setClearable(sbn.isClearable());
        notificationInfo.setOnGoing(sbn.isOngoing());
        notificationInfo.setTag(sbn.getTag());
        notificationInfo.setPackageName(sbn.getPackageName());
        notificationInfo.setLargeIcon(sbn.getNotification().getLargeIcon());
        notificationInfo.setSmallIcon(sbn.getNotification().getSmallIcon());
        notificationInfo.setTitle(extras.getString(android.app.Notification.EXTRA_TITLE));
        notificationInfo.setContent(extras.getString(android.app.Notification.EXTRA_TEXT));
        notificationInfo.setIntent(sbn.getNotification().contentIntent);
        return notificationInfo;
    }

    private void floatingTileAction(final NotificationInfo notificationInfo) {

                FloatingTile floatingTile = new FloatingTile(notificationInfo,NotificationService.this,false);
                floatingTile.setLastTile(TileObject.lastFloatingTile);
                floatingTile.showWindow();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    private void addForegroundNotification() {
        createNotificationChannel();

        String contentTitle = "通知处理器";
        String contentText = "通知处理器运行中...";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent msgIntent = getStartAppIntent(getApplicationContext());
        PendingIntent mainPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = mBuilder.setContentIntent(mainPendingIntent)
                .setAutoCancel(false).build();

        startForeground(MANAGER_NOTIFICATION_ID, notification);
    }
    private Intent getStartAppIntent(Context context) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(AppUtils.getAppPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }

        return intent;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "通知处理器";
            String description = "防止通知处理器被后台关闭";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


}

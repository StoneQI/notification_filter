package com.stone.notificationfilter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;

import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.blankj.utilcode.util.AppUtils;
import com.stone.notificationfilter.actioner.CopyActioner;
import com.stone.notificationfilter.actioner.FloatCustomViewActioner;
import com.stone.notificationfilter.actioner.NotificationSoundActioner;
import com.stone.notificationfilter.actioner.RunIntentActioner;
import com.stone.notificationfilter.actioner.SaveToFileActioner;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterDataBase;
import com.stone.notificationfilter.notificationhandler.NotificationHandlerJudge;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItemFileStorage;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterEntity;
import com.stone.notificationfilter.notificationhandler.databases.SystemBaseHandler;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.actioner.FloatingTileActioner;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Create by LingC on 2019/8/4 21:46
 */
public class NotificationService extends NotificationListenerService {
    private final static  String TAG ="NotificationService";

    public static boolean isStartListener = true;
    public static  boolean isCancalSystemNotification = false;

    private NotificationHandlerItemFileStorage notificationHandlerItemFileStorage;

    private ArrayList<NotificationFilterEntity> systemNotificationMatchers= new ArrayList<NotificationFilterEntity>();

    private ArrayList<NotificationHandlerItem> systemNotificationHandler;
    private ArrayList<NotificationHandlerItem> notificationHandlerItems;

    private List<NotificationFilterEntity> customNotificationMatchers= null;
    public static Set<String> selectAppList = null;
//    True为白名单， false为黑名单
    public static  boolean appListMode = false;
    private boolean isSceenLock =false;
    private NotificationInfo notificationInfo=null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            setSystemNotificationMatchers();
            notificationHandlerItems.addAll(SystemBaseHandler.getSystemHandlerRule());
            Log.e(TAG,"------------> msg.what = " + msg.what);
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        isStartListener = true;
        if (SpUtil.getBoolean(getApplicationContext(),"appSettings","notification_show", true)){
            addForegroundNotification();
        }

        String packageNamestring = SpUtil.getString(getApplicationContext(),"appSettings","select_applists", "");
        selectAppList = SpUtil.string2Set(packageNamestring);

        appListMode = SpUtil.getBoolean(getApplicationContext(),"appSettings","applist_mode", false);
        Log.e(TAG,"service create");
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
        registerReceiver(mBroadcastReceiver, new IntentFilter(NOTIFICATION_FILTER_START_INTENT));

        new Thread(new Runnable() {
            @Override
            public void run() {
//                NotificationFilterDataBase db =NotificationFilterDataBase.getInstance(getApplicationContext());
//                customNotificationMatchers = db.NotificationFilterDao().loadAllDESC();
                try {
                    notificationHandlerItemFileStorage = new NotificationHandlerItemFileStorage(getApplicationContext(),true);
                    notificationHandlerItems = notificationHandlerItemFileStorage.getAllAsArrayList();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mHandler.sendEmptyMessage(0);
            }
        }).start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    private void setSystemNotificationMatchers(){

        NotificationFilterEntity notificationMatcher8 = new NotificationFilterEntity();
        notificationMatcher8.orderID = 8;
        notificationMatcher8.name = "去掉正在其他应用上层";
        notificationMatcher8.contextPatter="正在其他的应用上层";
        notificationMatcher8.actioner = 2;
        notificationMatcher8.breakDown =false;

        NotificationFilterEntity notificationMatcher7 = new NotificationFilterEntity();
        notificationMatcher7.orderID = 5;
        notificationMatcher7.name = "播放网易云";
//        notificationMatcher6.contextPatter="^(?!.*?个联系人给你发过来).*$";
        notificationMatcher7.packageNames="com.netease.cloudmusic;com.miui.player;com.tencent.qqmusic";
        notificationMatcher7.actioner = 7;
        notificationMatcher7.breakDown =false;


        NotificationFilterEntity notificationMatcher6 = new NotificationFilterEntity();
        notificationMatcher6.orderID = 4;
        notificationMatcher6.name = "播放微信提示音";
//        notificationMatcher6.contextPatter="^(?!.*?个联系人给你发过来).*$";
        notificationMatcher6.packageNames="com.tencent.tim;com.tencent.mm;";
        notificationMatcher6.actioner = 6;
        notificationMatcher6.breakDown =false;

        NotificationFilterEntity notificationMatcher5 = new NotificationFilterEntity();
        notificationMatcher5.orderID = 3;
        notificationMatcher5.name = "日记记录";
        notificationMatcher5.contextPatter="^(?!.*?个联系人给你发过来).*$";
        notificationMatcher5.packageNames="com.tencent.tim;com.tencent.mm;com.tencent.mobileqq;";
        notificationMatcher5.actioner = 5;
        notificationMatcher5.breakDown =false;

        NotificationFilterEntity notificationMatcher2 = new NotificationFilterEntity();
        notificationMatcher2.orderID = 1;
        notificationMatcher2.name = "排除正在运行";
        notificationMatcher2.titlePattern="正在运行";
        notificationMatcher2.actioner = 2;
        notificationMatcher2.breakDown =true;

        NotificationFilterEntity notificationMatcher3 = new NotificationFilterEntity();
        notificationMatcher3.orderID = 1;
        notificationMatcher3.name = "排除下载";
        notificationMatcher3.titlePattern="下载";
        notificationMatcher3.actioner = 2;
        notificationMatcher3.breakDown =true;



        NotificationFilterEntity notificationMatcher = new NotificationFilterEntity();
        notificationMatcher.orderID = 0;
        notificationMatcher.name = "默认悬浮通知";
        notificationMatcher.actioner = 0;
        notificationMatcher.breakDown =true;

        systemNotificationMatchers.add(notificationMatcher7);
        systemNotificationMatchers.add(notificationMatcher8);
        systemNotificationMatchers.add(notificationMatcher6);
        systemNotificationMatchers.add(notificationMatcher5);
        systemNotificationMatchers.add(notificationMatcher3);
        systemNotificationMatchers.add(notificationMatcher2);
        systemNotificationMatchers.add(notificationMatcher);



    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        Log.i(TAG,"onNotificationPosted:"+isStartListener);
        if (!isStartListener){
            return;
        }
        if(selectAppList.size()!=0){
            if(appListMode){
                if(!selectAppList.contains(sbn.getPackageName())){
                    cancelNotification(sbn.getKey());
                    return;
                }
            }else {
                if(selectAppList.contains(sbn.getPackageName())){
                    cancelNotification(sbn.getKey());
                    return;
                }
            }
        }
        if (isSceenLock) {
            return;
        }


//            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            notificationInfo = getNotificationInfo(sbn);
            isCancalSystemNotification =false;

//            Log.e(TAG, "notifi_id"+String.valueOf(notificationInfo.ID));
//            Log.e(TAG, "notifi_key"+notificationInfo.key);
//            Log.e(TAG, "notifi_id"+String.valueOf(notificationInfo.getContent()));
            for(NotificationHandlerItem notificationHandlerItem:notificationHandlerItems){

                try {
                    NotificationHandlerJudge notificationHandlerJudge = new NotificationHandlerJudge(notificationInfo, notificationHandlerItem);
                    if (!notificationHandlerJudge.isMatch()){
                        Log.i(TAG,notificationInfo.title+" in "+notificationHandlerItem.name+" patter is fail");
                        continue;
                    }
                    Log.i(TAG,notificationInfo.title+" in "+notificationHandlerItem.name+" patter is match");
                    notificationInfo.title = notificationHandlerJudge.titleReplace(notificationInfo.title);
                    notificationInfo.content =notificationHandlerJudge.contentReplace(notificationInfo.content);
                    switch (notificationHandlerItem.actioner){
                        case 0:cancelNotification(notificationInfo.key);floatingTileAction(notificationInfo); break;
                        case 1:break;
                        case 2:isCancalSystemNotification =true; break;
                        case 3:new CopyActioner(notificationInfo,NotificationService.this).run();break;
                        case 4:new RunIntentActioner(notificationInfo,NotificationService.this).run();break;
                        case 5:new SaveToFileActioner(notificationInfo,NotificationService.this).run();break;
                        case 6:new NotificationSoundActioner(notificationInfo,NotificationService.this).run();isCancalSystemNotification =true;break;
                        case 7:new FloatCustomViewActioner(notificationInfo,NotificationService.this).run();isCancalSystemNotification =true;break;
                    }
                    if(notificationHandlerItem.breakDown){
                        break;
                    }
                }catch (Exception e){
                    isCancalSystemNotification =false;
                    e.printStackTrace();
                }
            }

        if (isCancalSystemNotification){
            if (notificationInfo!=null){
                cancelNotification(notificationInfo.key);
                Log.i(TAG,notificationInfo.title+" is cancel notification");
            }

        }else {
            super.onNotificationPosted(sbn);
        }

    }

    @Override
    public void onListenerConnected() {
        isStartListener =true;
        Log.i(TAG,"notification service connected start");
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        isStartListener = false;
        Log.i(TAG,"notification service connected stop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(new ComponentName(this, NotificationService.class));
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        stopForeground(true);
        super.onDestroy();
        Log.i(TAG,"notification service stop");
    }

    private NotificationInfo getNotificationInfo(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
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
//        notification.getBubbleMetadata();
//        extras.getString(android.app.Notification.EX);
//        extras.getString(Notification.DEFAULT_SOUND);
        notificationInfo.setIntent(sbn.getNotification().contentIntent);
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//        String audioContentsURI = extras.getString(Notification.EXTRA_AUDIO_CONTENTS_URI);
//        String backgoundImageURI = extras.getString(Notification.EXTRA_BACKGROUND_IMAGE_URI);
//        String audioContentsUri = extras.getString(Notification.EXTRA_AUDIO_CONTENTS_URI);
//        String bigText = extras.getString(Notification.EXTRA_BIG_TEXT);
//        String channelGroupID = extras.getString(Notification.EXTRA_CHANNEL_GROUP_ID);
//        String channelID = extras.getString(Notification.EXTRA_CHANNEL_ID);
////        String EXTRA_CONTAINS_CUSTOM_VIEW = extras.getString(Notification.EXTRA_CONTAINS_CUSTOM_VIEW);
//
//        String EXTRA_TEMPLATE = extras.getString(Notification.EXTRA_TEMPLATE);
//        Log.e(TAG,"TEMPLATE"+EXTRA_TEMPLATE);
//        String textLines = extras.getString(Notification.EXTRA_TEXT_LINES);
//        MediaSession.Token EXTRA_MEDIA_SESSION = extras.getParcelable(Notification.EXTRA_MEDIA_SESSION);
////        String EXTRA_COMPACT_ACTIONS = extras.getString(Notification.EXTRA_COMPACT_ACTIONS);
//        String EXTRA_SUMMARY_TEXT = extras.getString(Notification.EXTRA_SUMMARY_TEXT);
//        String EXTRA_TITLE_BIG = extras.getString(Notification.EXTRA_TITLE_BIG);
//
//        RemoteViews remoteView = sbn.getNotification().contentView;

//        if (notification == null) return;

        Configuration mConfiguration = getApplicationContext().getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            notificationInfo.sceenStatus =2;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            notificationInfo.sceenStatus =1;
        }

        RemoteViews remoteViews = getBigContentView(getApplicationContext(), notification);
        if(remoteViews == null)
            remoteViews = getContentView(getApplicationContext(), notification);

        if (remoteViews != null)
        {
            notificationInfo.remoteViews = remoteViews;
        }

        notificationInfo.setInteractive(powerManager.isInteractive());
        return notificationInfo;
    }

//    #TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static RemoteViews getBigContentView(Context context, Notification notification)
    {
        if(notification.bigContentView != null)
            return notification.bigContentView;
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Notification.Builder.recoverBuilder(context, notification).createBigContentView();
        else
            return null;
    }
    public static RemoteViews getContentView(Context context, Notification notification)
    {
        if(notification.contentView != null)
            return notification.contentView;
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Notification.Builder.recoverBuilder(context, notification).createContentView();
        else
            return null;
    }

    private void floatingTileAction(final NotificationInfo notificationInfo) {

                FloatingTileActioner floatingTile = new FloatingTileActioner(notificationInfo,NotificationService.this,false);
                floatingTile.run();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
    private RemoteViews mRemoteViews;
    private NotificationCompat.Builder mBuilder;
    private static final String NOTIFICATION_CHANNEL_ID = "FloatWindowService";
    private static final String NOTIFICATION_FILTER_START_INTENT = "com.stone.notificationfilter.FloatServiceStart";
    public static final int MANAGER_NOTIFICATION_ID = 0x1001;
    private void addForegroundNotification() {
        createNotificationChannel();

        mRemoteViews = new RemoteViews(getPackageName(),R.layout.notification_custom_layout);
        notificationCustomRemoteVIew();

        PendingIntent homeIntent = PendingIntent.getBroadcast(this,1,new Intent(NOTIFICATION_FILTER_START_INTENT),PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_filter_start,homeIntent);

        String contentText = "通知处理器开启运行中...";

        Intent msgIntent = getStartAppIntent(getApplicationContext());
        PendingIntent mainPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .setCustomContentView(mRemoteViews)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(contentText)
                .setContentIntent(mainPendingIntent)
                .setAutoCancel(false);

        startForeground(MANAGER_NOTIFICATION_ID, mBuilder.build());
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

    private void updateNotification(){
        Log.i(TAG,"updateNotification:"+isStartListener);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationCustomRemoteVIew();
        mNotificationManager.notify(MANAGER_NOTIFICATION_ID, mBuilder.build());

    }

    private void notificationCustomRemoteVIew() {
        if(isStartListener){
            mRemoteViews.setTextViewText(R.id.notification_filter_title, getResources().getString(R.string.service_start));
            mRemoteViews.setImageViewResource(R.id.notification_filter_start, android.R.drawable.ic_media_play);
        }else{
            mRemoteViews.setTextViewText(R.id.notification_filter_title, getResources().getString(R.string.service_stop));
            mRemoteViews.setImageViewResource(R.id.notification_filter_start, android.R.drawable.ic_media_pause);
        }
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case Intent.ACTION_SCREEN_OFF:
                        isSceenLock =true;
                        Log.d(TAG, "屏幕关闭，变黑");
                        break;
                    case Intent.ACTION_SCREEN_ON:
                        Log.d(TAG, "屏幕开启，变亮");
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        isSceenLock =false;
                        Log.d(TAG, "解锁成功");
                        break;
                    case NOTIFICATION_FILTER_START_INTENT:
                        isStartListener=!isStartListener;
                        updateNotification();
                        Log.i(TAG,"updateNotification:"+isStartListener);
                    default:
                        break;
                }
            }
        }
    };

}

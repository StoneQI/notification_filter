package com.stone.notificationfilter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
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
import com.stone.notificationfilter.util.NotificationInfo;
import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterEntity;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.actioner.FloatingTileActioner;

import java.lang.reflect.Field;
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
    private static final String NOTIFICATION_CHANNEL_ID = "FloatWindowService";
    public static final int MANAGER_NOTIFICATION_ID = 0x1001;
    public static final int HANDLER_DETECT_PERMISSION = 0x2001;
    private String content;
    private Icon iconIcon=null;
    private Bitmap iconBitmap = null;
    private ArrayList<NotificationFilterEntity> systemNotificationMatchers= new ArrayList<NotificationFilterEntity>();
    private List<NotificationFilterEntity> customNotificationMatchers= null;
    public static Set<String> selectAppList = null;
    public static  boolean appListMode = false;
    private boolean isSceenLock =false;

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

        String packageNamestring = SpUtil.getSp(getApplicationContext(),"appSettings").getString("select_applists", "");
        selectAppList = SpUtil.string2Set(packageNamestring);

        appListMode = SpUtil.getSp(getApplicationContext(),"appSettings").getBoolean("applist_mode", false);
        Log.e(TAG,"service create");
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (SpUtil.getSp(getApplicationContext(),"appSettings").getBoolean("notification_show", false)){
            addForegroundNotification();
        }
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    private void setSystemNotificationMatchers(){
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
        systemNotificationMatchers.add(notificationMatcher6);
        systemNotificationMatchers.add(notificationMatcher5);
        systemNotificationMatchers.add(notificationMatcher3);
        systemNotificationMatchers.add(notificationMatcher2);
        systemNotificationMatchers.add(notificationMatcher);



    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        try {

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
            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);

            if (isSceenLock) {
                return;
            }

            NotificationInfo notificationInfo = getNotificationInfo(sbn);
//            if (notificationInfo.getContent() == null && notificationInfo.getTitle()==null) {
//                return;
//            }

//            if (sbn.isClearable() ==false || sbn.getPackageName().equals("android")) {
////                super.onNotificationPosted(sbn);
//                return;
//            }



//            Log.e(TAG, "notifi_id"+String.valueOf(notificationInfo.ID));
            Log.e(TAG, "notifi_key"+notificationInfo.key);
            Log.e(TAG, "notifi_id"+String.valueOf(notificationInfo.getContent()));
            for(NotificationFilterEntity notificationMatcher:customNotificationMatchers){
                Log.e(TAG, notificationMatcher.name);
                if(  notificationMatcher.packageNames !=null && !notificationMatcher.packageNames.isEmpty()){
                    if (!notificationMatcher.packageNames.contains(notificationInfo.getPackageName())){
                        continue;
                    }
                }
                if (!TextUtils.isEmpty(notificationInfo.getTitle())){
                    if(!TextUtils.isEmpty(notificationMatcher.titlePattern) ){
                        Pattern p =  Pattern.compile(notificationMatcher.titlePattern);
                        if (!p.matcher(notificationInfo.getTitle()).find()) continue;
                    }
                    if( !TextUtils.isEmpty(notificationMatcher.titleFiliter)){
                        String new_title = "";
                        if (!TextUtils.isEmpty(notificationMatcher.titleFiliterReplace)){
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
                }

                if (!TextUtils.isEmpty(notificationInfo.getContent())) {
                    if (!TextUtils.isEmpty(notificationMatcher.contextPatter)) {
                        Pattern p = Pattern.compile(notificationMatcher.contextPatter);
                        if (!p.matcher(notificationInfo.getContent()).find()) continue;
                    }
                    if(!TextUtils.isEmpty(notificationMatcher.contextFiliter)){
                        String new_Content = "";
                        if (!TextUtils.isEmpty(notificationMatcher.contextFiliterReplace )){
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
                }

                switch (notificationMatcher.actioner){
                    case 0:cancelNotification(notificationInfo.key);floatingTileAction(notificationInfo); break;
                    case 1:break;
                    case 2:cancelNotification(notificationInfo.key); break;
                    case 3:cancelNotification(notificationInfo.key);new CopyActioner(notificationInfo,NotificationService.this).run();break;
                    case 4:cancelNotification(notificationInfo.key);new RunIntentActioner(notificationInfo,NotificationService.this).run();break;
                    case 5:cancelNotification(notificationInfo.key);new SaveToFileActioner(notificationInfo,NotificationService.this).run();break;
                    case 6:new NotificationSoundActioner(notificationInfo,NotificationService.this).run();break;
                    case 7:cancelNotification(notificationInfo.key);new FloatCustomViewActioner(notificationInfo,NotificationService.this).run();break;

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

    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
//        requestRebind();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        stopForeground(true);
        super.onDestroy();
        Log.e(TAG,"service stop");
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
        notification.getBubbleMetadata();
//        extras.getString(android.app.Notification.EX);
//        extras.getString(Notification.DEFAULT_SOUND);
        notificationInfo.setIntent(sbn.getNotification().contentIntent);
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        String audioContentsURI = extras.getString(Notification.EXTRA_AUDIO_CONTENTS_URI);
        String backgoundImageURI = extras.getString(Notification.EXTRA_BACKGROUND_IMAGE_URI);
        String audioContentsUri = extras.getString(Notification.EXTRA_AUDIO_CONTENTS_URI);
        String bigText = extras.getString(Notification.EXTRA_BIG_TEXT);
        String channelGroupID = extras.getString(Notification.EXTRA_CHANNEL_GROUP_ID);
        String channelID = extras.getString(Notification.EXTRA_CHANNEL_ID);
//        String EXTRA_CONTAINS_CUSTOM_VIEW = extras.getString(Notification.EXTRA_CONTAINS_CUSTOM_VIEW);

        String EXTRA_TEMPLATE = extras.getString(Notification.EXTRA_TEMPLATE);
        Log.e(TAG,"TEMPLATE"+EXTRA_TEMPLATE);
        String textLines = extras.getString(Notification.EXTRA_TEXT_LINES);
        MediaSession.Token EXTRA_MEDIA_SESSION = extras.getParcelable(Notification.EXTRA_MEDIA_SESSION);
//        String EXTRA_COMPACT_ACTIONS = extras.getString(Notification.EXTRA_COMPACT_ACTIONS);
        String EXTRA_SUMMARY_TEXT = extras.getString(Notification.EXTRA_SUMMARY_TEXT);
        String EXTRA_TITLE_BIG = extras.getString(Notification.EXTRA_TITLE_BIG);

        RemoteViews remoteView = sbn.getNotification().contentView;

//        if (notification == null) return;
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
                    default:
                        break;
                }
            }
        }
    };

}

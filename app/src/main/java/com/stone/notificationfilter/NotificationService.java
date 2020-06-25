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
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.blankj.utilcode.util.AppUtils;
import com.stone.notificationfilter.actioner.CopyActioner;
import com.stone.notificationfilter.actioner.DanMuActioner;
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
import com.stone.notificationfilter.util.Actioner;
import com.stone.notificationfilter.util.ImageUtil;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.actioner.FloatingTileActioner;
import com.stone.notificationfilter.util.ToolUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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

    private static String GROUP_KEY_NOTIFI_STROE = "com.stone.GROUP_KEY_NOTIFI_STROE";


    public static boolean isStartListener = true;
    public static boolean isCancalSystemNotification = false;

    private NotificationHandlerItemFileStorage notificationHandlerItemFileStorage;



    private ArrayList<NotificationHandlerItem> notificationHandlerItems;


    public static Set<String> selectAppList = null;
    public static Set<String> tempBlackAppLise = new HashSet<>();
    public static int notification_store_number;
//    True为白名单， false为黑名单
    public static  boolean appListMode = false;
    private boolean isSceenLock = false;
    private boolean isLandScape = false;
    private NotificationInfo notificationInfo=null;

    private static int NOTIFICATIONID = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"data refash");
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        isStartListener = true;
        init();
        Log.e(TAG,"service create");
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
        registerReceiver(mBroadcastReceiver, new IntentFilter(NOTIFICATION_FILTER_START_INTENT));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand create");
        init();
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }
    private  void init(){

        reloadData();
        createNotificationChannel();
        if (SpUtil.getBoolean(getApplicationContext(),"appSettings","notification_show", true)){
            addForegroundNotification();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind");
        reloadData();
        return super.onBind(intent);
    }

    public void reloadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isStartListener =true;
                    tempBlackAppLise.clear();
                    String packageNamestring = SpUtil.getString(getApplicationContext(),"appSettings","select_applists", "");
                    selectAppList = SpUtil.string2Set(packageNamestring);
                    appListMode = SpUtil.getBoolean(getApplicationContext(),"appSettings","applist_mode", false);
                    notification_store_number = Integer.parseInt(SpUtil.getString(getApplicationContext(),"appSettings","notification_store_number", "8"));
                    notificationHandlerItemFileStorage = new NotificationHandlerItemFileStorage(getApplicationContext(),true);
                    notificationHandlerItems = notificationHandlerItemFileStorage.getAllAsArrayList();
                    notificationHandlerItems.addAll(SystemBaseHandler.getSystemHandlerRule(getApplicationContext()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        Log.i(TAG,"onNotificationPosted:"+isStartListener);

        if (!isStartListener){
//            super.onNotificationPosted(sbn);
            return;
        }
        if (tempBlackAppLise.size() !=0){
            if (!(SpUtil.getBoolean(getApplicationContext(),"appSettings","notification_upblack_landscape",true) && !isLandScape)){
                if (tempBlackAppLise.contains(sbn.getPackageName())){
                    Log.i(TAG,sbn.getPackageName()+"is temp black");
                    cancelNotification(sbn.getKey());
                    return;
                }
            }
        }
        if(selectAppList.size()>1){
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

        try {
            notificationInfo = getNotificationInfo(sbn);

        }catch (Exception e){
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(notificationInfo.packageName) && notificationInfo.packageName.equals("com.stone.notificationfilter")){
            if (!TextUtils.isEmpty(notificationInfo.title) && !notificationInfo.title.equals("测试标题")){
                return;
            }
        }else{
            if (notification_store_number > 0){
                notificationRestore(notificationInfo);
            }

        }

        cancelNotification(notificationInfo.key);

        isCancalSystemNotification =false;
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
                    case "float_tile_notification":floatingTileAction(notificationInfo); isCancalSystemNotification =true;break;
                    case "drop_notification":isCancalSystemNotification =true; break;
                    case "copy_notification":new CopyActioner(notificationInfo,NotificationService.this).run();break;
                    case "click_notification":new RunIntentActioner(notificationInfo,NotificationService.this).run();break;
                    case "save_log_notification":new SaveToFileActioner(notificationInfo,NotificationService.this).run();break;
                    case "sound_notification":new NotificationSoundActioner(notificationInfo,NotificationService.this).run();break;
                    case "float_notification":new FloatCustomViewActioner(notificationInfo,NotificationService.this).run();isCancalSystemNotification =true;break;
                    case "danmu_notification":new DanMuActioner(notificationInfo,NotificationService.this).run();isCancalSystemNotification =true;break;
                }

                if(notificationHandlerItem.breakDown){
                    break;
                }
            }catch (Exception e){
                isCancalSystemNotification =false;
                e.printStackTrace();
            }
        }

    }



    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"notification remove");
        if (SpUtil.getBoolean(getApplicationContext(),"appSettings","on_notification_float_message_link",true)){
            notificationInfo = getNotificationInfo(sbn);
            new FloatCustomViewActioner(notificationInfo,NotificationService.this).remove();
        }
        super.onNotificationRemoved(sbn);
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

    public  void notificationRestore(NotificationInfo notificationInfo){

        if (!notificationInfo.isClearable){
            return;
        }
        if (notificationInfo.content == null && notificationInfo.title==null) {
            return;
        }

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (NOTIFICATIONID > notification_store_number){
            notificationManager.cancel(NOTIFICATIONID-notification_store_number);
        }

        String AppName = (String) PackageUtil.getAppNameFromPackname(getApplicationContext(),notificationInfo.packageName);
        Notification newMessageNotification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(AppName+": "+notificationInfo.title)
                .setLargeIcon(ImageUtil.drawable2Bitmap(notificationInfo.smallIcon.loadDrawable(getApplicationContext())))
                .setContentText(notificationInfo.content)
                .setContentIntent(notificationInfo.intent)
                .setGroup(GROUP_KEY_NOTIFI_STROE)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(NOTIFICATIONID++,newMessageNotification);

    }

    private void setSceenAngle(){
        Configuration mConfiguration = getApplicationContext().getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            isLandScape = true;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            isLandScape = false;
        }
    }

    private NotificationInfo getNotificationInfo(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        Bundle extras = sbn.getNotification().extras;
        NotificationInfo notificationInfo = new NotificationInfo(sbn.getId(),sbn.getKey(),sbn.getPostTime());
        notificationInfo.notification = notification;
        notificationInfo.isClearable = sbn.isClearable();
        notificationInfo.isOnGoing = sbn.isOngoing();
        notificationInfo.Tag = sbn.getTag();
        notificationInfo.packageName = sbn.getPackageName();
        notificationInfo.largeIcon = sbn.getNotification().getLargeIcon();
        notificationInfo.smallIcon = sbn.getNotification().getSmallIcon();
        notificationInfo.title = extras.getString(Notification.EXTRA_TITLE);
        notificationInfo.content = extras.getString(Notification.EXTRA_TEXT);
        notificationInfo.intent = sbn.getNotification().contentIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationInfo.ChannelID = notification.getChannelId();
        }
//        notificationInfo.ChannelID = extras.getString(Notification.EXTRA_CHANNEL_ID);
        notificationInfo.ChannelGROUPID = extras.getString(Notification.EXTRA_CHANNEL_GROUP_ID);

        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        Notification.Action[] action = notification.actions;

        //        String audioContentsURI = extras.getString(Notification.EXTRA_AUDIO_CONTENTS_URI);
//        String backgoundImageURI = extras.getString(Notification.EXTRA_BACKGROUND_IMAGE_URI);
//        String audioContentsUri = extras.getString(Notification.EXTRA_AUDIO_CONTENTS_URI);
//        String bigText = extras.getString(Notification.EXTRA_BIG_TEXT);
//        String bigText = extras.getString();
//        String channelGroupID = extras.getString(Notification.EXTRA_CHANNEL_GROUP_ID);
//        String channelID = extras.getString(Notification.EXTRA_CHANNEL_ID);
////        String EXTRA_CONTAINS_CUSTOM_VIEW = extras.getString(Notification.EXTRA_CONTAINS_CUSTOM_VIEW);
//
        String EXTRA_TEMPLATE = extras.getString(Notification.EXTRA_TEMPLATE);
//        Log.e(TAG,"TEMPLATE"+EXTRA_TEMPLATE);
//        String textLines = extras.getString(Notification.EXTRA_TEXT_LINES);
        MediaSession.Token EXTRA_MEDIA_SESSION = extras.getParcelable(Notification.EXTRA_MEDIA_SESSION);
////        String EXTRA_COMPACT_ACTIONS = extras.getString(Notification.EXTRA_COMPACT_ACTIONS);
//        String EXTRA_SUMMARY_TEXT = extras.getString(Notification.EXTRA_SUMMARY_TEXT);
//        String EXTRA_TITLE_BIG = extras.getString(Notification.EXTRA_TITLE_BIG);
//
//        RemoteViews remoteView = sbn.getNotification().contentView;

//        if (notification == null) return;

        if (isLandScape) {
            notificationInfo.sceenStatus =2;
        } else {
            notificationInfo.sceenStatus =1;
        }

        RemoteViews remoteViews = getBigContentView(getApplicationContext(), notification);
        if(remoteViews == null)
            remoteViews = getContentView(getApplicationContext(), notification);
        if (remoteViews != null)
        {
            notificationInfo.remoteViews = remoteViews;
        }


        notificationInfo.isInteractive = powerManager.isInteractive();
        return notificationInfo;
    }

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

    private RemoteViews mRemoteViews;
    private NotificationCompat.Builder mBuilder;
    private static final String NOTIFICATION_CHANNEL_ID = "FloatWindowService";
    private static final String NOTIFICATION_FILTER_START_INTENT = "com.stone.notificationfilter.FloatServiceStart";
    public static final int MANAGER_NOTIFICATION_ID = 0x1001;


    private void addForegroundNotification() {

        String contentText = "通知处理器开启运行中...";

        mBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .setContentTitle(contentText)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(GROUP_KEY_NOTIFI_STROE)
                .setGroupSummary(true)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText(contentText))
                .setAutoCancel(false);

        startForeground(MANAGER_NOTIFICATION_ID, mBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "通知处理器";
            String description = "防止通知处理器被后台关闭";
            int importance = NotificationManager.IMPORTANCE_LOW;
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

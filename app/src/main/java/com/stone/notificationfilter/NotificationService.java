package com.stone.notificationfilter;

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
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import androidx.core.app.NotificationCompat;

import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.stone.notificationfilter.actioner.CopyActioner;
import com.stone.notificationfilter.actioner.DanMuActioner;
import com.stone.notificationfilter.actioner.FloatCustomViewActioner;
import com.stone.notificationfilter.actioner.NotificationSoundActioner;
import com.stone.notificationfilter.actioner.RunIntentActioner;
import com.stone.notificationfilter.actioner.SaveToFileActioner;
import com.stone.notificationfilter.notificationhandler.NotificationHandlerJudge;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItem;
import com.stone.notificationfilter.notificationhandler.databases.NotificationHandlerItemFileStorage;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.notificationhandler.databases.SystemBaseHandler;
import com.stone.notificationfilter.util.ImageUtil;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.actioner.FloatingTileActioner;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Create by LingC on 2019/8/4 21:46
 */
public class NotificationService extends NotificationListenerService {
    private final static boolean DEBUG = false;

    private final static String TAG ="NotificationService";

    private static final String GROUP_KEY_NOTIFI_STROE = "com.stone.GROUP_KEY_NOTIFI_STROE";
    private static boolean isStartListener = false;
    private NotificationHandlerItemFileStorage notificationHandlerItemFileStorage;
    private ArrayList<NotificationHandlerItem> notificationHandlerItems;

    public static Set<String> selectAppList = null;
    public static Set<String> tempBlackAppLise = new HashSet<>();
    public static Set<String> systemImportAPP = new HashSet<>();


    public static boolean isForegroundNotification = true;
    public static boolean isNotificationStore;
//    True为白名单， false为黑名单
    public static  boolean appListMode = false;

//    public  boolean isCancalSystemNotification = false;
    private boolean isSceenLock = false;
    private boolean isLandScape = false;
    private boolean isNotificationUpblackLandscape = true;
    private NotificationInfo notificationInfo=null;
    private NotificationManager mNotificationManager;
    private static  int NOTIFICATIONID = 2;
    public static void offListener(){
        isStartListener = false;
    }
    public static void onListener(){
        isStartListener = true;
    }
//    private static Map<String, Boolean> isBlockNotification = new HashMap<>();
//    @SuppressLint("HandlerLeak")
//    private final Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (DEBUG) Log.e(TAG,"data refash");
//        }
//    };

    @Override
    public void onCreate() {
//        super.onCreate();


        init();
        if (DEBUG) Log.e(TAG,"service create");
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
        registerReceiver(mBroadcastReceiver, new IntentFilter(NOTIFICATION_FILTER_START_INTENT));
        systemImportAPP.add("com.android.incallui");
        systemImportAPP.add("com.android.server.telecom");

//

    }

    private  void init(){
        mNotificationManager = getSystemService(NotificationManager.class);
        isForegroundNotification = SpUtil.getBoolean(getApplicationContext(),"appSettings","notification_show", false);
        createNotificationChannel();
        if (isForegroundNotification){
            addForegroundNotification();
        }else {
            stopForeground(true);
        }
        isNotificationStore = Integer.parseInt(SpUtil.getString(getApplicationContext(), "appSettings", "notification_store_number", "32")) > 0;
        isNotificationUpblackLandscape = SpUtil.getBoolean(getApplicationContext(),"appSettings","notification_upblack_landscape",true);
        reloadData();

    }

    public void reloadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tempBlackAppLise.clear();
                    String packageNamestring = SpUtil.getString(getApplicationContext(),"appSettings","select_applists", "");
                    selectAppList = SpUtil.string2Set(packageNamestring);
                    appListMode = SpUtil.getBoolean(getApplicationContext(),"appSettings","applist_mode", false);

                    notificationHandlerItemFileStorage = new NotificationHandlerItemFileStorage(getApplicationContext(),true);
                    notificationHandlerItems = notificationHandlerItemFileStorage.getAllAsArrayList();
                    notificationHandlerItems.addAll(SystemBaseHandler.getSystemHandlerRule(getApplicationContext()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        if (sbn==null || !isStartListener ) return;
        if (DEBUG) Log.e(TAG,sbn.getKey());

        if( systemImportAPP.contains(sbn.getPackageName())){
            return;
        }
        boolean isCancalSystemNotification = true;
        boolean isRestoreNotification = true;
        // 监测屏幕反向
        setSceenAngle();

        // 临时禁止通知
        if (tempBlackAppLise.size() >0){
            if (!(isNotificationUpblackLandscape && !isLandScape)){
                if (tempBlackAppLise.contains(sbn.getPackageName())){
                    if (DEBUG) Log.i(TAG,sbn.getPackageName()+"is temp black");
                    cancelNotification(sbn.getKey());
                    return;
                }
            }
        }

        // 黑 白名单检测
        if(selectAppList.size()>1){
            if(appListMode^selectAppList.contains(sbn.getPackageName())){
                cancelNotification(sbn.getKey());
                return;
            }
        }

        try {
            notificationInfo = getNotificationInfo(sbn);
            if (notificationInfo == null) return;
        }catch (Exception e){
            e.printStackTrace();
        }


        if (!TextUtils.isEmpty(notificationInfo.packageName) && notificationInfo.packageName.equals("com.stone.notificationfilter")){
            if(notificationInfo.ChannelID.equals(NOTIFICATION_CHANNEL_ID)){
                if (DEBUG) Log.e(TAG,"restore: "+sbn.getKey());
                return;
            }
            cancelNotification(sbn.getKey());
        }

        try {
            for(NotificationHandlerItem notificationHandlerItem:notificationHandlerItems){
                    if (!NotificationHandlerJudge.isMatch(notificationInfo, notificationHandlerItem)){
//                        Log.i(TAG,notificationInfo.title+" in "+notificationHandlerItem.name+" patter is fail");
                        continue;
                    }
                   Log.i(TAG,notificationInfo.title+" in "+notificationHandlerItem.name+" patter is match");

                    notificationInfo.title = NotificationHandlerJudge.titleReplace(notificationInfo.title);
                    notificationInfo.content =NotificationHandlerJudge.contentReplace(notificationInfo.content);
                        switch (notificationHandlerItem.actioner){
                            case "float_tile_notification":
                                new Thread(()->{
                                    new FloatingTileActioner(notificationInfo,NotificationService.this,false).run();
                                }).start();
                                isCancalSystemNotification =true;
                                break;
                            case "system_notification":
                                isCancalSystemNotification = false;
                                break;
                            case "drop_notification":
                                isCancalSystemNotification = true;
                                isRestoreNotification =false;
                                break;

                            case "copy_notification":
                                new Thread(()->{
                                    new CopyActioner(notificationInfo,NotificationService.this).run();
                                }).start();
                                break;
                            case "click_notification":
                                new Thread(()->{
                                    new RunIntentActioner(notificationInfo,NotificationService.this).run();
                                }).start();
                                break;
                            case "save_log_notification":
                                new Thread(()->{
                                    new SaveToFileActioner(notificationInfo,NotificationService.this).run();
                                }).start();
                                break;
                            case "sound_notification":
                                new Thread(()->{
                                    new NotificationSoundActioner(notificationInfo,NotificationService.this).run();
                                }).start();
                                break;
                            case "float_notification":
                                new Thread(()->{
                                    new FloatCustomViewActioner(notificationInfo,NotificationService.this).run();
                                }).start();
                                isCancalSystemNotification = false;
                                isRestoreNotification = false;
                                break;
                            case "danmu_notification":
                                new Thread(()->{
                                    new DanMuActioner(notificationInfo,NotificationService.this).run();
                                }).start();
                                isCancalSystemNotification =true;
                                break;
                        }

                    if(notificationHandlerItem.breakDown){
                        break;
                    }
            }
            if (!notificationInfo.hasCustomView){
                if (isNotificationStore  && isRestoreNotification){
//                    if ()Log.i(TAG,"restore");
                    notificationRestore(notificationInfo);
                }

                if (isCancalSystemNotification){
                    if (notificationInfo.isOnGoing){
                        snoozeNotification(sbn.getKey(), 1000);
                    }else{
                        cancelNotification(sbn.getKey());
                    }
                }
            }
        }catch (Exception e){
//            isCancalSystemNotification =false;;
            e.printStackTrace();
        }

    }



    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (DEBUG) Log.i(TAG,"notification remove");
        if (sbn==null){
            return;
        }
        if (SpUtil.getBoolean(getApplicationContext(),"appSettings","on_notification_float_message_link",true)){
//            notificationInfo = getNotificationInfo(sbn);
            FloatCustomViewActioner.remove(sbn.getKey());
        }
//        super.onNotificationRemoved(sbn);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        offListener();
        unregisterReceiver(mBroadcastReceiver);
        stopForeground(true);
        if (DEBUG) Log.i(TAG,"unbind");
        return false;
    }

    @Override
    public void onListenerConnected() {
        if (!isStartListener){
            onListener();
        }
        if (DEBUG) Log.i(TAG,"notification service connected start");
    }

    @Override
    public void onListenerDisconnected() {
        offListener();
        if (DEBUG) Log.i(TAG,"notification service connected stop");
        requestRebind(new ComponentName(this, NotificationService.class));
    }

    public  void notificationRestore(NotificationInfo notificationInfo){
        if (DEBUG) Log.d(TAG,"restore notification");

        if(TextUtils.isEmpty(notificationInfo.title) && TextUtils.isEmpty(notificationInfo.content)){
            return;
        }
        String AppName = (String) PackageUtil.getAppNameFromPackname(getApplicationContext(),notificationInfo.packageName);
        Notification newMessageNotification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(AppName+": "+notificationInfo.title)
                .setLargeIcon(ImageUtil.drawable2Bitmap(notificationInfo.smallIcon.loadDrawable(getApplicationContext())))
                .setContentText(notificationInfo.content)
                .setContentIntent(notificationInfo.intent)
                .setGroup(GROUP_KEY_NOTIFI_STROE)
                .build();

        mNotificationManager.notify(Math.abs(notificationInfo.key.hashCode()),newMessageNotification);
        if (!isForegroundNotification){
            updateNotificationSummary();
        }
    }

    private void updateNotificationSummary() {
        if (DEBUG) Log.i(TAG,isForegroundNotification?"isForegroundNotification:true":"isForegroundNotification:flase");
        Notification newMessageNotification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.InboxStyle().setSummaryText("历史通知..."))
                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .setContentIntent(notificationInfo.intent)
                .setGroup(GROUP_KEY_NOTIFI_STROE)
                .setGroupSummary(true)
                .build();
        mNotificationManager.notify(MANAGER_NOTIFICATION_ID,newMessageNotification);
    }


    public static final String NOTIFICATION_CHANNEL_ID = "FloatWindowService";
    public static final String NOTIFICATION_FILTER_START_INTENT = "com.stone.notificationfilter.FloatServiceStart";
    public static final int MANAGER_NOTIFICATION_ID = 1;

    private void createNotificationChannel() {
        CharSequence name = "通知处理器";
        String description = "防止通知处理器被后台关闭和保存历史通知";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setShowBadge(false);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(channel);
        }
    }
    private void addForegroundNotification() {
        if (DEBUG) Log.i(TAG,isForegroundNotification?"isForegroundNotification:true":"isForegroundNotification:flase");
        String contentText = "通知处理器运行中...";
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification mBuilder = new NotificationCompat.Builder(this.getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setContentTitle(contentText)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker(contentText)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setSummaryText(contentText))
                .setGroup(GROUP_KEY_NOTIFI_STROE)
                .setGroupSummary(true)
                .build();
        startForeground(MANAGER_NOTIFICATION_ID, mBuilder);

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
        if (extras == null) return null;
        NotificationInfo notificationInfo = new NotificationInfo(sbn.getId(),sbn.getKey(),sbn.getPostTime());
        notificationInfo.notification = notification;
        notificationInfo.isClearable = sbn.isClearable();
        notificationInfo.isOnGoing = sbn.isOngoing();
        notificationInfo.Tag = sbn.getTag();
        notificationInfo.packageName = sbn.getPackageName();
        notificationInfo.largeIcon = sbn.getNotification().getLargeIcon();
        notificationInfo.smallIcon = sbn.getNotification().getSmallIcon();

        notificationInfo.title = extras.getString(Notification.EXTRA_TITLE,"");

        notificationInfo.content = extras.getString(Notification.EXTRA_TEXT,"");

        notificationInfo.intent = sbn.getNotification().contentIntent;
        notificationInfo.ChannelID = notification.getChannelId();

//        notificationInfo.ChannelID = extras.getString(Notification.EXTRA_CHANNEL_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            notificationInfo.ChannelGROUPID = extras.getString(Notification.EXTRA_CHANNEL_GROUP_ID);
        }

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

        notificationInfo.template = extras.getString(Notification.EXTRA_TEMPLATE,"");
//        String aa = extras.getString(Notification.EXTRA_CONTAINS_CUSTOM_VIEW,true);


        if(sbn.getNotification().bigContentView != null){
            notificationInfo.hasBigCustomView = true;
        }

        if(sbn.getNotification().contentView != null){
            notificationInfo.hasContentView = true;
        }
        if(sbn.getNotification().headsUpContentView != null){
            notificationInfo.hasHeadsUpContentView = true;
        }

        if(notificationInfo.hasBigCustomView){
            notificationInfo.remoteViews = sbn.getNotification().bigContentView;
        }else if(notificationInfo.hasContentView){
            notificationInfo.remoteViews = sbn.getNotification().contentView;
        }else if(notificationInfo.template.contains("MediaStyle")){
            MediaSession.Token mediaSession = extras.getParcelable(Notification.EXTRA_MEDIA_SESSION);
            notificationInfo.remoteViews = getBigContentView(getApplicationContext(), notificationInfo,mediaSession);
            if(notificationInfo.remoteViews == null){
                notificationInfo.remoteViews = getContentView(getApplicationContext(), notificationInfo,mediaSession);
            }
        }

        if(notificationInfo.hasContentView || notificationInfo.hasBigCustomView ||
                notificationInfo.hasHeadsUpContentView || notificationInfo.template.contains("MediaStyle")){
            notificationInfo.hasCustomView = true;
        }

//        sbn.getNotification().
        //        Log.e(TAG,"TEMPLATE"+EXTRA_TEMPLATE);
//        String textLines = extras.getString(Notification.EXTRA_TEXT_LINES);

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
//        sbn.getNotification().notify();



        notificationInfo.isInteractive = powerManager.isInteractive();
        return notificationInfo;
    }

    public static RemoteViews getBigContentView(Context context, NotificationInfo notification, MediaSession.Token mediaSession)
    {
        Notification.Builder builder = Notification.Builder.recoverBuilder(context, notification.notification);
        builder.setStyle(new Notification.MediaStyle().setMediaSession(mediaSession)).build();

//        Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
//        mediaStyle.setMediaSession(mediaSession);
//        Class<Notification.MediaStyle> mediaStyleClass = Notification.MediaStyle.class;
//        RemoteViews remoteViews = null;
//        try {
//            Method getBigRemoteView = mediaStyleClass.getDeclaredMethod("makeBigContentView");
//            remoteViews = (RemoteViews)getBigRemoteView.invoke(mediaStyle);
//            if (remoteViews == null) {
//                Log.e(TAG,"Remote error");
//            }
//        } catch (Exception e) {
//            Log.e(TAG,"reflace error");
//            e.printStackTrace();
//        }
//        getBigRemoteView.setAccessible(true);
//
//
//        Notification.Builder builder = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
//                .setStyle(new Notification.MediaStyle().setMediaSession(mediaSession));
        return builder.createBigContentView();
    }
    public static RemoteViews getContentView(Context context, NotificationInfo notification, MediaSession.Token mediaSession)
    {
        Notification.Builder builder = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setStyle(new Notification.MediaStyle().setMediaSession(mediaSession)); ;
//        Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
//        mediaStyle.setMediaSession(mediaSession);


        return builder.createContentView();
    }

    private void floatingTileAction(final NotificationInfo notificationInfo) {
                FloatingTileActioner floatingTile = new FloatingTileActioner(notificationInfo,NotificationService.this,false);
                floatingTile.run();
    }

//    private RemoteViews mRemoteViews;


    private void updateNotification(){
        if (DEBUG) Log.i(TAG,"updateNotification:"+isStartListener);
//        notificationCustomRemoteVIew();
//        mNotificationManager.notify(MANAGER_NOTIFICATION_ID, mBuilder.build());

    }

//    private void notificationCustomRemoteVIew() {
//        if(isStartListener){
//            mRemoteViews.setTextViewText(R.id.notification_filter_title, getResources().getString(R.string.service_start));
//            mRemoteViews.setImageViewResource(R.id.notification_filter_start, android.R.drawable.ic_media_play);
//        }else{
//            mRemoteViews.setTextViewText(R.id.notification_filter_title, getResources().getString(R.string.service_stop));
//            mRemoteViews.setImageViewResource(R.id.notification_filter_start, android.R.drawable.ic_media_pause);
//        }
//    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case Intent.ACTION_SCREEN_OFF:
                        isSceenLock =true;
                        if (DEBUG) Log.d(TAG, "屏幕关闭，变黑");
                        break;
                    case Intent.ACTION_SCREEN_ON:
                        if (DEBUG) Log.d(TAG, "屏幕开启，变亮");
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        isSceenLock =false;
                        if (DEBUG) Log.d(TAG, "解锁成功");
                        break;
                    case NOTIFICATION_FILTER_START_INTENT:
//                        isStartListener=!isStartListener;
//                        updateNotification();
                        init();
                        if (DEBUG) Log.i(TAG,"updateNotification:"+isStartListener);
                    default:
                        break;
                }
            }
        }
    };

}

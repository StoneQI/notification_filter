package com.stone.notificationfilter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//import android.support.v7.app.AlertDialog;
//import android.support.v7.preference.Preference;
//import android.support.v7.preference.PreferenceCategory;
//import android.support.v7.preference.PreferenceFragmentCompat;
//import android.support.v7.preference.PreferenceGroup;
//import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.stone.notificationfilter.dialogapppicker.DialogAppPicker;
import com.stone.notificationfilter.util.DialogUtil;
import com.stone.notificationfilter.util.NotificationInfo;
import com.stone.notificationfilter.actioner.FloatingTileActioner;
import com.stone.notificationfilter.actioner.TileObject;
import com.stone.notificationfilter.util.SpUtil;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceFragment;

/**
 * Create by LingC on 2019/8/4 21:54
 */
public class MainFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = "MainFragment";
    private boolean isCanDrawWindow;
    private boolean isNotificationListenerEnable;
    private static final String NOTIFICATION_CHANNEL_ID = "MainFragment";

    Dialog dia;



    @Override
    public void onResume() {
        super.onResume();
        isNotificationListenerEnable = true;
        isCanDrawWindow = true;
        if (!isNotificationListenerEnable(getContext())) {
            isNotificationListenerEnable = false;
            findPreference("notificatListen").setIcon(R.drawable.ic_warning_black_24dp);
        } else {
            findPreference("notificatListen").setIcon(null);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getContext())) {
                isCanDrawWindow = false;
                findPreference("flotatingWindow").setIcon(R.drawable.ic_warning_black_24dp);
            } else {
                findPreference("flotatingWindow").setIcon(null);
            }
        }
        if (!(isNotificationListenerEnable && isCanDrawWindow)) {
            Toast.makeText(getContext(), "权限不足，将无法正常使用应用", Toast.LENGTH_SHORT).show();
        }else {
            boolean isStart = SpUtil.getSp(getContext(),"appSettings").getBoolean("start_service", false);
            if (isStart){
                startNotificationListenerService();
//                Toast.makeText(getContext(), R.string.service_start, Toast.LENGTH_SHORT).show();
            }else {
//                Log.e("MainFragment",String.valueOf(isClicked));
                stopNotificationListenerService();
//                Toast.makeText(getContext(), "服务未开启", Toast.LENGTH_SHORT).show();
//                return false;
            }
//            if (){
//            hideInBackground
//                toggleNotificationListenerService();
//            }
        }

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName("appSettings");
        addPreferencesFromResource(R.xml.pref_lay);
//        addPreferencesFromResource(R.xml.pred_actioner_setting);
//        PreferenceCategory notifications = (PreferenceCategory) getPreferenceScreen ().findPreference (PreferenceKey.pref_notifications.name ());
//        addPreferencesFromResource (R.xml.pref_notifications, notifications);

//        SwitchPreferenceCompat switchPreference =(SwitchPreferenceCompat) findPreference("start_service");


//        findPreference("test_content").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                final FloatNotificationGroup floatNotificationGroup = FloatNotificationGroup.getInstance(getContext());
//
//                floatNotificationGroup.addView(getContext(),null);
////                floatNotificationGroup.addView(getContext());
//
//
//                return false;
//            }
//        });

        findPreference("start_service").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isClicked = (boolean)o;

                if(isClicked){

                    if (isNotificationListenerEnable && isCanDrawWindow){
                        Log.e("MainFragment",String.valueOf(isClicked));

                        startNotificationListenerService();
                        Toast.makeText(getContext(), R.string.service_start, Toast.LENGTH_SHORT).show();
                        return true;
                    }else {
                        Log.e("MainFragment",String.valueOf(isClicked));
                        Toast.makeText(getContext(), "权限不足，将无法开启服务", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else {
                    Log.e("MainFragment",String.valueOf(isClicked));
                    stopNotificationListenerService();
                    Toast.makeText(getContext(), R.string.service_stop, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });

        findPreference("select_applist").setOnPreferenceClickListener((new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String packageNamestring = SpUtil.getSp(getContext(), "appSettings").getString("select_applists", "");
                Log.e(TAG,"PackageNameList:"+packageNamestring);
                final Set<String> packageNames = SpUtil.string2Set(packageNamestring);
                final DialogAppPicker mDialog = new DialogAppPicker(getContext(), packageNames);
                mDialog.getDialog()
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newPackageNameList = SpUtil.set2String(packageNames);
                                Log.e(TAG,"newPackageNameList:"+newPackageNameList);
                                SpUtil.getSp(getContext(), "appSettings").edit().putString("select_applists", newPackageNameList).apply();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                return false;
            }
        }));
        findPreference("notificatListen").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                gotoNotificationAccessSetting();
                return false;
            }
        });

        findPreference("flotatingWindow").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName())), 1);
                return false;
            }
        });


//        findPreference("todoNotification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            //            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                onResume();
//                if (!isCanDrawWindow) {
//                    Toast.makeText(preference.getContext(), "无悬浮窗权限", Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//
//                createNotificationChannel();
//
//                NotificationManager notificationManager =
//                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                Intent intent = new Intent(getContext(),MainActivity.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
//                Notification  notification = new NotificationCompat.Builder(getContext(),NOTIFICATION_CHANNEL_ID)
//                        //指定通知的标题内容
//                        .setContentTitle("测试标题")
//                        //设置通知的内容
//                        .setContentText("测试内容：这是一条内容")
//                        //指定通知被创建的时间
//                        .setWhen(System.currentTimeMillis())
//                        //设置通知的小图标
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        //设置通知的大图标
//                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),
//                                R.drawable.ic_launcher_background))
//                        //添加点击跳转通知跳转
//                        .setContentIntent(pendingIntent)
//                        //实现点击跳转后关闭通知
//                        .setAutoCancel(true)
//                        .build();
//                notificationManager.notify(1,notification);
//                return false;
//            }
//        });

        findPreference("sendNotification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            //            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onResume();
                if (!isCanDrawWindow) {
                    Toast.makeText(preference.getContext(), "无悬浮窗权限", Toast.LENGTH_SHORT).show();
                    return false;
                }
                createNotificationChannel();
                NotificationManager notificationManager =
                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(getContext(),MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
                Notification  notification = new NotificationCompat.Builder(getContext(),NOTIFICATION_CHANNEL_ID)
                        //指定通知的标题内容
                        .setContentTitle("测试标题")
                        //设置通知的内容
                        .setContentText("测试内容：这是一条内容")
                        //指定通知被创建的时间
                        .setWhen(System.currentTimeMillis())
                        //设置通知的小图标
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        //设置通知的大图标
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_launcher_background))
                        //添加点击跳转通知跳转
                        .setContentIntent(pendingIntent)
                        //实现点击跳转后关闭通知
                        .setAutoCancel(true)
                        .build();
                notificationManager.notify(1,notification);
                return false;
            }
        });
        findPreference("floattitle_tileShowNum").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TileObject.clearAllTile();
                return false;
            }
        });
        findPreference("floattitle_tileDirection").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                TileObject.clearAllTile();
                return false;
            }
        });
        findPreference("floattitle_tilePosition").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogUtil.showDialog(preference.getContext(), new DialogUtil.onClickListener() {
                    //                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @SuppressLint("SourceLockedOrientationActivity")
                    @Override
                    public void onClick() {
                        Configuration mConfiguration = getContext().getResources().getConfiguration(); //获取设置的配置信息
                        int ori = mConfiguration.orientation;
                        Log.e(TAG,String.valueOf(ori));
                        if(ori != Configuration.ORIENTATION_PORTRAIT){
                            Log.e(TAG,String.valueOf(ori));
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                NotificationInfo notificationInfo = new NotificationInfo(1, "234", System.currentTimeMillis());
                                notificationInfo.setPackageName(getContext().getPackageName());
                                notificationInfo.setTitle("设置竖屏位置");
                                notificationInfo.setContent("上下移动设置竖屏初始位置");
                                FloatingTileActioner floatingTile = new FloatingTileActioner(notificationInfo, getContext(), true);
                                floatingTile.run(getActivity());
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(task, 500);

                    }
                }, new DialogUtil.onClickListener() {
                    @SuppressLint("SourceLockedOrientationActivity")
                    @Override
                    public void onClick() {
                        Configuration mConfiguration = getContext().getResources().getConfiguration(); //获取设置的配置信息
                        int ori = mConfiguration.orientation;
                        Log.e(TAG,String.valueOf(ori));
                        if(ori != Configuration.ORIENTATION_LANDSCAPE){
                            Log.e(TAG,String.valueOf(ori));
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                NotificationInfo notificationInfo = new NotificationInfo(1, "234", System.currentTimeMillis());
                                notificationInfo.setPackageName(getContext().getPackageName());
                                notificationInfo.setTitle("设置横屏位置");
                                notificationInfo.setContent("上下移动设置横屏初始位置");
                                FloatingTileActioner floatingTile = new FloatingTileActioner(notificationInfo, getContext(), true);
                                floatingTile.run(getActivity());
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(task, 1000);


                    }
                });
                return false;
            }
        });
        findPreference("showFiliter").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                MainActivity mainActivity = (MainActivity)getActivity();
//                mainActivity.replaceFragment(new FiliterActivity());
                Intent intent = new Intent(getActivity(), FiliterActivity.class);
                startActivity(intent);
//
                return false;
            }
        });

        findPreference("checkFiliter").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(getActivity(), CheckNotificationLogActivity.class);
                startActivity(intent);
                return false;
            }
        });

        findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(preference.getContext())
                        .setTitle("关于 - " + getString(R.string.app_name))
                        .setMessage("版本：" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")" +
                                "\n开发者：Stone" +
                                "\n编译日期：" + BuildConfig.releaseTime+
                                "\n引用开源项目：HelloLingC@floating-tile"+
                                 "开源项目地址：https://github.com/HelloLingC/floating-tile")
                        .setPositiveButton("关闭", null)
                        .show();
                return false;
            }
        });

        findPreference("donate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), Donate.class);
                startActivity(intent);
                return false;
            }
        });

    }

    private boolean isNotificationListenerEnable(Context context) {
        if (TextUtils.isEmpty(context.getPackageName())) {
            return false;
        }
        Set<String> packagenameSet = NotificationManagerCompat.getEnabledListenerPackages(context);
        return packagenameSet.contains(context.getPackageName());
    }

    private boolean gotoNotificationAccessSetting() {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                startActivity(intent);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "测试通知";
            String description = "测试通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(false);
//            channel.setSound(Uri sound, AudioAttributes audioAttributes);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    private void startNotificationListenerService() {
        ComponentName thisComponent = new ComponentName(getContext(),  com.stone.notificationfilter.NotificationService.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void stopNotificationListenerService() {
        ComponentName thisComponent = new ComponentName(getContext(),   com.stone.notificationfilter.NotificationService.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged " + key);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, getString(R.string.on_preference_change_toast_message, preference.getKey(), newValue.toString()));
        return true;
    }
}

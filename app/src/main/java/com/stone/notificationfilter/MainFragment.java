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
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;

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
import com.stone.notificationfilter.fragment.FloatTileCustomViewActivity;
import com.stone.notificationfilter.util.DialogUtil;
import com.stone.notificationfilter.util.NotificationCollectorMonitorService;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.actioner.FloatingTileActioner;
import com.stone.notificationfilter.actioner.TileObject;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.notificationhandler.FiliterActivity;
import com.stone.notificationfilter.util.ToolUtils;

import java.io.File;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

//import moe.shizuku.preference.Preference;
//import moe.shizuku.preference.PreferenceFragment;

/**
 * Create by LingC on 2019/8/4 21:54
 */
public class MainFragment extends PreferenceFragment {
    private static final String TAG = "MainFragment";
    private boolean isCanDrawWindow;
    private boolean isNotificationListenerEnable;
    private static final String NOTIFICATION_CHANNEL_ID = "MainFragment";

    Dialog dia;



    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume is runing");
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
//                NotificationService.isStartListener=true;
                getActivity().startService(new Intent(getActivity(), NotificationCollectorMonitorService.class));
//                Toast.makeText(getContext(), R.string.service_start, Toast.LENGTH_SHORT).show();
            }else {
                NotificationService.isStartListener=false;
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.pref_lay);//加载xml文件
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName("appSettings");
        addPreferencesFromResource(R.xml.pref_lay);

        findPreference("start_service").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isClicked = (boolean)o;

                if(isClicked){

                    if (isNotificationListenerEnable && isCanDrawWindow){
                        Log.e("MainFragment",String.valueOf(isClicked));
//                        Intent intent = new Intent(getActivity(),NotificationService.class);
//                        getActivity().startService(intent);
                        NotificationService.isStartListener=true;
                        getActivity().startService(new Intent(getActivity(), NotificationCollectorMonitorService.class));
                        getActivity().startService(new Intent(getActivity(), NotificationService.class));
//                        startNotificationListenerService();
                        Toast.makeText(getContext(), R.string.service_start, Toast.LENGTH_SHORT).show();
                        return true;
                    }else {
                        Log.e("MainFragment",String.valueOf(isClicked));
                        Toast.makeText(getContext(), "权限不足，将无法开启服务", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else {
                    Log.e("MainFragment",String.valueOf(isClicked));
//                    stopNotificationListenerService();
                    NotificationService.isStartListener=false;
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
                                SpUtil.putString(getContext(), "appSettings","select_applists", newPackageNameList);
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


        findPreference("message_replay").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isClicked = (boolean)o;

                if(isClicked){
                            AlertDialog.Builder m = new AlertDialog.Builder(getContext())
                                    .setIcon(R.drawable.ic_launcher).setMessage(R.string.message_reply_tip)
                                    .setIcon(R.drawable.ic_launcher)
                                    .setNegativeButton("小窗模式开关", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(SpUtil.getBoolean(getContext(), "appSettings","freeform_mode_switch", true)){
                                                SpUtil.putBoolean(getContext(), "appSettings","freeform_mode_switch", false);
                                                Toast.makeText(getContext(), R.string.freeform_mode_stop, Toast.LENGTH_SHORT).show();
                                            }else{
                                                SpUtil.putBoolean(getContext(), "appSettings","freeform_mode_switch", true);
                                                Toast.makeText(getContext(), R.string.freeform_mode_start, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                            if (!ToolUtils.checkAppInstalled(getContext(),"com.google.android.projection.gearhead")){
                                if(ToolUtils.copyApkFromRaws(getContext(),R.raw.messageauto)) {
                                    m.setPositiveButton("安装", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            File f = new File(getContext().getFilesDir().getAbsolutePath() + "/messageauto.apk");
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Uri apkUri;

                                            // Android 7.0 以上不支持 file://协议 需要通过 FileProvider 访问 sd卡 下面的文件，所以 Uri 需要通过 FileProvider 构造，协议为 content://
                                            if (Build.VERSION.SDK_INT >= 24) {
                                                // content:// 协议
                                                apkUri = FileProvider.getUriForFile(getContext(), "com.stone.notificationfilter.fileProvider", f);
                                                //Granting Temporary Permissions to a URI
                                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            } else {
                                                // file:// 协议
                                                apkUri = Uri.fromFile(f);
                                            }

                                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                                            try {
                                                getContext().startActivity(intent);

//                                                return true;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }else{
                                m.setPositiveButton("插件已安装",null);
                            }
                            m.show();
                        }

                return true;
            }
        });

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
                                notificationInfo.packageName = getContext().getPackageName();
                                notificationInfo.title = "设置竖屏位置";
                                notificationInfo.content = "上下移动设置竖屏初始位置";
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
                                notificationInfo.packageName = getContext().getPackageName();
                                notificationInfo.title = "设置横屏位置";
                                notificationInfo.content = "上下移动设置横屏初始位置";
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
                return true;
            }
        });


        findPreference("floattitle_custom_view").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                MainActivity mainActivity = (MainActivity)getActivity();
//                mainActivity.replaceFragment(new FiliterActivity());
                Intent intent = new Intent(getActivity(), FloatTileCustomViewActivity.class);
                startActivity(intent);
//
                return true;
            }
        });

        findPreference("checkFiliter").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(getActivity(), CheckNotificationLogActivity.class);
                startActivity(intent);
                return true;
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

}

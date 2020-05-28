package com.lingc.notificationfilter;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

//import android.support.v7.app.AlertDialog;
//import android.support.v7.preference.Preference;
//import android.support.v7.preference.PreferenceCategory;
//import android.support.v7.preference.PreferenceFragmentCompat;
//import android.support.v7.preference.PreferenceGroup;
//import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lingc.notificationfilter.util.DialogUtil;
import com.lingc.notificationfilter.util.NotificationCollectorMonitorService;
import com.lingc.notificationfilter.util.NotificationInfo;
import com.lingc.notificationfilter.actioner.FloatingTile;
import com.lingc.notificationfilter.actioner.TileObject;

import java.util.Set;

/**
 * Create by LingC on 2019/8/4 21:54
 */
public class MainFragment extends PreferenceFragmentCompat {
    private boolean isCanDrawWindow;
    private boolean isNotificationListenerEnable;

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
            if (!isNotificationListenerEnable(getContext())){
                toggleNotificationListenerService();
            }
//            Log.e("MainFragment","权限足");
//            if(SpUtil.getSp(getContext(),"appSettings").getBoolean("start_service", false)){
//                Log.e("MainFragment","start Service2");
//                Intent intent = new Intent(getContext(), NotificationService.class);
//                getActivity().startService(intent);
////                Toast.makeText(getContext(), R.string.service_start, Toast.LENGTH_SHORT).show();
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
        findPreference("start_service").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isClicked = (boolean)o;

                if(isClicked){

                    if (isNotificationListenerEnable && isCanDrawWindow){
                        Log.e("MainFragment",String.valueOf(isClicked));
                        ComponentName thisComponent = new ComponentName(getContext(),  NotificationService.class);
                        PackageManager pm = getActivity().getPackageManager();
                        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                        Toast.makeText(getContext(), R.string.service_start, Toast.LENGTH_SHORT).show();
                        return true;
                    }else {
                        Log.e("MainFragment",String.valueOf(isClicked));
                        Toast.makeText(getContext(), "权限不足，将无法开启服务", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else {
                    Log.e("MainFragment",String.valueOf(isClicked));
                    ComponentName thisComponent = new ComponentName(getContext(),  NotificationService.class);
                    PackageManager pm = getActivity().getPackageManager();
                    pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    Toast.makeText(getContext(), R.string.service_stop, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });

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
        findPreference("sendNotification").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            //            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onResume();
                if (!isCanDrawWindow) {
                    Toast.makeText(preference.getContext(), "无悬浮窗权限", Toast.LENGTH_SHORT).show();
                    return false;
                }

                NotificationInfo notificationInfo = new NotificationInfo(1,"234",System.currentTimeMillis());
                notificationInfo.setPackageName(getContext().getPackageName());
                notificationInfo.setTitle("Title");
                notificationInfo.setContent("Message");
                FloatingTile floatingTile = new FloatingTile(notificationInfo,getContext());
                floatingTile.setLastTile(TileObject.lastFloatingTile);
                floatingTile.showWindow();
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
                    @Override
                    public void onClick() {
                        NotificationInfo notificationInfo = new NotificationInfo(1,"234",System.currentTimeMillis());

                        notificationInfo.setPackageName(getContext().getPackageName());
                        notificationInfo.setTitle("Title");
                        notificationInfo.setContent("Message");
                        FloatingTile floatingTile = new FloatingTile(notificationInfo,getContext());
                        floatingTile.setLastTile(TileObject.lastFloatingTile);
                        floatingTile.isEditPos = true;
                        floatingTile.showWindow();
                    }
                });
                return false;
            }
        });
        findPreference("showFiliter").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(getActivity(), FiliterActivity.class);
                startActivity(intent);
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

//    private void addPreferencesFromResource (int id, PreferenceGroup newParent) {
//        PreferenceScreen screen = getPreferenceScreen ();
//        int last = screen.getPreferenceCount ();
//        addPreferencesFromResource (id);
//        while (screen.getPreferenceCount () > last) {
//            Preference p = screen.getPreference (last);
//            screen.removePreference (p); // decreases the preference count
//            newParent.addPreference (p);
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void toggleNotificationListenerService() {
        ComponentName thisComponent = new ComponentName(getContext(),  NotificationCollectorMonitorService.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }
}

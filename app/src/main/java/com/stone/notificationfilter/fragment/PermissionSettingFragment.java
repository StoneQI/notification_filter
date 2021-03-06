package com.stone.notificationfilter.fragment;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stone.notificationfilter.NotificationService;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.NotificationCollectorMonitorService;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.ToolUtils;

import java.util.Set;

public class PermissionSettingFragment extends PreferenceFragmentCompat {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PermiSettingFragment";



    private boolean isCanDrawWindow;
    private boolean isNotificationListenerEnable;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume is runing");
        isNotificationListenerEnable = true;
        isCanDrawWindow = true;
        if (!ToolUtils.isNotificationListenerEnable(getContext())) {
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
                NotificationService.offListener();
            }
        }

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("appSettings");
        addPreferencesFromResource(R.xml.fragment_permission_setting_preference);

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
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName())), 1);
                return false;
            }
        });
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

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_permission_setting, container, false);
//    }
}
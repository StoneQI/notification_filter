



package com.stone.notificationfilter.fragment;

import android.os.Bundle;
import android.util.Log;

import androidx.navigation.Navigation;
import androidx.preference.PreferenceFragmentCompat;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dialogapppicker.DialogAppPicker;
import com.stone.notificationfilter.dummy.GlobalConst;
import com.stone.notificationfilter.util.SpUtil;

import java.util.Set;

public class NotificationRuleSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.notification_rule_setting_preferences, rootKey);
        findPreference("dropNotification").setOnPreferenceClickListener((preference -> {
            navigateRuleShow(GlobalConst.DROP_NOTIFICATION);
            return false;
        }));
        findPreference("systemNotification").setOnPreferenceClickListener((preference -> {
            navigateRuleShow(GlobalConst.SYSTEM_NOTIFICATION);
            return false;
        }));
        findPreference("floatViewNotification").setOnPreferenceClickListener((preference -> {
            navigateRuleShow(GlobalConst.FLOAT_VIEW_NOTIFICATION);
            return false;
        }));
        findPreference("bulletNotification").setOnPreferenceClickListener((preference -> {
            navigateRuleShow(GlobalConst.BULLET_NOTIFICATION);
            return false;
        }));
        findPreference("floatTileNotification").setOnPreferenceClickListener((preference -> {
            navigateRuleShow(GlobalConst.FLOAT_VIEW_NOTIFICATION);
            return false;
        }));
        findPreference("playMusicNotification").setOnPreferenceClickListener((preference -> {
//            navigateRuleShow(GlobalConst.DROP_NOTIFICATION);
            final String packageNamestring = SpUtil.getSp(getContext(), "appSettings").getString(GlobalConst.PLAY_MUSIC_NOTIFICATION, "");
//            Log.e(TAG,"PackageNameList:"+packageNamestring);
            final Set<String> packageNames = SpUtil.string2Set(packageNamestring);
            final DialogAppPicker mDialog = new DialogAppPicker(getContext(), packageNames);
            mDialog.getDialog()
                    .setPositiveButton("确定", (dialog, which) -> {
                        String newPackageNameList = SpUtil.set2String(packageNames);
//                        Log.e(TAG,"newPackageNameList:"+newPackageNameList);
                        SpUtil.putString(getContext(), "appSettings",GlobalConst.PLAY_MUSIC_NOTIFICATION, newPackageNameList);
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
            return false;
        }));
        findPreference("saveLogNotification").setOnPreferenceClickListener((preference -> {
            final String packageNamestring = SpUtil.getSp(getContext(), "appSettings").getString(GlobalConst.SAVE_LOG_NOTIFICATION, "");
//            Log.e(TAG,"PackageNameList:"+packageNamestring);
            final Set<String> packageNames = SpUtil.string2Set(packageNamestring);
            final DialogAppPicker mDialog = new DialogAppPicker(getContext(), packageNames);
            mDialog.getDialog()
                    .setPositiveButton("确定", (dialog, which) -> {
                        String newPackageNameList = SpUtil.set2String(packageNames);
//                        Log.e(TAG,"newPackageNameList:"+newPackageNameList);
                        SpUtil.putString(getContext(), "appSettings",GlobalConst.SAVE_LOG_NOTIFICATION, newPackageNameList);
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
            return false;
        }));
    }

    private void navigateRuleShow(String value) {
        Bundle bundle = new Bundle();
        bundle.putString(NotificationRuleListFragment.ARG_NOTIFICATION_RULE_NAME, value);
//        NotificationRuleListFragment.newInstance(value);
//            Navigation.createNavigateOnClickListener(R.id.action_notificationHanlderFragment_to_addNotificationHandlerFragment,bundle);
        Navigation.findNavController(getView()).navigate(R.id.action_notificationRuleSettingsFragment_to_notificationRuleListFragment,bundle);
    }
}
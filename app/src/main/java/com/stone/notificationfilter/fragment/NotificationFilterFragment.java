package com.stone.notificationfilter.fragment;

import android.os.Bundle;


import com.stone.notificationfilter.R;

import moe.shizuku.preference.PreferenceFragment;

public class NotificationFilterFragment extends PreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("appSettings");
        addPreferencesFromResource(R.xml.notification_filte_setting);
    }
}

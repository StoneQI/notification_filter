package com.lingc.notificationfilter;

import android.os.Bundle;
//import android.support.v7.preference.PreferenceFragmentCompat;

import androidx.preference.PreferenceFragmentCompat;

public class ActionerSettingFragment extends PreferenceFragmentCompat {
    private final static  String TAG ="ActionerSettingFragment";
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName("actionerSettings");
        addPreferencesFromResource(R.xml.pred_actioner_setting);
    }
}

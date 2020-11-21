package com.stone.notificationfilter.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.dialogapppicker.DialogAppPicker;
import com.stone.notificationfilter.util.SpUtil;
//import com.tencent.mmkv.MMKV;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GlobalSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GlobalSettingFragment extends PreferenceFragmentCompat {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "GlobalSettingFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GlobalSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GloblSetting.
     */
    // TODO: Rename and change types and number of parameters
    public static GlobalSettingFragment newInstance(String param1, String param2) {
        GlobalSettingFragment fragment = new GlobalSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        MMKV preferences = MMKV.mmkvWithID("myData");
//        SharedPreferences.Editor editor = preferences.edit();
//        getPreferenceManager().setSharedPreferencesName(editor);

        getPreferenceManager().setSharedPreferencesName("appSettings");
        addPreferencesFromResource(R.xml.fragment_global_setting_preference);

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
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_globl_setting, container, false);
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//
////        view.findViewById(R.id.back).setOnClickListener(
////                Navigation.createNavigateOnClickListener(R.id.action_globlSetting_to_mainNavHastFragment));
//        super.onViewCreated(view, savedInstanceState);
//    }
}
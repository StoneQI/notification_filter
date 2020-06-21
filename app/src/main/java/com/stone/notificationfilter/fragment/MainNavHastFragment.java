package com.stone.notificationfilter.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stone.notificationfilter.BuildConfig;
import com.stone.notificationfilter.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainNavHastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainNavHastFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainNavHastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainNavHastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainNavHastFragment newInstance(String param1, String param2) {
        MainNavHastFragment fragment = new MainNavHastFragment();
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

//        Button button =
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_nav_hast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.permission_setting).setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_mainNavHastFragment_to_permissionSettingFragment)
        );
        view.findViewById(R.id.notification_handler).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainNavHastFragment_to_notificationHanlderFragment));
        view.findViewById(R.id.notification_log).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainNavHastFragment_to_notificationLogFragment));
        view.findViewById(R.id.global_setting).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainNavHastFragment_to_globlSetting));
        view.findViewById(R.id.float_tile_setting).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainNavHastFragment_to_floatTilesSettingFragment));
        view.findViewById(R.id.other_setting).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainNavHastFragment_to_otherSettingFragment));
        view.findViewById(R.id.danmu_tile_setting).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainNavHastFragment_to_floatDanMuSettingFragment));

        view.findViewById(R.id.donate).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mainNavHastFragment_to_donateFragment));

        view.findViewById(R.id.about_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("关于 - " + getString(R.string.app_name))
                        .setMessage("版本：" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")" +
                                "\n开发者：Stone" +
                                "\n编译日期：" + BuildConfig.releaseTime+
                                "\n引用开源项目：HelloLingC@floating-tile"+
                                "开源项目地址：https://github.com/HelloLingC/floating-tile")
                        .setPositiveButton("关闭", null)
                        .show();
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }
}
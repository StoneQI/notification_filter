package com.stone.notificationfilter.fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.Preference;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.stone.notificationfilter.BuildConfig;
import com.stone.notificationfilter.MainActivity3;
import com.stone.notificationfilter.NotificationService;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.NotificationCollectorMonitorService;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.ToolUtils;

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
    private static final String TAG ="MainNavHastFragment" ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean isCanDrawWindow;
    private boolean isNotificationListenerEnable;
    private static final String NOTIFICATION_CHANNEL_ID = "MainNavHastFragment";
    private Switch start_sevice;

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
        setHasOptionsMenu(true);



//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

//        Button button =
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume is runing");
        isNotificationListenerEnable = true;
        isCanDrawWindow = true;
        if (!ToolUtils.isNotificationListenerEnable(getContext())) {
            isNotificationListenerEnable = false;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getContext())) {
                isCanDrawWindow = false;
            }
        }
        if (!(isNotificationListenerEnable && isCanDrawWindow)) {
            start_sevice.setChecked(false);
        }else {
            boolean isStart = SpUtil.getBoolean(getContext(),"appSettings","start_service", true);
            if (isStart){
                NotificationService.isStartListener=true;
                getActivity().startService(new Intent(getActivity(), NotificationCollectorMonitorService.class));
                if (!start_sevice.isChecked()){
                    start_sevice.setChecked(true);
                }
//                start_sevice.setChecked(true);
//                Toast.makeText(getContext(), R.string.service_start, Toast.LENGTH_SHORT).show();
            }else {
                NotificationService.isStartListener = false;
                if (start_sevice.isChecked()) {
                    start_sevice.setChecked(false);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_nav_hast, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

//        Toolbar toolbar = getActivity().getActionBar();

        start_sevice = view.findViewById(R.id.start_service);
        start_sevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (isNotificationListenerEnable && isCanDrawWindow){
                        Log.e("MainFragment",String.valueOf(isChecked));
                        NotificationService.isStartListener=true;
                        getActivity().startService(new Intent(getActivity(), NotificationCollectorMonitorService.class));
                        getActivity().startService(new Intent(getActivity(), NotificationService.class));
//                        Toast.makeText(getContext(), R.string.service_start, Toast.LENGTH_SHORT).show();
//                        buttonView.setChecked(true);
                        SpUtil.putBoolean(getContext(),"appSettings","start_service", true);
                    }else {
                        Log.e("MainFragment",String.valueOf(isChecked));
                        Toast.makeText(getContext(), "权限不足，将无法开启服务", Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                        SpUtil.putBoolean(getContext(),"appSettings","start_service", false);

                    }
                }else {
                    Log.e("MainFragment",String.valueOf(isChecked));
//                    stopNotificationListenerService();
                    NotificationService.isStartListener=false;
                    Toast.makeText(getContext(), R.string.service_stop, Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                    SpUtil.putBoolean(getContext(),"appSettings","start_service", false);

                }
            }
        });


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

        view.findViewById(R.id.send_test_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificationChannel();
                NotificationManager notificationManager =
                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(getContext(), MainActivity3.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
                Notification notification = new NotificationCompat.Builder(getContext(),NOTIFICATION_CHANNEL_ID)
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
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //对菜单项点击内容进行设置
        int id = item.getItemId();
        if (id == R.id.donate_list) {
            AlertDialog.Builder m = new AlertDialog.Builder(getContext())
                    .setIcon(R.drawable.ic_launcher).setMessage(R.string.donate_list)
                    .setIcon(R.drawable.ic_launcher);
//                    .setPositiveButton("确认",null);
            m.show();

        }
        return super.onOptionsItemSelected(item);
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
}
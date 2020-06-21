package com.stone.notificationfilter.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.widget.Toast;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.actioner.FloatingTileActioner;
import com.stone.notificationfilter.actioner.TileObject;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.DialogUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.ToolUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FloatTilesSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FloatTilesSettingFragment extends PreferenceFragmentCompat {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG ="FloatSettingFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FloatTilesSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FloatTilesSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FloatTilesSettingFragment newInstance(String param1, String param2) {
        FloatTilesSettingFragment fragment = new FloatTilesSettingFragment();
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
        getPreferenceManager().setSharedPreferencesName("appSettings");
        addPreferencesFromResource(R.xml.fragment_float_tiles_setting_preference);


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

        findPreference("floattitle_custom_view").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                MainActivity mainActivity = (MainActivity)getActivity();
//                mainActivity.replaceFragment(new FiliterActivity());
                Navigation.findNavController(getView()).navigate(R.id.action_floatTilesSettingFragment_to_floatTileCustomViewFragment);
//                Intent intent = new Intent(getActivity(), FloatTileCustomViewActivity.class);
//                startActivity(intent);//
                return true;
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

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_float_tiles_setting, container, false);
//    }
}
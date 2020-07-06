package com.stone.notificationfilter.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.ImageUtil;
import com.stone.notificationfilter.util.JxdUtils;
import com.stone.notificationfilter.util.SpUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherSettingFragment extends PreferenceFragmentCompat {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int FILE_REQUEST_CODE = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OtherSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherSettingFragment newInstance(String param1, String param2) {
        OtherSettingFragment fragment = new OtherSettingFragment();
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
        addPreferencesFromResource(R.xml.fragment_others_setting_preference);

        findPreference("setting_notification_float").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                MainActivity mainActivity = (MainActivity)getActivity();
//                mainActivity.replaceFragment(new FiliterActivity());
//                Navigation.findNavController(getView()).navigate(R.id.action_floatTilesSettingFragment_to_floatTileCustomViewFragment);
//                Intent intent = new Intent(getActivity(), FloatTileCustomViewAictivy.class);
//                startActivity(intent);//
//                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
//                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
//                        .setCheckPermission(true)
//                        .setShowImages(true)
//                        .setShowFiles(false)
//                        .setShowAudios(false)
//                        .setShowVideos(false)
//                        .setSingleChoiceMode(true)
//                        .enableImageCapture(false)
//                        .setSkipZeroSizeFiles(true)
//                        .build());
//                startActivityForResult(intent, FILE_REQUEST_CODE);
                chooseFile();
                return true;
            }
        });


    }

    private static final String TAG1 = "FileChoose";

    // 调用系统文件管理器
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*").addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Choose File"), FILE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if(data ==null || data.getData()==null){
                return;
            }
            if (requestCode == FILE_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    String imagePath = JxdUtils.getPath(getContext(), uri);

                    String detaleFilePath =  SpUtil.getString(getContext(), "appSettings", "notification_float_foreground_path", "-1");
                    if (!TextUtils.isEmpty(imagePath) ) {
                        try {
                            String newFileName = System.currentTimeMillis()%10+'-'+JxdUtils.getFileNameforPath(imagePath);
                            JxdUtils.copyFile(new File(imagePath), new File(getContext().getFilesDir(),newFileName ));
                            SpUtil.putString(getContext(), "appSettings", "notification_float_foreground_path",newFileName);
                            if (!detaleFilePath.equals("-1")) {
                                getContext().deleteFile(detaleFilePath);
//                                getContext().deleteFile(imagePath);
                            }
                            Toast.makeText(getActivity(), "图片设置成功", Toast.LENGTH_SHORT).show();

                            return;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(getActivity(), "图片设置失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "你没有选择任何图片", Toast.LENGTH_SHORT).show();
                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_other_setting, container, false);
//    }
}
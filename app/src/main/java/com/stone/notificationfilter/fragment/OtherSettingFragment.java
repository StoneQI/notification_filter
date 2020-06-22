package com.stone.notificationfilter.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.ImageUtil;

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
                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .setShowFiles(false)
                        .setShowAudios(false)
                        .setShowVideos(false)
                        .setSingleChoiceMode(true)
                        .enableImageCapture(false)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent, FILE_REQUEST_CODE);
                return true;
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case FILE_REQUEST_CODE:
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files == null || files.size() ==0){
                    return;
                }
                Glide.with(getContext()).load(files.get(0).getUri())//签到整体 背景
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                ImageUtil.saveImageToData(getContext(),"notification_float_foreground.png",ImageUtil.drawable2Bitmap(resource));
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }        //设置宽高
                        });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
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
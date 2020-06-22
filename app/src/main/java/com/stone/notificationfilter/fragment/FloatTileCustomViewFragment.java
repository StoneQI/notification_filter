package com.stone.notificationfilter.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cbman.roundimageview.RoundImageView;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

//import com.luck.picture.lib.PictureSelector;
//import com.luck.picture.lib.config.PictureConfig;
//import com.luck.picture.lib.config.PictureMimeType;
//import com.luck.picture.lib.entity.LocalMedia;
//import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.CheckUtil;
import com.stone.notificationfilter.util.ImageUtil;
import com.stone.notificationfilter.util.SpUtil;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FloatTileCustomViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FloatTileCustomViewFragment extends Fragment implements ColorPickerDialogListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private View floatTileView;

    private int iconWidthHeightValue=-1;
    private int iconTypeValue=-1;
    private  int iconRidusValue=-1;
    private int titleTextSizeValue=-1;
    private int contentTextSizeValue=-1;
    private int rootPaddingTopAndBottomValue =-1;
    private int rootPaddingLeftOrRightValue = -1;
    private  int rootElevationValue=-1;
    private int titleTextColorValue=-1;
    private int contentTextColorValue=-1;

    private int rootLayRidusValue=-1;
    private int rootLayColorValue=-1;
    private  int rootLayBackGround = -1;

    private LinearLayout rootLayout;
    private TextView titleText;
    private TextView contentText;
    private RoundImageView imageIcon;
    private ViewGroup.LayoutParams imageIconLayoutParams;
    private final int FILE_REQUEST_CODE = 2;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FloatTileCustomViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FloatTileCustomViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FloatTileCustomViewFragment newInstance(String param1, String param2) {
        FloatTileCustomViewFragment fragment = new FloatTileCustomViewFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_float_tile_custom_view, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case FILE_REQUEST_CODE:
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files == null || files.size() ==0){
                    return;
                }
                Uri fileUri = files.get(0).getUri();
                Glide.with(getContext()).load(files.get(0).getUri())//签到整体 背景
                                        .into(new CustomTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                    ImageUtil.saveImageToData(getContext(),"float_tile_background.9.png",ImageUtil.drawable2Bitmap(resource));
                                                    rootLayBackGround =1;
                                                    rootLayout.setBackground(resource);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        LinearLayout layout = view.findViewById(R.id.float_window_tile_view);
        floatTileView = View.inflate(getContext(), R.layout.window_lay_right, null);

        layout.addView(floatTileView);

        rootLayout = view.findViewById(R.id.window_root_lay);
        final LinearLayout messageLay = floatTileView.findViewById(R.id.window_messgae_lay);
//        final LinearLayout message_content = view.view.findViewById(R.id.message_content);
        imageIcon = floatTileView.findViewById(R.id.window_icon_img);
//        rootLayout
        imageIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_gray,null));
        imageIconLayoutParams = imageIcon.getLayoutParams();


        titleText = floatTileView.findViewById(R.id.window_title_text);
        contentText = floatTileView.findViewById(R.id.window_content_text);
        titleText.setText("调整样式");
        contentText.setText("根据下列选项调整磁贴样式");
        initView();



        TextView rootBackgroundImage = view.findViewById(R.id.root_background_image);
        rootBackgroundImage.setOnClickListener(v -> {
            CheckUtil.verifyStoragePermissions(getActivity());
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

        });


        SeekBar iconWidthHeigtSeekBar = view.findViewById(R.id.icon_widthHeight);
        iconWidthHeigtSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageIconLayoutParams.width=progress;
                imageIconLayoutParams.height =progress;
//                imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageIcon.setLayoutParams(imageIconLayoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                iconWidthHeightValue = seekBar.getProgress();

            }
        });

        SeekBar titleTextSizeSeekBar = view.findViewById(R.id.titleTextSize);
        titleTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                titleText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                titleTextSizeValue = seekBar.getProgress();
            }
        });


        final Spinner icon_type = view.findViewById(R.id.icon_type);

        icon_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position ==0) imageIcon.setDisplayType(RoundImageView.DisplayType.ROUND_RECT);
                if (position ==1) imageIcon.setDisplayType(RoundImageView.DisplayType.CIRCLE);
                if (position ==2) imageIcon.setDisplayType(RoundImageView.DisplayType.NORMAL);
                imageIcon.invalidate();
                iconTypeValue = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        TextView titleTextColor = view.findViewById(R.id.title_text_color);
        titleTextColor.setOnClickListener(v -> ColorPickerDialog.newBuilder().setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(R.id.title_text_color)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(getActivity()));


        TextView contentTextColor = view.findViewById(R.id.content_text_color);
        contentTextColor.setOnClickListener(v -> ColorPickerDialog.newBuilder().setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(R.id.content_text_color)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(getActivity()));

        SeekBar icon_ridas = view.findViewById(R.id.icon_ridas);
        icon_ridas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                imageIcon.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                iconRidusValue = seekBar.getProgress();

            }
        });

        SeekBar contextTextSizeSeekBar = view.findViewById(R.id.contextTextSize);
        contextTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                contentText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                contentTextSizeValue = seekBar.getProgress();

            }
        });

        final SeekBar contextPaddingTopAndBottom = view.findViewById(R.id.contextPaddingTopAndBottom);
        contextPaddingTopAndBottom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    floatTileView.setPadding(progress,progress,3,progress);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rootPaddingTopAndBottomValue = seekBar.getProgress();

            }
        });

//        final SeekBar contextPaddingLeftOrRight = view.findViewById(R.id.contextPaddingLeftOrRight);
//        contextPaddingLeftOrRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (rootPaddingTopAndBottomValue !=-1){
//                    floatTileView.setPadding(0,rootPaddingTopAndBottomValue,rootPaddingLeftOrRightValue,rootPaddingTopAndBottomValue);
//                }else
//                {
//                    floatTileView.setPadding(0,progress,0,progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                rootPaddingLeftOrRightValue = seekBar.getProgress();
//
//            }
//        });

        final SeekBar rootElevation = view.findViewById(R.id.rootElevation);
        rootElevation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                floatTileView.setElevation(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rootElevationValue = seekBar.getProgress();

            }
        });

        view.findViewById(R.id.submit_custom_view).setOnClickListener(v -> {
            if (iconWidthHeightValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","iconWidthHeightValue",iconWidthHeightValue);
            }
            if (iconTypeValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","iconTypeValue",iconTypeValue);
            }
            if (iconRidusValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","iconRidusValue",iconRidusValue);
            }
            if (titleTextSizeValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","titleTextSizeValue",titleTextSizeValue);
            }
            if (contentTextSizeValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","contentTextSizeValue",contentTextSizeValue);
            }
            if (rootPaddingTopAndBottomValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","rootPaddingTopAndBottomValue", rootPaddingTopAndBottomValue);
            }
            if (rootElevationValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","rootElevationValue",iconWidthHeightValue);
            }

            if (titleTextColorValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","titleTextColorValue",titleTextColorValue);
            }
            if (contentTextColorValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","contentTextColorValue",contentTextColorValue);
            }

            if (titleTextColorValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","rootLayRidusValue",rootLayRidusValue);
            }
            if (contentTextColorValue != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","rootLayColorValue",rootLayColorValue);
            }

            if (rootLayBackGround != -1){
                SpUtil.putInt(getContext(),"floatTileCustonView","rootBackGround",rootLayBackGround);
            }

            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("修改成功")
                    .show();
        });

        view.findViewById(R.id.reset_custom_view).setOnClickListener(v -> {
            SpUtil.putInt(getContext(),"floatTileCustonView","iconWidthHeightValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","iconTypeValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","iconRidusValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","titleTextSizeValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","contentTextSizeValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","rootPaddingTopAndBottomValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","rootElevationValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","titleTextColorValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","contentTextColorValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","rootLayRidusValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","rootLayColorValue",-1);
            SpUtil.putInt(getContext(),"floatTileCustonView","rootBackGround",-1);

            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("重置成功")
                    .show();
            initView();
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private  void initView(){
        int iconWidthHeightValue = SpUtil.getInt(getContext(),"floatTileCustonView","iconWidthHeightValue",-1);
        int iconTypeValue = SpUtil.getInt(getContext(),"floatTileCustonView","iconTypeValue",-1);
        int iconRidusValue = SpUtil.getInt(getContext(),"floatTileCustonView","iconRidusValue",-1);
        int titleTextSizeValue = SpUtil.getInt(getContext(),"floatTileCustonView","titleTextSizeValue",-1);
        int contentTextSizeValue = SpUtil.getInt(getContext(),"floatTileCustonView","contentTextSizeValue",-1);
        int rootPaddingTopAndBottomValue = SpUtil.getInt(getContext(),"floatTileCustonView","rootPaddingTopAndBottomValue",-1);
        int rootElevationValue = SpUtil.getInt(getContext(),"floatTileCustonView","rootElevationValue",-1);
        int titleTextColorValue = SpUtil.getInt(getContext(),"floatTileCustonView","titleTextColorValue",-1);
        int contentTextColorValue = SpUtil.getInt(getContext(),"floatTileCustonView","contentTextColorValue",-1);
        int rootLayBackGroundValue = SpUtil.getInt(getContext(),"floatTileCustonView","rootBackGround",-1);

        if (iconWidthHeightValue != -1){
            imageIconLayoutParams.width=iconWidthHeightValue;
            imageIconLayoutParams.height =iconWidthHeightValue;
//                imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageIcon.setLayoutParams(imageIconLayoutParams);
        }
        if (iconTypeValue != -1){
            if (iconTypeValue ==0) imageIcon.setDisplayType(RoundImageView.DisplayType.ROUND_RECT);
            if (iconTypeValue ==1) imageIcon.setDisplayType(RoundImageView.DisplayType.CIRCLE);
            if (iconTypeValue ==2) imageIcon.setDisplayType(RoundImageView.DisplayType.NORMAL);
        }
        if (iconRidusValue != -1){
            imageIcon.setRadius(iconRidusValue);
        }
        if (titleTextSizeValue != -1){
            titleText.setTextSize(titleTextSizeValue);
        }
        if (contentTextSizeValue != -1){
            contentText.setTextSize(contentTextSizeValue);
        }
        if (titleTextColorValue != -1){
            titleText.setTextColor(titleTextColorValue);
        }
        if (contentTextColorValue != -1){
            contentText.setTextColor(contentTextColorValue);
        }

        if (rootPaddingTopAndBottomValue != -1){
            floatTileView.setPadding(rootPaddingTopAndBottomValue,rootPaddingTopAndBottomValue,3,rootPaddingTopAndBottomValue);
        }
        if (rootElevationValue != -1){
            floatTileView.setElevation(rootElevationValue);
        }
        if (rootLayBackGroundValue != -1){
            Drawable drawable = ImageUtil.BitmapToDrawable(ImageUtil.getImageFromData(getContext(),"float_tile_background.9.png"),getContext());
            rootLayout.setBackground(drawable);
        }
    }
    @Override
    public void onColorSelected(int dialogId, int color) {
        if (dialogId == R.id.title_text_color){
            titleTextColorValue = color;
            titleText.setTextColor(color);

        }
        if (dialogId == R.id.content_text_color){
            contentTextColorValue = color;
            contentText.setTextColor(color);

        }

    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
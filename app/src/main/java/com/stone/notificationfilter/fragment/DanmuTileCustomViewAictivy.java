package com.stone.notificationfilter.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cbman.roundimageview.RoundImageView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.stone.notificationfilter.BaseActivity;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.CheckUtil;
import com.stone.notificationfilter.util.ImageUtil;
import com.stone.notificationfilter.util.SpUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DanmuTileCustomViewAictivy extends BaseActivity implements ColorPickerDialogListener {

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
    private  String rootLayBackGround = "-1";

    private LinearLayout rootLayout;
    private TextView titleText;
    private TextView contentText;
    private RoundImageView imageIcon;
    private ViewGroup.LayoutParams imageIconLayoutParams;
    private final int FILE_REQUEST_CODE = 2;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_float_dan_mu_custom_view);

        LinearLayout layout = findViewById(R.id.float_window_tile_view);
        floatTileView = View.inflate(getApplicationContext(), R.layout.window_float_danmu, null);

        layout.addView(floatTileView);

        rootLayout = findViewById(R.id.window_root_lay);
        final LinearLayout messageLay = floatTileView.findViewById(R.id.window_messgae_lay);
//        final LinearLayout message_content = view.view.findViewById(R.id.message_content);
        imageIcon = floatTileView.findViewById(R.id.window_icon_img);
//        rootLayout
        imageIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_gray,null));
        imageIconLayoutParams = imageIcon.getLayoutParams();


        titleText = floatTileView.findViewById(R.id.window_title_text);
        contentText = floatTileView.findViewById(R.id.window_content_text);
        titleText.setText("调整样式：");
        contentText.setText("根据下列选项调整磁贴样式");
        initView();



//        TextView rootBackgroundImage = findViewById(R.id.root_background_image);
//        rootBackgroundImage.setOnClickListener(v -> {
//            CheckUtil.verifyStoragePermissions(this);
//            Intent intent = new Intent(this, FilePickerActivity.class);
//            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
//                    .setCheckPermission(true)
//                    .setShowImages(true)
//                    .setShowFiles(false)
//                    .setShowAudios(false)
//                    .setShowVideos(false)
//                    .setSuffixes("png","jpg","bmp")
//                    .setSingleChoiceMode(true)
//                    .enableImageCapture(false)
//                    .setSkipZeroSizeFiles(true)
//                    .build());
//            startActivityForResult(intent, FILE_REQUEST_CODE);
//
//        });


        SeekBar iconWidthHeigtSeekBar = findViewById(R.id.icon_widthHeight);
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

        SeekBar titleTextSizeSeekBar = findViewById(R.id.titleTextSize);
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


        final Spinner icon_type = findViewById(R.id.icon_type);

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


        TextView titleTextColor = findViewById(R.id.title_text_color);
        titleTextColor.setOnClickListener(v -> ColorPickerDialog.newBuilder().setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(R.id.title_text_color)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this));


        TextView contentTextColor = findViewById(R.id.content_text_color);
        contentTextColor.setOnClickListener(v -> ColorPickerDialog.newBuilder().setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(R.id.content_text_color)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this));

        SeekBar icon_ridas = findViewById(R.id.icon_ridas);
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

        SeekBar contextTextSizeSeekBar = findViewById(R.id.contextTextSize);
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

        final SeekBar contextPaddingTopAndBottom = findViewById(R.id.contextPaddingTopAndBottom);
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

        final SeekBar rootElevation = findViewById(R.id.rootElevation);
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

        findViewById(R.id.submit_custom_view).setOnClickListener(v -> {
            if (iconWidthHeightValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","iconWidthHeightValue",iconWidthHeightValue);
            }
            if (iconTypeValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","iconTypeValue",iconTypeValue);
            }
            if (iconRidusValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","iconRidusValue",iconRidusValue);
            }
            if (titleTextSizeValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","titleTextSizeValue",titleTextSizeValue);
            }
            if (contentTextSizeValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","contentTextSizeValue",contentTextSizeValue);
            }
            if (rootPaddingTopAndBottomValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","rootPaddingTopAndBottomValue", rootPaddingTopAndBottomValue);
            }
            if (rootElevationValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","rootElevationValue",iconWidthHeightValue);
            }

            if (titleTextColorValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","titleTextColorValue",titleTextColorValue);
            }
            if (contentTextColorValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","contentTextColorValue",contentTextColorValue);
            }

            if (titleTextColorValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","rootLayRidusValue",rootLayRidusValue);
            }
            if (contentTextColorValue != -1){
                SpUtil.putInt(getApplicationContext(),"danmuCustonView","rootLayColorValue",rootLayColorValue);
            }

            if (!rootLayBackGround.equals("-1")){
                SpUtil.putString(getApplicationContext(),"danmuCustonView","rootBackGround",rootLayBackGround);
            }

            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("修改成功")
                    .show();
        });

        findViewById(R.id.reset_custom_view).setOnClickListener(v -> {
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","iconWidthHeightValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","iconTypeValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","iconRidusValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","titleTextSizeValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","contentTextSizeValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","rootPaddingTopAndBottomValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","rootElevationValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","titleTextColorValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","contentTextColorValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","rootLayRidusValue",-1);
            SpUtil.putInt(getApplicationContext(),"danmuCustonView","rootLayColorValue",-1);
            SpUtil.putString(getApplicationContext(),"danmuCustonView","rootBackGround","-1");

            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("重置成功")
                    .show();
            initView();
        });
    }

    private  void initView(){
        int iconWidthHeightValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","iconWidthHeightValue",-1);
        int iconTypeValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","iconTypeValue",-1);
        int iconRidusValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","iconRidusValue",-1);
        int titleTextSizeValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","titleTextSizeValue",-1);
        int contentTextSizeValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","contentTextSizeValue",-1);
        int rootPaddingTopAndBottomValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","rootPaddingTopAndBottomValue",-1);
        int rootElevationValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","rootElevationValue",-1);
        int titleTextColorValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","titleTextColorValue",-1);
        int contentTextColorValue = SpUtil.getInt(getApplicationContext(),"danmuCustonView","contentTextColorValue",-1);
        String rootLayBackGroundValue = SpUtil.getString(getApplicationContext(),"danmuCustonView","rootBackGround","-1");

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
        if (!rootLayBackGroundValue.equals("-1")){
            Glide.with(getApplicationContext()).load(new File(getApplicationContext().getFilesDir(),rootLayBackGroundValue))//签到整体 背景
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            rootLayout.setBackground(resource);
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }        //设置宽高
                    });
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
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

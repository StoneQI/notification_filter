package com.stone.notificationfilter.fragment;

import android.graphics.Color;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.cbman.roundimageview.RoundImageView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.stone.notificationfilter.BaseActivity;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.actioner.floatmessagereply.FloatMessageReply;
import com.stone.notificationfilter.util.SpUtil;
import com.tencent.bugly.crashreport.CrashReport;

//import me.codego.view.RoundLayout;

public class FloatTileCustomViewActivity extends BaseActivity implements ColorPickerDialogListener{
    private View view;

    private int iconWidthHeightValue=-1;
    private int iconTypeValue=-1;
    private  int iconRidusValue=-1;
    private int titleTextSizeValue=-1;
    private int contentTextSizeValue=-1;
    private int rootPaddingValue=-1;
    private  int rootElevationValue=-1;
    private int titleTextColorValue=-1;
    private int contentTextColorValue=-1;

    private int rootLayRidusValue=-1;
    private int rootLayColorValue=-1;

//    private RoundLayout rootLayout;
    private TextView titleText;
    private TextView contentText;
    private RoundImageView imageIcon;
    private ViewGroup.LayoutParams imageIconLayoutParams;

    public FloatTileCustomViewActivity() {
    }






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.float_window_tile_style_custom);

//        CrashReport.testJavaCrash();

        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("磁贴样式自定义");


        LinearLayout layout = findViewById(R.id.float_window_tile_view);
        view = View.inflate(getApplicationContext(), R.layout.window_lay_right, null);

        layout.addView(view);

//        rootLayout = view.findViewById(R.id.window_root_lay);
        final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
//        final LinearLayout message_content = view.findViewById(R.id.message_content);
        imageIcon = view.findViewById(R.id.window_icon_img);
//        rootLayout
        imageIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_gray,null));
        imageIconLayoutParams = imageIcon.getLayoutParams();


        titleText = view.findViewById(R.id.window_title_text);
        contentText = view.findViewById(R.id.window_content_text);
        titleText.setText("调整样式");
        contentText.setText("根据下列选项调整磁贴样式");
        initView();

        SeekBar iconWIdthHeigtSeekBar = findViewById(R.id.icon_widthHeight);
        iconWIdthHeigtSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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


        final Spinner icon_type = (Spinner) findViewById(R.id.icon_type);

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
        titleTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder().setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .setDialogId(R.id.title_text_color)
                        .setColor(Color.BLACK)
                        .setShowAlphaSlider(true)
                        .show(FloatTileCustomViewActivity.this);
            }
        });


        TextView contentTextColor = findViewById(R.id.content_text_color);
        contentTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder().setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                        .setAllowPresets(false)
                        .setDialogId(R.id.content_text_color)
                        .setColor(Color.BLACK)
                        .setShowAlphaSlider(true)
                        .show(FloatTileCustomViewActivity.this);

            }
        });

////        TextView rootLayColor = findViewById(R.id.root_lay_color);
//        rootLayColor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ColorPickerDialog.newBuilder().setDialogType(ColorPickerDialog.TYPE_CUSTOM)
//                        .setAllowPresets(false)
//                        .setDialogId(R.id.root_lay_color)
//                        .setColor(Color.BLACK)
//                        .setShowAlphaSlider(true)
//                        .show(FloatTileCustomViewActivity.this);
//
//            }
//        });

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

        final SeekBar contextPaddingSeekBar = findViewById(R.id.contextPadding);
        contextPaddingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                view.setPadding(progress,progress,progress,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rootPaddingValue = seekBar.getProgress();

            }
        });

        final SeekBar rootElevation = findViewById(R.id.rootElevation);
        rootElevation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                view.setElevation(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rootElevationValue = seekBar.getProgress();

            }
        });

        findViewById(R.id.submit_custom_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iconWidthHeightValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","iconWidthHeightValue",iconWidthHeightValue);
                }
                if (iconTypeValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","iconTypeValue",iconTypeValue);
                }
                if (iconRidusValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","iconRidusValue",iconRidusValue);
                }
                if (titleTextSizeValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","titleTextSizeValue",titleTextSizeValue);
                }
                if (contentTextSizeValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","contentTextSizeValue",contentTextSizeValue);
                }
                if (rootPaddingValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","rootPaddingValue",rootPaddingValue);
                }
                if (rootElevationValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","rootElevationValue",iconWidthHeightValue);
                }

                if (titleTextColorValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","titleTextColorValue",titleTextColorValue);
                }
                if (contentTextColorValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","contentTextColorValue",contentTextColorValue);
                }

                if (titleTextColorValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","rootLayRidusValue",rootLayRidusValue);
                }
                if (contentTextColorValue != -1){
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","rootLayColorValue",rootLayColorValue);
                }

                new AlertDialog.Builder(FloatTileCustomViewActivity.this)
                        .setTitle("提示")
                        .setMessage("修改成功")
                        .show();
            }
        });

        findViewById(R.id.reset_custom_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","iconWidthHeightValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","iconTypeValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","iconRidusValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","titleTextSizeValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","contentTextSizeValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","rootPaddingValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","rootElevationValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","titleTextColorValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","contentTextColorValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","rootLayRidusValue",-1);
                    SpUtil.putInt(getApplicationContext(),"floatTileCustonView","rootLayColorValue",-1);


                new AlertDialog.Builder(FloatTileCustomViewActivity.this)
                        .setTitle("提示")
                        .setMessage("重置成功")
                        .show();
            }
        });






    }


    private  void initView(){
        int iconWidthHeightValue = SpUtil.getInt(this,"floatTileCustonView","iconWidthHeightValue",-1);
        int iconTypeValue = SpUtil.getInt(this,"floatTileCustonView","iconTypeValue",-1);
        int iconRidusValue = SpUtil.getInt(this,"floatTileCustonView","iconRidusValue",-1);
        int titleTextSizeValue = SpUtil.getInt(this,"floatTileCustonView","titleTextSizeValue",-1);
        int contentTextSizeValue = SpUtil.getInt(this,"floatTileCustonView","contentTextSizeValue",-1);
        int rootPaddingValue = SpUtil.getInt(this,"floatTileCustonView","rootPaddingValue",-1);
        int rootElevationValue = SpUtil.getInt(this,"floatTileCustonView","rootElevationValue",-1);

        int titleTextColorValue = SpUtil.getInt(this,"floatTileCustonView","titleTextColorValue",-1);
        int contentTextColorValue = SpUtil.getInt(this,"floatTileCustonView","contentTextColorValue",-1);

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

        if (rootPaddingValue != -1){
            view.setPadding(rootPaddingValue,rootPaddingValue,rootPaddingValue,rootPaddingValue);
        }
        if (rootElevationValue != -1){
            view.setElevation(rootElevationValue);
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

//        if (dialogId == R.id.root_lay_color){
//            rootLayColorValue = color;
//            rootLayout.setBackgroundColor(color);
//        }

    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }


}

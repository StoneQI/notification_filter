package com.stone.notificationfilter.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cbman.roundimageview.RoundImageView;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.ToolUtils;

import java.util.Arrays;
import java.util.List;

public class FloatTileCustomView extends AppCompatActivity {
    private LinearLayout layout;
    private View view;

    private int iconWidthHeightValue=-1;
    private int iconTypeValue=-1;
    private  int iconRidusValue=-1;
    private int titleTextSizeValue=-1;
    private int contentTextSizeValue=-1;
    private int rootPaddingValue=-1;
    private  int rootElevationValue=-1;

    @SuppressLint("ResourceAsColor")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.float_window_tile_style_custom);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("磁贴样式自定义");

        layout= findViewById(R.id.float_window_tile_view);
        view = View.inflate(getApplicationContext(), R.layout.window_lay_right, null);

        layout.addView(view);
        final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
//        final LinearLayout message_content = view.findViewById(R.id.message_content);
        final RoundImageView imageIcon = view.findViewById(R.id.window_icon_img);

        imageIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_gray,null));
        final ViewGroup.LayoutParams imageIconLayoutParams = imageIcon.getLayoutParams();


        final TextView titleText = view.findViewById(R.id.window_title_text);
        final TextView contentText = view.findViewById(R.id.window_content_text);
        titleText.setText("调整样式");
        contentText.setText("根据下列选项调整磁贴样式");

//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        layoutParams.width = (int) ToolUtils.dp2Px(200,getApplicationContext());
//        messageLay.setLayoutParams(layoutParams);
//        titleText.setTextSize(18);
//        imageIconLayoutParams.width=80;
//        imageIconLayoutParams.height =80;
//        imageIcon.setBackgroundColor(R.color.greyColor);

//        imageIcon.setDisplayType();
//        imageIcon.setRadius();

//        imageIcon.setLayoutParams(imageIconLayoutParams);
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

                new AlertDialog.Builder(FloatTileCustomView.this)
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

                new AlertDialog.Builder(FloatTileCustomView.this)
                        .setTitle("提示")
                        .setMessage("重置成功")
                        .show();
            }
        });






    }
}

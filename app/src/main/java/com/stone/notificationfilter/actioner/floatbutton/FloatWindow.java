package com.stone.notificationfilter.actioner.floatbutton;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stone.notificationfilter.R;

/**
 * Created by wengyiming on 2017/9/13.
 */

public class FloatWindow extends BaseFloatDailog {
    private FrameLayout floatCustomViewLeft;
    private FrameLayout floatCustomViewRight;
    FrameLayout.LayoutParams layoutParams;
    private View view;
    public interface IOnItemClicked{
        void onBackItemClick();//返回键按下
        void onCloseItemClick();//关闭键按下
        void onClose();//对话框折叠
        void onExpand();//对话框展开
    }
    IOnItemClicked itemClickedListener;

    /**
     * 构造函数
     * @param context 上下文
     * @param location 悬浮窗停靠位置，0为左边，1为右边
     * @param callBack 点击按钮的回调
     */
    public FloatWindow(Context context, int location, int defaultY,IOnItemClicked callBack) {
        super(context,location,defaultY);
        this.itemClickedListener=callBack;
    }
    public FloatWindow(Context context, int defaultY,IOnItemClicked callBack) {
        super(context,defaultY);
        this.itemClickedListener=callBack;
    }

    @Override
    protected void updateViewBeforeCloseMenu(int mHintLocation) {

    }

    @Override
    protected void updateViewBeforeOpenMenu(int mHintLocation) {
        if(mHintLocation ==RIGHT){
            floatCustomViewRight.removeAllViews();
            floatCustomViewLeft.removeAllViews();
            floatCustomViewRight.addView(view,layoutParams);
        }else{
            floatCustomViewLeft.removeAllViews();
            floatCustomViewRight.removeAllViews();
            floatCustomViewLeft.addView(view,layoutParams);
        }
    }

    @Override
    protected View getLeftView(LayoutInflater inflater, View.OnTouchListener touchListener) {
        View view = inflater.inflate(R.layout.widget_float_window_left, null);
        floatCustomViewLeft=(FrameLayout)view.findViewById(R.id.float_custom_view_left);
        floatCustomViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickedListener.onBackItemClick();
            }
        });
        ImageView closeItem=(ImageView)view.findViewById(R.id.close_item);
        closeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickedListener.onCloseItemClick();
            }
        });
        return view;
    }

    @Override
    protected View getRightView(LayoutInflater inflater, View.OnTouchListener touchListener) {
        View view = inflater.inflate(R.layout.widget_float_window_right, null);
        floatCustomViewRight=(FrameLayout)view.findViewById(R.id.float_custom_view_right);
        floatCustomViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickedListener.onBackItemClick();
            }
        });
        ImageView closeItem=(ImageView)view.findViewById(R.id.close_item);
        closeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickedListener.onCloseItemClick();
            }
        });
        return view;
    }

    @Override
    protected View getLogoView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.widget_float_window_logo, null);
    }

    @Override
    protected void resetLogoViewSize(int hintLocation, View logoView) {
        logoView.clearAnimation();
        logoView.setTranslationX(0);
        logoView.setScaleX(1);
        logoView.setScaleY(1);
    }

    @Override
    protected void dragingLogoViewOffset(View logoView, boolean isDraging, boolean isResetPosition, float offset) {
        if (isDraging && offset > 0) {
            logoView.setBackgroundDrawable(null);
//            logoView.setScaleX(1 + offset);
//            logoView.setScaleY(1 + offset);
        } else {
            logoView.setBackgroundResource(R.drawable.widget_float_button_logo_bg);
            logoView.setTranslationX(0);
            logoView.setScaleX(1);
            logoView.setScaleY(1);
        }


        if (isResetPosition) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                logoView.setRotation(offset * 360);
            }
        } else {
            logoView.setRotation(offset * 360);
        }
    }

    @Override
    public void shrinkLeftLogoView(View smallView) {
        smallView.setTranslationX(-smallView.getWidth() / 3);
    }

    @Override
    public void shrinkRightLogoView(View smallView) {
        smallView.setTranslationX(smallView.getWidth() / 3);
    }

    @Override
    public void leftViewOpened(View leftView) {
        this.itemClickedListener.onExpand();
    }

    @Override
    public void rightViewOpened(View rightView) {
        this.itemClickedListener.onExpand();
    }

    @Override
    public void leftOrRightViewClosed(View smallView) {
        this.itemClickedListener.onClose();
    }

    @Override
    protected void onDestoryed() {
        if(isApplictionDialog()){
            if(getContext() instanceof Activity){
                dismiss();
            }
        }
    }

    public void setContentView(View view){
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = (int)(mScreenWidth*0.8);

        this.view = view;

    }
    public void show() {
        super.show();
    }
}

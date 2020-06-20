package com.stone.notificationfilter.actioner;

import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cbman.roundimageview.RoundImageView;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.actioner.floatmessagereply.FloatMessageReply;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.ToolUtils;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.stone.notificationfilter.util.ToolUtils.startFreeformHack;


public class StautsBarFloatActioner {
    private static String TAG ="DanMuActioner";
    private NotificationInfo notificationInfo;
    private Context context;
    private PendingIntent intent = null;


    private int viewWidth;
    private int viewHeight;
    private int mStatusBarHeight;
    private int mScreenWidth;
    private int mMoveSpeed =8;
    private int mResetUpdateX =0;
    private static int mPrePosition =-1;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public boolean isEditPos=false;
    private boolean isOpen=true;
    public boolean isRemove;

    public  int showID = -1;
    private View view;
    private Timer mtimer =null;
    private  boolean isLeft=true;
    private  boolean isVert =false;
    private  boolean isRefuse =false;

    private Interpolator mLinearInterpolator = new LinearInterpolator();
    ValueAnimator valueAnimator = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    public StautsBarFloatActioner(NotificationInfo notificationInfo, Context context) {
        this.context = context;
        this.notificationInfo = notificationInfo;



        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getScreenSize();
        ViewInit();
        WindowInit(context);
    }




    private void  ViewInit(){
//        setSceneOrientation(context);

        view = View.inflate(context, R.layout.window_float_statusbar, null);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width =mScreenWidth;
        layoutParams.height = mStatusBarHeight;
        view.setLayoutParams(layoutParams);


        RoundImageView imageView = view.findViewById(R.id.window_icon_img);
        if (this.notificationInfo.largeIcon ==null){
            imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.packageName));
        } else {
            imageView.setImageIcon(this.notificationInfo.largeIcon);
        }

        final ViewGroup.LayoutParams imageIconLayoutParams = imageView.getLayoutParams();
        imageIconLayoutParams.width=mStatusBarHeight-7;
        imageIconLayoutParams.height =mStatusBarHeight-7;
//                imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(imageIconLayoutParams);

        final TextView contentText = view.findViewById(R.id.window_content_text);
        contentText.setText(this.notificationInfo.title+":"+this.notificationInfo.content);

//        if(this.notificationInfo.title.length() >18 || this.notificationInfo.content.length() >18){
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.width = (int) ToolUtils.dp2Px(250,context);
//            messageLay.setLayoutParams(layoutParams);
//        }



    }

    private void WindowInit(Context context) {

        if (!notificationInfo.isClearable){
            isRefuse =true;
            return;
        }
        if (notificationInfo.content == null && notificationInfo.title==null) {
            isRefuse = true;
            return;
        }
        layoutParams.gravity = Gravity.TOP|Gravity.START;

        intent = this.notificationInfo.intent;
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        viewWidth = view.getMeasuredWidth();
        viewHeight = view.getMeasuredHeight();

        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.format = PixelFormat.RGBA_8888;

    }

    private void getScreenSize() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        mScreenWidth = point.x;
        mStatusBarHeight = getStatusBarHeight(context);
        //        mCurrentIconAlpha = 70 / 100f;
    }

    public int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
    public void run(){
        if (isRefuse){
            return;
        }
//        Log.e(TAG, "mScreenWidth : "+mScreenWidth);
//        Log.e(TAG, "mScreenHeight : "+mScreenHeight);
//        Log.e(TAG, "viewWidth : "+viewWidth);
//        Log.e(TAG, "viewHeight : "+viewHeight);

        isEditPos = false;
//        setOnTouchListenr();
        addViewToWindow();

    }
    public void addViewToWindow() {

        layoutParams.y =0;
        layoutParams.x = 0;

//        Animation translateAnimation = new TranslateAnimation( mScreenWidth,-viewWidth,layoutParams.y,layoutParams.y);
//        // 步骤2：创建平移动画的对象：平移动画对应的Animation子类为TranslateAnimation
//        // 参数分别是：
//        // 1. fromXDelta ：视图在水平方向x 移动的起始值
//        // 2. toXDelta ：视图在水平方向x 移动的结束值
//        // 3. fromYDelta ：视图在竖直方向y 移动的起始值
//        // 4. toYDelta：视图在竖直方向y 移动的结束值
//
//        translateAnimation.setDuration(5000);
//        // 固定属性的设置都是在其属性前加“set”，如setDuration（）
//
//        view.startAnimation(translateAnimation);

        view.setLayerType(View.LAYER_TYPE_HARDWARE,null);
//        layoutParams.layoutAnimationParameters = controller;
//        w



        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {

                    windowManager.addView(view, layoutParams);

                    TimerTask timerTask = new TimerTask(){
                        @Override
                        public void run() {
                            if (isOpen){
                                removeTile();
                            }
                        }
                    };
                    mtimer =new Timer();
                    mtimer.schedule(timerTask,6000);
                } catch (WindowManager.BadTokenException e) {
                    // 无悬浮窗权限
                    e.printStackTrace();
                }
            }
        });


//        TileObject.waitingForShowingTileList.add(this);
    }

    private void hideStausbar(boolean enable) {
//        if (enable) {
//            WindowManager.LayoutParams attrs = getWindow().getAttributes();
//            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            getWindow().setAttributes(attrs);
//        } else {
//            WindowManager.LayoutParams attrs = getWindow().getAttributes();
//            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            getWindow().setAttributes(attrs);
//        }
    }


    /**
     * 设置位置
     */


    public void removeTile() {
        // It's time to show some Floating Tile that waiting for showing.
        Log.e(TAG,"IS Remove");
        if (isRemove) {
            return;
        }
        if(mtimer !=null){
            mtimer.cancel();
        }
        isRemove = true;
        removeView();
//        TileObject.removeSingleShowingTile(this);

    }


    public void removeView() {
        windowManager.removeView(view);
    }


}

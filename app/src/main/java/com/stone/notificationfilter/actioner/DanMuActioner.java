package com.stone.notificationfilter.actioner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cbman.roundimageview.RoundImageView;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.actioner.floatmessagereply.FloatMessageReply;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.ImageUtil;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.ToolUtils;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.stone.notificationfilter.util.ToolUtils.startFreeformHack;

public class DanMuActioner {
    private static String TAG ="DanMuActioner";
    private NotificationInfo notificationInfo;
    private Context context;
    private PendingIntent intent = null;


    private int viewWidth;
    private int viewHeight;
    private int mScreenHeight;
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


    public DanMuActioner(NotificationInfo notificationInfo, Context context) {
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

        view = View.inflate(context, R.layout.window_float_danmu, null);

//        RelativeLayout relativeLayout = new RelativeLayout(null);
//        RelativeLayout.LayoutParams = new
//        relativeLayout.addView(view);



//        layoutParams.windowAnimations =R.style.PopupFloatTiltAnimLeft;

        LinearLayout linearLayout = view.findViewById(R.id.window_root_lay);
        final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
//        final LinearLayout message_content = view.findViewById(R.id.message_content);
        RoundImageView imageView = view.findViewById(R.id.window_icon_img);
        if (this.notificationInfo.largeIcon ==null){
            imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.packageName));
        } else {
            imageView.setImageIcon(this.notificationInfo.largeIcon);
        }

        final TextView titleText = view.findViewById(R.id.window_title_text);
        final TextView contentText = view.findViewById(R.id.window_content_text);
        titleText.setText(this.notificationInfo.title+"：");
        contentText.setText(this.notificationInfo.content);

        if(this.notificationInfo.title.length() >18 || this.notificationInfo.content.length() >18){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.width = (int) ToolUtils.dp2Px(250,context);
            messageLay.setLayoutParams(layoutParams);
        }
        final ViewGroup.LayoutParams imageIconLayoutParams = imageView.getLayoutParams();
        int iconWidthHeightValue = SpUtil.getInt(context,"danmuCustonView","iconWidthHeightValue",-1);
        int iconTypeValue = SpUtil.getInt(context,"danmuCustonView","iconTypeValue",-1);
        int iconRidusValue = SpUtil.getInt(context,"danmuCustonView","iconRidusValue",-1);
        int titleTextSizeValue = SpUtil.getInt(context,"danmuCustonView","titleTextSizeValue",-1);
        int contentTextSizeValue = SpUtil.getInt(context,"danmuCustonView","contentTextSizeValue",-1);
        int rootPaddingTopAndBottomValue = SpUtil.getInt(context,"danmuCustonView","rootPaddingTopAndBottomValue",-1);
        int rootElevationValue = SpUtil.getInt(context,"danmuCustonView","rootElevationValue",-1);

        int titleTextColorValue = SpUtil.getInt(context,"danmuCustonView","titleTextColorValue",-1);
        int contentTextColorValue = SpUtil.getInt(context,"danmuCustonView","contentTextColorValue",-1);
        String rootLayBackGroundValue = SpUtil.getString(context,"danmuCustonView","rootBackGround","-1");


        if (iconWidthHeightValue != -1){
            imageIconLayoutParams.width=iconWidthHeightValue;
            imageIconLayoutParams.height =iconWidthHeightValue;
//                imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(imageIconLayoutParams);
        }
        if (iconTypeValue != -1){
            if (iconTypeValue ==0) imageView.setDisplayType(RoundImageView.DisplayType.ROUND_RECT);
            if (iconTypeValue ==1) imageView.setDisplayType(RoundImageView.DisplayType.CIRCLE);
            if (iconTypeValue ==2) imageView.setDisplayType(RoundImageView.DisplayType.NORMAL);
        }
        if (iconRidusValue != -1){
            imageView.setRadius(iconRidusValue);
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
            if (isLeft){
                view.setPadding(3,rootPaddingTopAndBottomValue,rootPaddingTopAndBottomValue,rootPaddingTopAndBottomValue);
            }else{
                view.setPadding(rootPaddingTopAndBottomValue,rootPaddingTopAndBottomValue,3,rootPaddingTopAndBottomValue);
            }

        }
        if (rootElevationValue != -1){
            view.setElevation(rootElevationValue);
        }

        if (!rootLayBackGroundValue.equals("-1")){
            if (isLeft) {

                Bitmap bitmapBackGround = ImageUtil.getImageFromData(context, rootLayBackGroundValue);
                Bitmap modBm = Bitmap.createBitmap(bitmapBackGround.getWidth(),bitmapBackGround.getHeight(),bitmapBackGround.getConfig());

                Canvas canvas = new Canvas(modBm);
                Paint paint = new Paint();
                Matrix matrix = new Matrix();

                matrix.setScale(-1,1);//翻转
                matrix.postTranslate(bitmapBackGround.getWidth(),0);

                canvas.drawBitmap(bitmapBackGround,matrix,paint);
                linearLayout.setBackground(ImageUtil.BitmapToDrawable(modBm,context));


            }else{
                Drawable drawable = ImageUtil.BitmapToDrawable(ImageUtil.getImageFromData(context, rootLayBackGroundValue), context);
                linearLayout.setBackground(drawable);
            }
        }




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
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.format = PixelFormat.RGBA_8888;

    }

    private void getScreenSize() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        mScreenWidth = point.x;
        mScreenHeight = point.y;
        //        mCurrentIconAlpha = 70 / 100f;
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
        Random random = new Random();
        layoutParams.y = random.nextInt(4)*(viewHeight+9)+60;
        layoutParams.x = mScreenWidth;

        layoutParams.windowAnimations = R.style.DanMuMoveTiltAnim;

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
                            mtimer.schedule(timerTask,10000);
//                            valueAnimator = ValueAnimator.ofInt(0,mScreenWidth+viewWidth);
////                            valueAnimator.setInterpolator(TimeInterpolator);
//                            valueAnimator.setDuration(8000);
////                            valueAnimator.setPersistentDrawingCache（PERSISTENT_ANIMATION_CACHE）
//                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                                @Override
//                                public void onAnimationUpdate(ValueAnimator animation) {
//                                    mResetUpdateX = (int) animation.getAnimatedValue();
//                                    layoutParams.x = mScreenWidth-mResetUpdateX;
//                                    if (layoutParams.x == -viewWidth){
//                                        removeTile();
//                                        valueAnimator.cancel();
//                                        return;
//                                    }
//                                    windowManager.updateViewLayout(view,layoutParams);
//
////                                    mHandler.post(updatePositionRunnable);
//
//                                }
//                            });
//                            valueAnimator.addListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    mHandler.post(removeViewRunnable);
//                                    super.onAnimationEnd(animation);
//                                }
//                            });
//                            valueAnimator.start();
                        } catch (WindowManager.BadTokenException e) {
                            // 无悬浮窗权限
                            e.printStackTrace();
                        }
                    }
                    });


//        TileObject.waitingForShowingTileList.add(this);
    }

    private Runnable updatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            updateView();
        }
    };

    private Runnable removeViewRunnable = new Runnable() {
        @Override
        public void run() {
            removeTile();
        }
    };

    private void updateView(){
//        Log.e(TAG, "mResetUpdateX : "+mResetUpdateX);
//        Log.e(TAG, "x : "+String.valueOf(layoutParams.x));

            try {
                windowManager.updateViewLayout(view,layoutParams);
            }catch (Exception e){
                e.printStackTrace();
            }


    }


    private void setOnTouchListenr() {

        final GestureDetector gestureDetector= new GestureDetector(this.view.getContext(), new GestureDetector.OnGestureListener(){
            protected static final float FLIP_DISTANCE = 50;

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }


            @Override
            public void onShowPress(MotionEvent e) {
                return;
            }


            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (isOpen){
                    try {
                        if(intent !=null){
                            intent.send();
                            removeTile();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }else {
                    showNoificationInfo();
                }
                Log.i(TAG, "点击...");
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if(SpUtil.getBoolean(context,"appSettings","message_replay",false)){
                    Log.e(TAG,"小窗打开");
                    if (SpUtil.getBoolean(context, "appSettings","freeform_mode_switch", true)){
                        openActivity(notificationInfo.packageName);
                    }

                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i(TAG, velocityX+","+velocityY);


                if (e2.getX() - e1.getX() > 110) {
                    if(isLeft){
                        if (isOpen){
                            TileObject.clearShowingTile();
                        }
                    }else {
                        closeNoificationInfo();
                    }
                    return true;
                }
                if (e1.getX() - e2.getX() > 110) {
                    if(!isLeft){
                        if (isOpen){
                            TileObject.clearShowingTile();
                        }
                    }else {
                        closeNoificationInfo();
                    }

                    Log.i(TAG, "向左滑...");
                    Log.i(TAG, String.valueOf(mScreenHeight));
                    return true;
                }
                if (e1.getY() - e2.getY() > 30) {
                    if(isOpen){
                        removeTile();
                    }
                    Log.i(TAG, "向下滑...");
                    return true;
                }
                if (e2.getY() - e1.getY() > 30) {
                    if(isOpen){
                        removeTile();
                        if(SpUtil.getBoolean(context,"appSettings","message_replay",false))
                            if (notificationInfo.packageName.contains("com.tencent.mm")){
                                //快捷回复
                                Log.e(TAG,"快捷回复");
                                if (ToolUtils.checkAppInstalled(context,"com.google.android.projection.gearhead")){
                                    FloatMessageReply floatMessageReply = new FloatMessageReply(notificationInfo,context);
                                    floatMessageReply.run();
                                }
                            }
                    }
                    Log.i(TAG, "向上滑...");
                    return true;
                }




                Log.d("TAG", e2.getX() + " " + e2.getY());

                return false;
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

    }

    public void openActivity(String appName ) {


        Intent intent =  context.getPackageManager().getLaunchIntentForPackage(appName);
//            intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
        startFreeformHack(context,intent,mScreenWidth,mScreenHeight);
//            context.startActivity(intent);

    }




    private void closeNoificationInfo() {
        view.findViewById(R.id.window_messgae_lay).setVisibility(View.GONE);
        isOpen =false;
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        windowManager.updateViewLayout(view, layoutParams);
        Log.i(TAG, "向右滑...");
        if(mtimer != null)
        {
            mtimer.cancel();
        }
    }

    private void showNoificationInfo() {
        view.findViewById(R.id.window_messgae_lay).setVisibility(View.VISIBLE);
        isOpen = true;
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        windowManager.updateViewLayout(view, layoutParams);
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

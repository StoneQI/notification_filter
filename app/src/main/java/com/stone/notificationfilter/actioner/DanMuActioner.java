package com.stone.notificationfilter.actioner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.view.animation.PathInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;
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

    private static int preLineInt;
    private PropertyValuesHolder animatorX;


    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private boolean isOpen=true;
    public boolean isRemove;


    private View view;
    private  LinearLayout rootContentLayout;

    private Timer mtimer =null;
    private  boolean isLeft=true;
    private  boolean isRefuse =false;
    private  boolean isDanmuClicked = false;


    private Handler mHandler = new Handler(Looper.getMainLooper());


    public DanMuActioner(NotificationInfo notificationInfo, Context context) {
        this.context = context;
        this.notificationInfo = notificationInfo;


        isDanmuClicked = SpUtil.getBoolean(context,"appSettings","is_danmu_clicked",false);
        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getScreenSize();
        ViewInit();
        WindowInit(context);
    }


    private void  ViewInit(){
//        setSceneOrientation(context);

        view = View.inflate(context, R.layout.window_float_danmu, null);

        rootContentLayout = view.findViewById(R.id.window_root_lay);
        final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
//        final LinearLayout message_content = view.findViewById(R.id.message_content);
        RoundImageView imageView = view.findViewById(R.id.window_icon_img);
        if (this.notificationInfo.largeIcon ==null){
            imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.packageName));
        } else {
            imageView.setImageIcon(this.notificationInfo.largeIcon);
        }

        if (isDanmuClicked){
            rootContentLayout.setOnClickListener(v -> {
                try {
                    if (intent != null)
                        intent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                removeTile();
            });
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
                rootContentLayout.setBackground(ImageUtil.BitmapToDrawable(modBm,context));


            }else{
                Drawable drawable = ImageUtil.BitmapToDrawable(ImageUtil.getImageFromData(context, rootLayBackGroundValue), context);
                rootContentLayout.setBackground(drawable);
            }
        }




    }

    public int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
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

        layoutParams.width = mScreenWidth;
        layoutParams.height = viewHeight;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;;
        }
        if (isDanmuClicked){
            layoutParams.flags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        }else {
            layoutParams.flags =WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED|  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }
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
//
//        if (isDanmuClicked){
//            new Handler(Looper.getMainLooper()).post(this::setOnTouchListenr);
//        }
        addViewToWindow();

    }
    public void addViewToWindow() {
        Random random = new Random();

        layoutParams.y = random.nextInt(3)*(viewHeight+13)+getStatusBarHeight(context);
        layoutParams.x = 0;
//        layoutParams.windowAnimations = R.style.DanMuMoveTiltAnim;


        view.bringToFront();
//        layoutParams.layoutAnimationParameters = controller;
//        w





        mHandler.post(() -> {
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

//                PropertyValuesHolder.ofFloat("translationX",(float)mScreenWidth,-(float)viewWidth)

                animatorX = PropertyValuesHolder.ofFloat("translationX",(float)mScreenWidth,-(float)viewWidth);
                ObjectAnimator.ofPropertyValuesHolder(view, animatorX).setDuration(10000).start();
//                objectAnimator.setDuration(10000);
//                objectAnimator.setInterpolator(new LinearInterpolator());
//                objectAnimator.start();
//                objectAnimator.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        view.setLayerType(View.LAYER_TYPE_NONE, null);
//                        super.onAnimationEnd(animation);
//
//                    }
//
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        view.setLayerType(View.LAYER_TYPE_HARDWARE,null);
//                        super.onAnimationStart(animation);
//                    }
//                });


                mtimer =new Timer();
                mtimer.schedule(timerTask,10000);
            } catch (WindowManager.BadTokenException e) {
                // 无悬浮窗权限
                e.printStackTrace();
            }
        });

    }

//    private Runnable updatePositionRunnable = new Runnable() {
//        @Override
//        public void run() {
//            updateView();
//        }
//    };
//
//    private Runnable removeViewRunnable = new Runnable() {
//        @Override
//        public void run() {
//            removeTile();
//        }
//    };
//
//    private void updateView(){
//            try {
//                windowManager.updateViewLayout(view,layoutParams);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//
//    }


    private void setOnTouchListenr() {

        final GestureDetector gestureDetector= new GestureDetector(this.rootContentLayout.getContext(), new GestureDetector.OnGestureListener(){
            protected static final float FLIP_DISTANCE = 50;
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }


            @Override
            public void onShowPress(MotionEvent e) {
            }


            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                    try {
                        if(intent !=null){
                            intent.send();
                            removeTile();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                Log.i(TAG, "点击...");
                return false;
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
                if (e1 == null || e2 == null){
                    return true;
                }

//                if (e2.getX() - e1.getX() > 110) {
//                    if(isLeft){
//                        if (isOpen){
//                            TileObject.clearShowingTile();
//                        }
//                    }else {
//                        closeNoificationInfo();
//                    }
//                    return true;
//                }
//                if (e1.getX() - e2.getX() > 110) {
//                    if(!isLeft){
//                        if (isOpen){
//                            TileObject.clearShowingTile();
//                        }
//                    }else {
//                        closeNoificationInfo();
//                    }
//
//                    Log.i(TAG, "向左滑...");
//                    Log.i(TAG, String.valueOf(mScreenHeight));
//                    return true;
//                }
                if (e1.getY() - e2.getY() > 30) {
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
        view.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });

    }

    public void openActivity(String appName ) {


        Intent intent =  context.getPackageManager().getLaunchIntentForPackage(appName);
//            intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent== null)
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

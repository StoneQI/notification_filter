package com.stone.notificationfilter.actioner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cbman.roundimageview.RoundImageView;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.ToolUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Create by LingC on 2019/8/4 22:11
 */
public class FloatingTileActioner {
    private static String TAG ="FloatingTile";
    private NotificationInfo notificationInfo;
    private Context context;
    private PendingIntent intent = null;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public boolean isEditPos=false;
    private boolean isOpen=true;
    public boolean isRemove;
    private int viewWidth;
    private int viewHeight;
    private int mScreenHeight;
    private int mScreenWidth;
    public  int showID = -1;
    private View view;
    private Timer mtimer =null;
    private  boolean isLeft=true;
    private  boolean isVert =false;
    private  boolean isRefuse =false;

//    class FloatingNotificationItem{
//        protected View view;
//        protected Timer mtimer =null;
//        protected int viewWidth;
//        protected int viewHeight;
//        protected boolean isShow =false;
//        protected boolean isOpen = true;
//        protected PendingIntent intent =null;
//        protected NotificationInfo notificationInfo =null;
//
//        FloatingNotificationItem(Context context, NotificationInfo notificationInfo){
//            final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
//            ImageView imageView = view.findViewById(R.id.window_icon_img);
//            if (this.notificationInfo.getLargeIcon() ==null){
//                imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.getPackageName()));
//            } else {
//                imageView.setImageIcon(this.notificationInfo.getLargeIcon());
//            }
//
//            final TextView titleText = view.findViewById(R.id.window_title_text);
//            final TextView contentText = view.findViewById(R.id.window_content_text);
//            titleText.setText(this.notificationInfo.getTitle());
//            contentText.setText(this.notificationInfo.getContent());
//
//            intent = this.notificationInfo.getIntent();
//            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//            view.measure(width, height);
//            viewWidth = view.getMeasuredWidth();
//            viewHeight = view.getMeasuredHeight();
//        }
//    }

    public FloatingTileActioner(NotificationInfo notificationInfo, Context context, boolean isEditPos) {
        this.context = context;
        this.isEditPos =isEditPos;
        this.notificationInfo = notificationInfo;
        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        viewInit(context);
    }

    private void setSceneOrientation(Context context){
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        int floattitle_x=0;
        getScreenSize();
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            isVert =false;
            floattitle_x = SpUtil.getInt(context,"appSettings","floattitle_landscape_x",mScreenHeight);
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            floattitle_x = SpUtil.getInt(context,"appSettings","floattitle_portrait_x",mScreenWidth);
            isVert =true;
        }
        Log.e(TAG, "floattitle_x:"+String.valueOf(floattitle_x));
        if (Math.abs(floattitle_x) < Math.abs(floattitle_x - mScreenWidth)){
            layoutParams.gravity= Gravity.START;
//            view = View.inflate(context, R.layout.window_lay_left, null);
            isLeft =true;

        }else{
            layoutParams.gravity =Gravity.END;
//            view = View.inflate(context, R.layout.window_lay_right, null);
            isLeft =false;
        }
    }

    private void  customView(){
        setSceneOrientation(context);
        if (isLeft){
            view = View.inflate(context, R.layout.window_lay_left, null);
            layoutParams.windowAnimations =R.style.PopupFloatTiltAnimLeft;
        }else{
            view = View.inflate(context, R.layout.window_lay_right, null);
            layoutParams.windowAnimations = R.style.PopupFloatTiltAnimright;

        }

        final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
//        final LinearLayout message_content = view.findViewById(R.id.message_content);
        RoundImageView imageView = view.findViewById(R.id.window_icon_img);
        if (this.notificationInfo.getLargeIcon() ==null){
            imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.getPackageName()));
        } else {
            imageView.setImageIcon(this.notificationInfo.getLargeIcon());
        }

        final TextView titleText = view.findViewById(R.id.window_title_text);
        final TextView contentText = view.findViewById(R.id.window_content_text);
        titleText.setText(this.notificationInfo.getTitle());
        contentText.setText(this.notificationInfo.getContent());

        if(this.notificationInfo.getTitle().length() >18 || this.notificationInfo.getContent().length() >18){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.width = (int)ToolUtils.dp2Px(250,context);
            messageLay.setLayoutParams(layoutParams);
        }
        final ViewGroup.LayoutParams imageIconLayoutParams = imageView.getLayoutParams();
        int iconWidthHeightValue = SpUtil.getInt(context,"floatTileCustonView","iconWidthHeightValue",-1);
        int iconTypeValue = SpUtil.getInt(context,"floatTileCustonView","iconTypeValue",-1);
        int iconRidusValue = SpUtil.getInt(context,"floatTileCustonView","iconRidusValue",-1);
        int titleTextSizeValue = SpUtil.getInt(context,"floatTileCustonView","titleTextSizeValue",-1);
        int contentTextSizeValue = SpUtil.getInt(context,"floatTileCustonView","contentTextSizeValue",-1);
        int rootPaddingValue = SpUtil.getInt(context,"floatTileCustonView","rootPaddingValue",-1);
        int rootElevationValue = SpUtil.getInt(context,"floatTileCustonView","rootElevationValue",-1);

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
        if (rootPaddingValue != -1){
            view.setPadding(rootPaddingValue,rootPaddingValue,rootPaddingValue,rootPaddingValue);
        }
        if (rootElevationValue != -1){
            view.setElevation(rootElevationValue);
        }



    }
    private void viewInit(Context context) {
        layoutParams.x =0;
        if (!notificationInfo.isClearable){
            isRefuse =true;
            return;
        }
        if (notificationInfo.getContent() == null && notificationInfo.getTitle()==null) {
            isRefuse = true;
            return;
        }

        customView();
        intent = this.notificationInfo.getIntent();
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        viewWidth = view.getMeasuredWidth();
        viewHeight = view.getMeasuredHeight();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
//        setOnTouchListenr();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        layoutParams.format = PixelFormat.RGBA_8888;
//        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
//        layoutParams.windowAnimations = android.R.style.Animation_Translucent;




    }
    private void getScreenSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            windowManager.getDefaultDisplay().getSize(point);
            mScreenWidth = point.x;
            mScreenHeight = point.y;
        } else {
            mScreenWidth = windowManager.getDefaultDisplay().getWidth();
            mScreenHeight = windowManager.getDefaultDisplay().getHeight();
        }
//        mCurrentIconAlpha = 70 / 100f;
    }
    public void run(){
        if (isRefuse){
            return;
        }

        isEditPos = false;
        setOnTouchListenr();
        addViewToWindow();

    }
    public void run(Activity activity){
        isEditPos = true;
        setOnTouchListenr(activity);
        addViewToWindow();
    }
    public void addViewToWindow() {
        String showDirection = SpUtil.getString(context,"appSettings","floattitle_tileDirection","down");
//        String showDirection = SpUtil.getSp(context,"appSettings").getString("floattitle_tileDirection", "down");

        if(isVert){
            layoutParams.y = SpUtil.getInt(context,"appSettings","floattitle_portrait_y", -65);
//            layoutParams.y = SpUtil.getSp(context,"appSettings").getInt("floattitle_portrait_y", -65);
        }else
        {
            layoutParams.y = SpUtil.getInt(context,"appSettings","floattitle_landscape_y", -65);

//            layoutParams.y = SpUtil.getSp(context,"appSettings").getInt("floattitle_landscape_y", -101);
        }

        int mostShowNum = Integer.parseInt(SpUtil.getString(context,"appSettings","floattitle_tileShowNum", "4"));
        TileObject.setMostShowTitleNum(mostShowNum);
        if(TileObject.showTileNum < mostShowNum){
            showID = TileObject.getNextPosition();
            if(showID != -1){
                if (showDirection.equals("up")) {
                    layoutParams.y = layoutParams.y - (viewHeight + 18)*showID;
                } else {
                    layoutParams.y = layoutParams.y + (viewHeight + 18)*showID;
                }



                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            windowManager.addView(view, layoutParams);
                            long floattitle_time = Long.parseLong(SpUtil.getString(context,"appSettings","floattitle_time", "6"));
                            if(!isEditPos || floattitle_time <=0){
                                TimerTask timerTask = new TimerTask(){
                                    @Override
                                    public void run() {
                                        if (isOpen){
                                            removeTile();
                                        }
                                    }
                                };
                                mtimer =new Timer();
                                mtimer.schedule(timerTask,floattitle_time*1000);
                            }



                        } catch (WindowManager.BadTokenException e) {
                            // 无悬浮窗权限
                            e.printStackTrace();
                        }
                    }
                });
                TileObject.showingFloatingTileList.add(this);
                return;

            }
        }

        TileObject.waitingForShowingTileList.add(this);
    }

    private  void setOnTouchListenr(final Activity activity){
        final OnTouchListener editPosFloatingOnTouchListener = new OnTouchListener() {
            private int x;
            private int y;
            float lastX,lastY,changeX,changeY;
            int newX, newY;
            int viewx =0;

            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.e(TAG,"layoutParams"+String.valueOf(layoutParams.x)+","+String.valueOf(layoutParams.y));
                Log.e(TAG,"raw:"+String.valueOf(x)+","+String.valueOf(y));

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        if(isLeft) {
                            viewx = x;
                        }else {
                            viewx = mScreenWidth-x;
                        }
                        y = (int) event.getRawY();
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        Log.e(TAG,"viewx:"+String.valueOf(viewx));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        changeX = (int) event.getRawX();
                        changeY = (int) event.getRawY();
//                        newX = (int)(m)
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        Log.e(TAG,"viewwidth:"+viewWidth);
                        int movedX;
                        if(isLeft){
                            if(nowX-viewx>0){
                                movedX = nowX-viewx;
                            }else
                            {
                                movedX =0;
//                                if(isLeft) {
//                                    viewx = x;
//                                }else {
//                                    viewx = mScreenWidth-x;
//                                }
                            }
                        }else {
                            if((mScreenWidth-nowX-viewx)>0){
                                movedX = mScreenWidth-nowX-viewx;
                            }else
                            {
                                movedX =0;

                            }
                        }
                        Log.e(TAG,"viewx:"+String.valueOf(movedX));
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        layoutParams.x = movedX;
                        layoutParams.y = layoutParams.y+ movedY;
                        // 更新悬浮窗控件布局
                        windowManager.updateViewLayout(v, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isVert){
                            Log.e(TAG,"portrait:"+String.valueOf(x)+","+String.valueOf(layoutParams.y));
                            SpUtil.putInt(context,"appSettings","floattitle_portrait_x", x);
                            SpUtil.putInt(context,"appSettings","floattitle_portrait_y", layoutParams.y);
                        }else {
                            Log.e(TAG,"landscape:"+String.valueOf(x)+","+String.valueOf(layoutParams.y));
                            SpUtil.putInt(context,"appSettings","floattitle_landscape_x", x);
                            SpUtil.putInt(context,"appSettings","floattitle_landscape_y", layoutParams.y);
                        }
                        removeTile();
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        mtimer.cancel();
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
        view.setOnTouchListener(editPosFloatingOnTouchListener);
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
                return;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i(TAG, velocityX+","+velocityY);


                if (e2.getX() - e1.getX() > 90) {
                    if(isLeft){
                        if (isOpen){
                            TileObject.clearShowingTile();
                        }
                    }else {
                        closeNoificationInfo();
                    }
                    return true;
                }
                if (e1.getX() - e2.getX() > 90) {
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
                    }
                    Log.i(TAG, "向上滑...");
                    return true;
                }




                Log.d("TAG", e2.getX() + " " + e2.getY());

                return false;
            }
        });
        view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
        });

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
        if (isRemove) {
            return;
        }
        if(mtimer !=null){
            mtimer.cancel();
        }
        isRemove = true;
        removeView();
        TileObject.removeSingleShowingTile(this);

    }


    public void removeView() {
        windowManager.removeView(view);
    }


}

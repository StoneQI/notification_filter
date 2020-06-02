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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.actioner.floatnotification.FloatNotificationGroup;
import com.stone.notificationfilter.util.NotificationInfo;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;

import java.util.LinkedHashMap;
import java.util.Map;
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
    private FloatingTileActioner lastTile;
    public boolean isRemove;
    private int viewWidth;
    private int viewHeight;
    private int mScreenHeight;
    private int mScreenWidth;
    private View view;
    private Timer mtimer =null;
    private  boolean isLeft=true;
    private  boolean isVert =false;

    class FloatingNotificationItem{
        protected View view;
        protected Timer mtimer =null;
        protected int viewWidth;
        protected int viewHeight;
        protected boolean isShow =false;
        protected boolean isOpen = true;
        protected PendingIntent intent =null;
        protected NotificationInfo notificationInfo =null;

        FloatingNotificationItem(Context context, NotificationInfo notificationInfo){
            final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
            ImageView imageView = view.findViewById(R.id.window_icon_img);
            if (this.notificationInfo.getLargeIcon() ==null){
                imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.getPackageName()));
            } else {
                imageView.setImageIcon(this.notificationInfo.getLargeIcon());
            }

            final TextView titleText = view.findViewById(R.id.window_title_text);
            final TextView contentText = view.findViewById(R.id.window_content_text);
            titleText.setText(this.notificationInfo.getTitle());
            contentText.setText(this.notificationInfo.getContent());

            intent = this.notificationInfo.getIntent();
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(width, height);
            viewWidth = view.getMeasuredWidth();
            viewHeight = view.getMeasuredHeight();
        }
    }

    public FloatingTileActioner(NotificationInfo notificationInfo, Context context, boolean isEditPos) {
        this.context = context;
        this.isEditPos =isEditPos;
        this.notificationInfo = notificationInfo;
        String content = this.notificationInfo.getContent();
        if(content.length() >18){
            this.notificationInfo.setContent(content.substring(0,18)+"...");
        }
//        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
//        int ori = mConfiguration.orientation; //获取屏幕方向

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        viewInit(context);
    }

    private void setSceneOrientation(Context context){
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        int floattitle_x=0;
        getScreenSize();
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            isVert =false;
            floattitle_x = SpUtil.getSp(context,"appSettings").getInt("floattitle_landscape_x", mScreenWidth) ;
//             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
            floattitle_x = SpUtil.getSp(context,"appSettings").getInt("floattitle_portrait_x", mScreenWidth);
            isVert =true;
        }
        Log.e(TAG, "floattitle_x:"+String.valueOf(floattitle_x));
        if (Math.abs(floattitle_x) < Math.abs(floattitle_x - mScreenWidth)){
            layoutParams.gravity= Gravity.LEFT;
//            view = View.inflate(context, R.layout.window_lay_left, null);
            isLeft =true;

        }else{
            layoutParams.gravity =Gravity.RIGHT;
//            view = View.inflate(context, R.layout.window_lay_right, null);
            isLeft =false;
        }
    }

    private void viewInit(Context context) {
        setSceneOrientation(context);
        if (isLeft){
            layoutParams.gravity= Gravity.LEFT;
            view = View.inflate(context, R.layout.window_lay_left, null);
        }else{
            layoutParams.gravity =Gravity.RIGHT;
            view = View.inflate(context, R.layout.window_lay_right, null);
        }
        layoutParams.x =0;
        final LinearLayout messageLay = view.findViewById(R.id.window_messgae_lay);
        ImageView imageView = view.findViewById(R.id.window_icon_img);
        if (this.notificationInfo.getLargeIcon() ==null){
            imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.getPackageName()));
        } else {
            imageView.setImageIcon(this.notificationInfo.getLargeIcon());
        }

        final TextView titleText = view.findViewById(R.id.window_title_text);
        final TextView contentText = view.findViewById(R.id.window_content_text);
        titleText.setText(this.notificationInfo.getTitle());
        contentText.setText(this.notificationInfo.getContent());

        intent = this.notificationInfo.getIntent();
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        viewWidth = view.getMeasuredWidth();
        viewHeight = view.getMeasuredHeight();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
//        if(viewWidth>mScreenWidth*(2/3)){
//            messageLay.setWeightSum(mScreenWidth*(2/3));
//            layoutParams.width = mScreenWidth*(2/3);
//        }
        setOnTouchListenr();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.windowAnimations = android.R.style.Animation_Translucent;

    }
    public void setLastTile(FloatingTileActioner lastTile) {
        this.lastTile = lastTile;
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
    public void showWindow(){
        isEditPos = false;
        setOnTouchListenr();
        addViewToWindow();

    }
    public void showWindow(Activity activity){
        isEditPos = true;
        setOnTouchListenr(activity);
        addViewToWindow();
    }
    public void addViewToWindow() {


        String showDirection = SpUtil.getSp(context,"appSettings").getString("floattitle_tileDirection", "down");

        if(isVert){
            layoutParams.y = SpUtil.getSp(context,"appSettings").getInt("floattitle_portrait_y", 1300);
        }else
        {
            layoutParams.y = SpUtil.getSp(context,"appSettings").getInt("floattitle_landscape_y", 450);
        }
//        if (SpUtil.getSp(context,"appSettings").getInt("floattitle_y", -1) == -1) {
////            layoutParams.x = 1024;
//            layoutParams.y = 300;
//        } else {
////            layoutParams.x = SpUtil.getSp(context,"appSettings").getInt("floattitle_x", -1);
//            layoutParams.y = SpUtil.getSp(context,"appSettings").getInt("floattitle_y", -1);
//        }

        int mostShowNum = Integer.parseInt(SpUtil.getSp(context,"appSettings").getString("floattitle_tileShowNum", "6"));


        if (TileObject.showTileNum == 0) {
            // 屏幕内没有任何 Tile
            lastTile = null;
        }
        if (lastTile != null) {
            if (TileObject.positionArray.size() != mostShowNum) {
                // 此时固定位置并未分配完毕
                if (showDirection.equals("up")) {
                    layoutParams.y = lastTile.layoutParams.y - viewHeight - 18;
                } else {
                    layoutParams.y = lastTile.layoutParams.y + viewHeight + 18;
                }
            } else {
                // OOM
                TileObject.lastFloatingTile = null;
            }
            if (TileObject.positionArray.get(layoutParams.y)) {
                // 位置冲突
                int y = TileObject.getYInNullTile();
                if (y != -1) {
                    // 如果屏幕内有空闲位置
                    layoutParams.y = y;
                    TileObject.positionArray.put(y, true);
                } else {
                    TileObject.waitingForShowingTileList.add(this);
                    return;
                }
            }
        }
        TileObject.positionArray.put(layoutParams.y, true);
        if (TileObject.showTileNum < mostShowNum){
            TileObject.showTileNum++;
        } else {
            // 非法显示，屏幕内磁贴数量已超过指定数量
            TileObject.waitingForShowingTileList.add(this);
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    windowManager.addView(view, layoutParams);
                    long floattitle_time = Long.parseLong(SpUtil.getSp(context,"appSettings").getString("floattitle_time", "6"));
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
                if (!isEditPos) {
                    TileObject.showingFloatingTileList.add(FloatingTileActioner.this);
                }

            }
        });
        TileObject.lastFloatingTile = this;
    }

    private  void setOnTouchListenr(final Activity activity){
        final OnTouchListener editPosFloatingOnTouchListener = new OnTouchListener() {
            private int x;
            private int y;
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
                        Log.e(TAG,"viewx:"+String.valueOf(viewx));
                        break;
                    case MotionEvent.ACTION_MOVE:

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
//                                if(isLeft) {
//                                viewx = x;
//                                }else {
//                                    viewx = mScreenWidth-x;
//                                }
                            }
                        }
                        Log.e(TAG,"viewx:"+String.valueOf(movedX));
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        layoutParams.x = movedX;
                        layoutParams.y = layoutParams.y + movedY;
                        // 更新悬浮窗控件布局
                        windowManager.updateViewLayout(v, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isVert){
                            SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_portrait_x", x).apply();
                            SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_portrait_y", layoutParams.y).apply();
                        }else {
                            SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_landscape_x", x).apply();
                            SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_landscape_y", layoutParams.y).apply();
                        }
                        removeTile();
                        if(isVert){
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
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
                if (e1.getX() - e2.getX() > FLIP_DISTANCE) {
                    if(!isLeft){
                        showNoificationInfo();
                    }else {
                        closeNoificationInfo();
                    }

                    Log.i(TAG, "向左滑...");
                    return true;
                }

                if (e1.getY() - e2.getY() > 40) {
                    if(isOpen){
                        removeTile();
                    }
                    Log.i(TAG, "向下滑...");
                    return true;
                }
                if (e2.getY() - e1.getY() > 40) {
                    if(isOpen){
                        removeTile();
                    }
                    Log.i(TAG, "向上滑...");
                    return true;
                }
                if (e2.getX() - e1.getX() > 50) {
                    if(isLeft){
                        showNoificationInfo();
                    }else {
                        closeNoificationInfo();
                    }
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
        TileObject.showTileNum--;
        isRemove = true;
        TileObject.showingFloatingTileList.remove(this);
        TileObject.positionArray.put(layoutParams.y, false);
        showWaitingTile();
        removeView();
    }

    public void showWaitingTile() {
        if (!TileObject.waitingForShowingTileList.isEmpty()) {
            FloatingTileActioner floatingTile = TileObject.waitingForShowingTileList.get(0);
            floatingTile.setLastTile(lastTile);
            floatingTile.addViewToWindow();
            TileObject.waitingForShowingTileList.remove(floatingTile);
        }
    }

    public void removeView() {
        if(mtimer !=null){
            mtimer.cancel();
        }
        windowManager.removeView(view);
    }


}

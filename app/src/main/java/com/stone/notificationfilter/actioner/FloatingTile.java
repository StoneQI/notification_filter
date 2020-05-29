package com.stone.notificationfilter.actioner;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.NotificationInfo;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Create by LingC on 2019/8/4 22:11
 */
public class FloatingTile {
    private static String TAG ="FloatingTile";
    private NotificationInfo notificationInfo;
    private Context context;
    private PendingIntent intent = null;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public boolean isEditPos=false;
    private boolean isOpen=true;
    private FloatingTile lastTile;
    public boolean isRemove;
    private int viewWidth;
    private int viewHeight;
    private View view;
    private Timer mtimer =null;

    public FloatingTile(NotificationInfo notificationInfo,Context context, boolean isEditPos) {
        this.context = context;
        this.isEditPos =isEditPos;
        this.notificationInfo = notificationInfo;
        String content = this.notificationInfo.getContent();
        if (!TextUtils.isEmpty(content)) {
            if (content.length() > 17) {
                this.notificationInfo.setContent(content.substring(0, 18) + "...");
            }
        }
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        viewInit(context);
    }

    private void viewInit(Context context) {
        view = View.inflate(context, R.layout.window_lay, null);
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
        setOnTouchListenr();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        layoutParams.format = PixelFormat.RGBA_8888;
//        layoutParams.gravity= Gravity.LEFT | Gravity.TOP;
        layoutParams.windowAnimations = android.R.style.Animation_Translucent;
    }
    public void setLastTile(FloatingTile lastTile) {
        this.lastTile = lastTile;
    }

    public void showWindow() {

        String showDirection = SpUtil.getSp(context,"appSettings").getString("floattitle_tileDirection", "down");

        if (SpUtil.getSp(context,"appSettings").getInt("floattitle_x", -1) == -1) {
            layoutParams.x = 1024;
            layoutParams.y = 300;
        } else {
            layoutParams.x = SpUtil.getSp(context,"appSettings").getInt("floattitle_x", -1);
            layoutParams.y = SpUtil.getSp(context,"appSettings").getInt("floattitle_y", -1);
        }

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
                    if(!isEditPos){
                        long floattitle_time = Long.parseLong(SpUtil.getSp(context,"appSettings").getString("floattitle_time", "6000"));
                        TimerTask timerTask = new TimerTask(){
                            @Override
                            public void run() {
                                if (isOpen){
                                    removeTile();
                                }
                            }
                        };
                        mtimer =new Timer();
                        mtimer.schedule(timerTask,floattitle_time);
                    }


                } catch (WindowManager.BadTokenException e) {
                    // 无悬浮窗权限
                    e.printStackTrace();
                }
                if (!isEditPos) {
                    TileObject.showingFloatingTileList.add(FloatingTile.this);
                }

            }
        });
        TileObject.lastFloatingTile = this;
    }

    private void setOnTouchListenr() {
        final OnTouchListener editPosFloatingOnTouchListener = new OnTouchListener() {
            private int x;
            private int y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e(TAG,String.valueOf(x)+","+String.valueOf(y));
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        layoutParams.x = layoutParams.x + movedX;
                        layoutParams.y = layoutParams.y + movedY;
                        // 更新悬浮窗控件布局
                        windowManager.updateViewLayout(v, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_x", layoutParams.x).apply();
                        SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_y", layoutParams.y).apply();
                        removeTile();
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
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
                    return true;
                }


                Log.d("TAG", e2.getX() + " " + e2.getY());

                return false;
            }
        });
        if (isEditPos) {
            view.setOnTouchListener(editPosFloatingOnTouchListener);
        } else {
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });
        }
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
            FloatingTile floatingTile = TileObject.waitingForShowingTileList.get(0);
            floatingTile.setLastTile(lastTile);
            floatingTile.showWindow();
            TileObject.waitingForShowingTileList.remove(floatingTile);
        }
    }

    public void removeView() {
        windowManager.removeView(view);
    }


}

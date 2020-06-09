package com.stone.notificationfilter.actioner.floatnotification;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FloatNotificationGroup {
    private static LinkedHashMap<Integer,View> viewMap =null;
    private static Map<Integer, Timer> timerMap =null;
    private static Map<Integer, PendingIntent> pendingIntentMap = null;

    private final static String TAG ="FloatNotificationGroup";
//    private Context context;
    private static LinearLayout layout=null;
    private static WindowManager windowManager =null;
    private static WindowManager.LayoutParams windowManagerLayoutParams =null;
    private static LinearLayout.LayoutParams linearLayoutParmas = null;
    private int totalNumber = 4;
    private int showNumber =0;
    private boolean isShow =false;
    private boolean isOpen = true;
    private  boolean isEditPos =false;

    private static volatile FloatNotificationGroup singleton;
    private FloatNotificationGroup(){};
    public static FloatNotificationGroup getInstance(Context context)
    {
        if (singleton==null)
        {
            synchronized (FloatNotificationGroup.class)
            {
                if (singleton==null)
                {
                    singleton=new FloatNotificationGroup();
                    windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    windowManagerLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    viewMap =new LinkedHashMap<Integer,View>();
                    timerMap = new LinkedHashMap<>();
                    pendingIntentMap = new LinkedHashMap<>();
//                    SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_x", layoutParams.x).apply();
//                    SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_y", layoutParams.y).apply();
                    layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    linearLayoutParmas =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                    LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
                    layoutAnimationController.setInterpolator(new AccelerateInterpolator());
                    layoutAnimationController.setDelay(0.5f);
                    layoutAnimationController.setOrder(LayoutAnimationController.ORDER_RANDOM);
                    layout.setLayoutAnimation(layoutAnimationController);
                    layout.setPadding(0,0,0,5);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        windowManagerLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    } else {
                        windowManagerLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                    }
                    windowManagerLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
                    windowManagerLayoutParams.format = PixelFormat.RGBA_8888;
                    windowManagerLayoutParams.gravity= Gravity.RIGHT | Gravity.TOP;
                    windowManagerLayoutParams.windowAnimations = android.R.style.Animation_Translucent;
                    windowManagerLayoutParams.x =0;
                    windowManagerLayoutParams.y =600;
//                    singleton.isRead =false;
                }
            }
        }
        return singleton;
    }

    public void addView(Context context, NotificationInfo notificationInfo){
        View view = View.inflate(context, R.layout.window_lay_right,null);
        final TextView titleText = view.findViewById(R.id.window_title_text);
        final TextView contentText = view.findViewById(R.id.window_content_text);
        titleText.setText("tetx");
        contentText.setText("text");
        viewMap.put(view.hashCode(),view);
//        pendingIntentMap.put(viewMap.hashCode(),notificationInfo.intent);
        addToSceen(false);
    }
    private Handler handler = new Handler();
    private void addToSceen(final boolean isUpdate) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!isUpdate){
                    if(showNumber < totalNumber){
                        if(viewMap.size()!=0){
                            final View view =getHead(viewMap).getValue();
                            linearLayoutParmas.setMargins(0,0,0,18);
                            view.setLayoutParams(linearLayoutParmas);
                            layout.addView(view);
                            updateLayout();
                            try{
                                if(showNumber==0){
                                    Log.e(TAG, String.valueOf(layout.getChildCount()));
                                    windowManager.addView(layout,windowManagerLayoutParams);
                                }else{
                                    windowManager.updateViewLayout(layout,windowManagerLayoutParams);
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                                return;
                            }
                            TimerTask timerTask = new TimerTask(){
                                @Override
                                public void run() {
                                    if (isOpen){
                                        removeView(view);
                                    }
                                }
                            };
                            Timer mtimer=new Timer();
                            Log.e(TAG,String.valueOf(view.hashCode()));
                            mtimer.schedule(timerTask,3000);
                            timerMap.put(view.hashCode(),mtimer);
                            showNumber++;
                            viewMap.remove(getHead(viewMap).getKey());
                        }
                    }
                }
                else {
                    windowManager.updateViewLayout(layout,windowManagerLayoutParams);
                }
            }
        });

    }

    public <K, V> Map.Entry<K, V> getHead(LinkedHashMap<K, V> map) {
        return map.entrySet().iterator().next();
    }


    private void setOnTouchListenr(final View view) {
//        final View.OnTouchListener editPosFloatingOnTouchListener = new View.OnTouchListener() {
//            private int x;
//            private int y;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.e(TAG,String.valueOf(x)+","+String.valueOf(y));
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        x = (int) event.getRawX();
//                        y = (int) event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        int nowX = (int) event.getRawX();
//                        int nowY = (int) event.getRawY();
//                        int movedX = nowX - x;
//                        int movedY = nowY - y;
//                        x = nowX;
//                        y = nowY;
//                        layoutParams.x = layoutParams.x + movedX;
//                        layoutParams.y = layoutParams.y + movedY;
//                        // 更新悬浮窗控件布局
//                        layout.updateViewLayout(v, layoutParams);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_x", layoutParams.x).apply();
//                        SpUtil.getSp(context,"appSettings").edit().putInt("floattitle_y", layoutParams.y).apply();
//                        removeTile();
//                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        };
        final GestureDetector gestureDetector= new GestureDetector(view.getContext(), new GestureDetector.OnGestureListener(){
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
//                    try {
//                        if(intent !=null){
//                            intent.send();
////                            removeTile();
//                        }
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
                }else {
                    view.findViewById(R.id.window_messgae_lay).setVisibility(View.VISIBLE);
                    isOpen = true;
//                    int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                    int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                    view.measure(width, height);
//                    int viewWidth = view.getMeasuredWidth();
//                    int viewHeight = view.getMeasuredHeight();
//                    layoutParams.width = viewWidth;
//                    layoutParams.height = viewHeight;
                    layout.updateViewLayout(view,linearLayoutParmas);
                    addToSceen(true);
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
//                    int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                    int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                    view.measure(width, height);
//                    int viewWidth = view.getMeasuredWidth();
//                    int viewHeight = view.getMeasuredHeight();
//                    layoutParams.width = viewWidth;
//                    layoutParams.height = viewHeight;
                    layout.updateViewLayout(view, linearLayoutParmas);
                    addToSceen(true);
                    Log.i(TAG, "向左滑...");
                    return true;
                }

                if (e1.getY() - e2.getY() > 40) {
                    if(isOpen){
                        removeView(view);
                    }
                    Log.i(TAG, "向下滑...");
                    return true;
                }
                if (e2.getY() - e1.getY() > 40) {
                    if(isOpen){
                        removeView(view);
                    }
                    Log.i(TAG, "向上滑...");
                    return true;
                }
                if (e2.getX() - e1.getX() > 50) {
                    view.findViewById(R.id.window_messgae_lay).setVisibility(View.GONE);
                    isOpen =false;
//                    int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                    int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                    view.measure(width, height);
//                    int viewWidth = view.getMeasuredWidth();
//                    int viewHeight = view.getMeasuredHeight();
//                    layoutParams.width = viewWidth;
//                    layoutParams.height = viewHeight;
                    layout.updateViewLayout(view, linearLayoutParmas);
                    Log.i(TAG, "向右滑...");
                    if(timerMap.get(view.hashCode()) != null)
                    {
                        timerMap.get(view.hashCode()).cancel();
                    }
                    return true;
                }


                Log.d("TAG", e2.getX() + " " + e2.getY());

                return false;
            }
        });
        if (isEditPos) {
//            view.setOnTouchListener(editPosFloatingOnTouchListener);
        } else {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });
        }
    }

    private void removeView(final View view) {

        if(view != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    layout.removeView(view);
                    showNumber--;
                    addToSceen(true);
                    if (showNumber == 0) {
                        windowManager.removeView(layout);
                        addToSceen(false);
                    }
                }
            });
        }
    }


    public  void updateLayout(){
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        layout.measure(width, height);
        int viewWidth = layout.getMeasuredWidth();
        int viewHeight = layout.getMeasuredHeight();

        windowManagerLayoutParams.width = viewWidth;
        windowManagerLayoutParams.height = viewHeight;
//        setOnTouchListenr();
    }



}

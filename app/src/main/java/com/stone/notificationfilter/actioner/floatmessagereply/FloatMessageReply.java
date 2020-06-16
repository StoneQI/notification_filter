package com.stone.notificationfilter.actioner.floatmessagereply;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.cbman.roundimageview.RoundImageView;
import com.google.android.material.button.MaterialButton;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.actioner.TileObject;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.PackageUtil;
import com.stone.notificationfilter.util.SpUtil;
import com.stone.notificationfilter.util.ToolUtils;

import java.util.Timer;
import java.util.TimerTask;

public class FloatMessageReply {

    private static String TAG ="FloatMessageReply";
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
    private InputMethodManager inputManager;
    private  NotificationCompat.CarExtender.UnreadConversation conversation;


    public FloatMessageReply(NotificationInfo notificationInfo, Context context) {
        this.context = context;
        this.isEditPos =isEditPos;
        this.notificationInfo = notificationInfo;
        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        WindowInit(context);
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
//        if (Math.abs(floattitle_x) < Math.abs(floattitle_x - mScreenWidth)){
//            layoutParams.gravity= Gravity.START;
////            view = View.inflate(context, R.layout.window_lay_left, null);
//            isLeft =true;
//
//        }else{
//            layoutParams.gravity =Gravity.END;
////            view = View.inflate(context, R.layout.window_lay_right, null);
//            isLeft =false;
//        }
    }

    private void  ViewInit(){
//        setSceneOrientation(context);

        view = View.inflate(context, R.layout.float_message_reply, null);

        RoundImageView imageView = view.findViewById(R.id.notification_icon);
        if (this.notificationInfo.largeIcon ==null){
            imageView.setImageDrawable(PackageUtil.getAppIconFromPackname(context, this.notificationInfo.packageName));
        } else {
            imageView.setImageIcon(this.notificationInfo.largeIcon);
        }

        final TextView titleText = view.findViewById(R.id.notification_title);
        final TextView contentText = view.findViewById(R.id.notification_content);
        final EditText replyEditText = view.findViewById(R.id.notification_reply);
        final Button replayButton = view.findViewById(R.id.notification_reply_button);
        replyEditText.setFocusable(true);
        replyEditText.setFocusableInTouchMode(true);


        titleText.setText(this.notificationInfo.title);
        contentText.setText(this.notificationInfo.content);

        replyEditText.requestFocus();

        inputManager =
                (InputMethodManager) replyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        Timer timer =new Timer();

        timer.schedule(new TimerTask() {

            @Override

            public void run() {
                inputManager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);

            }

        },200);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               replayAndClose(true,replyEditText.getText().toString());


            }
        });

//        view.setFocusableInTouchMode(true);
        replyEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                Log.e("wytings", String.valueOf(keyCode));
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    replayAndClose(true,replyEditText.getText().toString());
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                        || event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) {

                    if(event.getAction()==KeyEvent.ACTION_DOWN){   //按键  按下和移开会有两个不同的事件所以需要区分
                        Log.e(TAG,"back key");
                        replayAndClose(false,null);
                    }

                }
                return false;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    notificationInfo.intent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                removeTile();

            }
        });




    }

    public void replayAndClose(boolean isReply,String replyContent){

        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        removeTile();

        if (isReply){
            if (TextUtils.isEmpty(replyContent)) return;

                if (conversation != null) {
                    PendingIntent pendingReply = conversation.getReplyPendingIntent();
                    RemoteInput remoteInput = conversation.getRemoteInput();
                    String key = remoteInput.getResultKey();

                    if (pendingReply != null) {
                        Intent localIntent = new Intent();
                        Bundle resultBundle = new Bundle();
                        resultBundle.putString(key, replyContent);
                        RemoteInput.addResultsToIntent(new RemoteInput[]{new RemoteInput.Builder(key).build()}, localIntent, resultBundle);
                        try {
                            pendingReply.send(context, 0, localIntent);
                        } catch (Exception e) {
                        }
                    }
                }

        }

    }

    private void WindowInit(Context context) {
        layoutParams.x =0;
        if (!notificationInfo.isClearable){
            isRefuse =true;
            return;
        }
        if (notificationInfo.content == null && notificationInfo.title==null) {
            isRefuse = true;
            return;
        }
        layoutParams.gravity =Gravity.TOP;
        layoutParams.y =30;

        ViewInit();
        intent = this.notificationInfo.intent;
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
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.format = PixelFormat.RGBA_8888;
//        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
//        layoutParams.windowAnimations = android.R.style.Animation_Translucent;




    }

    private void getScreenSize() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        mScreenWidth = point.x;
        mScreenHeight = point.y;
        //        mCurrentIconAlpha = 70 / 100f;
    }
    public void run(){
        Notification notification = notificationInfo.notification;
        if (notification != null) {
            NotificationCompat.CarExtender mCarExtender = new NotificationCompat.CarExtender(notification);
            if (mCarExtender != null) {
                conversation = mCarExtender.getUnreadConversation();
            }
        }

        addViewToWindow();

    }

    public void addViewToWindow() {


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            windowManager.addView(view, layoutParams);
                        } catch (WindowManager.BadTokenException e) {
                            // 无悬浮窗权限
                            e.printStackTrace();
                        }
                    }
                });
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

    }


    public void removeView() {
        windowManager.removeView(view);
    }

}


package com.stone.notificationfilter.actioner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.stone.notificationfilter.R;
import com.stone.notificationfilter.actioner.floatbutton.FloatWindow;
import com.stone.notificationfilter.actioner.floatbutton.MainActivity;
import com.stone.notificationfilter.util.NotificationInfo;
import com.stone.notificationfilter.util.SpUtil;

import java.util.Timer;
import java.util.TimerTask;

public class FloatCustomViewActioner {

    private static String TAG ="FloatCustomViewActioner";
    private NotificationInfo notificationInfo;
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private static View view = null;
    public boolean isRemove;
    private int viewWidth;
    private int viewHeight;
    private int mScreenHeight;
    private int mScreenWidth;
    private boolean isOpen=true;
    private static String key ="0";
    private PendingIntent intent = null;
//    private RemoteViews.RemoteView view=null;
    private static FloatWindow dialog;
    private static boolean isShow=false;
    public  FloatCustomViewActioner(NotificationInfo notificationInfo, Context context) {
        this.notificationInfo = notificationInfo;
        this.context = context;
        intent = this.notificationInfo.getIntent();

    }
    public void run(){

        if (!SpUtil.getSp(context,"appSettings").getBoolean("on_notification_float_message",true)){
            return;
        };
        view = notificationInfo.remoteViews.apply(context,null);
        Log.e(TAG,"Key"+key);
        if (key.equals(this.notificationInfo.key)&& isShow ==true){
            dialog.setContentView(view);
            dialog.updateExpanedView();
            return;
        }
        if (dialog!=null){
            dialog.dismiss();
        }
        dialog=new com.stone.notificationfilter.actioner.floatbutton.FloatWindow(context,500, new FloatWindow.IOnItemClicked() {
            @Override
            public void onBackItemClick() {
                try {
                    intent.send();
                }catch (Exception e){
                    e.printStackTrace();
                }
//                    Toast.makeText(context,"返回",Toast.LENGTH_SHORT).show();
                dialog.openOrCloseMenu();
            }

            @Override
            public void onCloseItemClick() {
                Toast.makeText(context,"通知悬浮组件关闭",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                isShow=false;
            }

            @Override
            public void onClose() {
                return;
            }

            @Override
            public void onExpand() {
                return;
            }
        });
        dialog.setContentView(view);
        dialog.show();
        isShow =true;
        key = this.notificationInfo.key;
        Log.e(TAG,"Show Window renew");
    }
}

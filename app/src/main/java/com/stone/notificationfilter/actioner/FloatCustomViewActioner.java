package com.stone.notificationfilter.actioner;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.stone.notificationfilter.actioner.floatbutton.FloatWindow;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.SpUtil;

public class FloatCustomViewActioner {

    private static String TAG ="FloatCustomViewActioner";
    private NotificationInfo notificationInfo;
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public boolean isRemove;
    private int viewWidth;
    private int viewHeight;
    private int mScreenHeight;
    private int mScreenWidth;
    private boolean isOpen=true;
    private static String key ="0";
    private PendingIntent intent = null;
    private static FloatWindow dialog;
    private static View view = null;
    private static boolean isShow=false;
    public  FloatCustomViewActioner(NotificationInfo notificationInfo, Context context) {
        this.notificationInfo = notificationInfo;
        this.context = context;
        intent = this.notificationInfo.intent;

    }
    public void run(){
        if (this.notificationInfo.remoteViews == null)
            return;
        if (!SpUtil.getBoolean(context,"appSettings","on_notification_float_message",true)){
            return;
        };
        view = notificationInfo.remoteViews.apply(context,null);
        Log.e(TAG,"Key"+key);
        if (key.equals(this.notificationInfo.key)&& dialog!=null){
            dialog.setContentView(view,context);
            try {
                dialog.updateExpanedView();
            }catch (IllegalArgumentException e){
                dialog.dismiss();
                dialog = null;
                showFloatWindow();
            }

            return;
        }
        if (dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        showFloatWindow();
    }

    private void showFloatWindow() {
        dialog=new FloatWindow(context,500, new FloatWindow.IOnItemClicked() {
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
                dialog=null;
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
        dialog.setContentView(view,context);
        dialog.show();
        key = this.notificationInfo.key;
        Log.e(TAG,"Show Window renew");
    }

    public void remove(){
        Log.e(TAG,"remove Window renew");
        if (key!= null && key.equals(notificationInfo.key)){
            if(dialog!=null){
                dialog.dismiss();
                dialog = null;
            }
        }
    }
}

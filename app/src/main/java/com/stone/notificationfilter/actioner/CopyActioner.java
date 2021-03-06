package com.stone.notificationfilter.actioner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.stone.notificationfilter.notificationhandler.databases.NotificationInfo;
import com.stone.notificationfilter.util.SpUtil;

public class CopyActioner {
    private static String TAG ="CopyActioner";
    private NotificationInfo notificationInfo;
    private Context context;
//    private PendingIntent intent = null;

    public CopyActioner(NotificationInfo notificationInfo, Context context) {
        this.notificationInfo = notificationInfo;
        this.context = context;
    }
    public void  run(){
        String mode = SpUtil.getString(context,"appSettings","copyactioner_select_mode","title");
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//        Log.e(TAG,TAG);
        String content = "";
        if(mode.equals("title")){
            content = notificationInfo.title;
        }
        if(mode.equals("content")){
            content = notificationInfo.content;
        }
        Log.e(TAG,content);
        String finalContent = content;
        new Handler(Looper.getMainLooper()).post(()-> {
            if (!StringUtils.isEmpty(finalContent)) {
                ClipData mClipData = ClipData.newPlainText("Label", finalContent);
                cm.setPrimaryClip(mClipData);
//        Toast.makeText(context,"\""+content+"\"已被复制",);
                Toast.makeText(context, "\"" + finalContent + "\"已被复制", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

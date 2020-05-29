package com.stone.notificationfilter.actioner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.stone.notificationfilter.util.NotificationInfo;
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
        String mode = SpUtil.getSp(context,"appSettings").getString("copyactioner_select_mode","title");
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        Log.e(TAG,mode);
        String content = "";
        if(mode.equals("title")){
            content = notificationInfo.title;
        }
        if(mode.equals("content")){
            content = notificationInfo.content;
        }
        Log.e(TAG,content);
        if(!StringUtils.isEmpty(content)){
            ClipData mClipData = ClipData.newPlainText("Label", content);
            cm.setPrimaryClip(mClipData);
//        Toast.makeText(context,"\""+content+"\"已被复制",);
            Toast.makeText(context, "\""+content+"\"已被复制", Toast.LENGTH_SHORT).show();
        }

    }
}

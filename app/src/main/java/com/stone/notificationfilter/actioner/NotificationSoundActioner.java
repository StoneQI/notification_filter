package com.stone.notificationfilter.actioner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.stone.notificationfilter.R;
import com.stone.notificationfilter.util.NotificationInfo;
import com.stone.notificationfilter.util.SpUtil;

public class NotificationSoundActioner {

    private static String TAG ="NotificationSoundActioner";
    private SoundPool soundPool;
    private Context context;
    private int soundID;
//    private PendingIntent intent = null;


    public NotificationSoundActioner(NotificationInfo notificationInfo, Context context) {
        this.context = context;
        soundPool = new SoundPool.Builder().build();
        soundID = soundPool.load(context, R.raw.wenxing, 1);
    }
    public void  run(){
        Log.e(TAG,TAG);
        if (!SpUtil.getSp(context,"appSettings").getBoolean("on_sound_message",false)){
            return;
        };
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                soundPool.play(1,  //声音id
                        1, //左声道
                        1, //右声道
                        1, //优先级
                        0, // 0表示不循环，-1表示循环播放
                        1);//播放比率，0.5~2，一般为1
            }
        });
    }
}

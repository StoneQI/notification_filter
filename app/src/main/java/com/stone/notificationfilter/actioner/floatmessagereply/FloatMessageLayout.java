package com.stone.notificationfilter.actioner.floatmessagereply;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class FloatMessageLayout extends LinearLayout {
    public FloatMessageLayout(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                || event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) {

            if(event.getAction()==KeyEvent.ACTION_DOWN){   //按键  按下和移开会有两个不同的事件所以需要区分
                Log.e("FloatMessageLayout","back key");
            }

        }
        return super.dispatchKeyEvent(event);
    }
}

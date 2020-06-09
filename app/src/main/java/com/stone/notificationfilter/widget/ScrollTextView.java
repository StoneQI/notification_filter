package com.stone.notificationfilter.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;


import androidx.annotation.Nullable;

/**
 * 跑马灯效果的TextView, 使用方式：
 * xml文件中记得设置：android:focusable="true", android:singleLine="true"
 *
 * Created by dasu on 2017/3/21.
 * http://www.jianshu.com/u/bb52a2918096
 */

class ScrollTextView extends androidx.appcompat.widget.AppCompatTextView {

    public ScrollTextView(Context context) {
        super(context);
        initView();
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setSingleLine(true);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public boolean isFocused() {
        return true;
    }


}



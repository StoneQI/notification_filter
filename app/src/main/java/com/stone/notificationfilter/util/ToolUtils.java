package com.stone.notificationfilter.util;

import android.content.Context;
import android.util.TypedValue;

public class ToolUtils {

    public static int dp2Px(float dp, Context mContext) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                mContext.getResources().getDisplayMetrics());
    }
}

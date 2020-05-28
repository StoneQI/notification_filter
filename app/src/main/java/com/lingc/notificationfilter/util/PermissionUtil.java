package com.lingc.notificationfilter.util;

import android.content.Context;
import android.provider.Settings;

import com.lingc.notificationfilter.basefloat.FloatWindowParamManager;

public class PermissionUtil {
    public static boolean notificationListenerEnable(Context context) {
        boolean enable = false;
        String packageName = context.getPackageName();
        String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (flat != null) {
            enable = flat.contains(packageName);
        }
        return enable;
    }

    public static boolean floatPermissionEnable(Context context) {
        return FloatWindowParamManager.checkPermission(context);
//        return premiss;
    }

    public static boolean PermissionEnable(Context context) {
        return floatPermissionEnable(context) && notificationListenerEnable(context);
//
    }


}

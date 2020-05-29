package com.stone.notificationfilter.notificationlog.objects;

import android.graphics.drawable.Drawable;


import java.util.HashMap;
import java.util.Map;


public class NotificationLogItem {
    public long id;
    public String mAppName;
    protected Drawable mAppIcon;
    public String mTitle;
    public String mPostTime;
    public String mContent;
    public String mPackageName;

//    private static LruCache<String, Drawable> sAppIconCache;
    private static Map<String,Drawable> sAppIconCache = new HashMap<>();
    public long getChildId(){
        return id;
    }
    public void  setAppIcon(Drawable appIcon){
        if (mAppIcon == null) {
//            final String key = getKey();
            mAppIcon = sAppIconCache.get(mPackageName);
            if (mAppIcon == null) {
                sAppIconCache.put(mPackageName, appIcon);
            }
        }
//        mAppIcon = appIcon;
    }
//    @Override
    public Drawable getAppIcon() {
       return mAppIcon = sAppIconCache.get(mPackageName);
    }

}

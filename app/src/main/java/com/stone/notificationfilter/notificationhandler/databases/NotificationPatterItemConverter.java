package com.stone.notificationfilter.notificationhandler.databases;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import java.util.List;

class NotificationPatterItemConverter {
    @TypeConverter
    public static List<NotificationHandlerItem.NotificationPatterItem> revert(String areaInfoStr) {
        // 使用Gson方法把json格式的string转成List
        //            val listType = Object:TypeTokenList<NotificationHandleItem.NotificationPatterItem>() {}.type;
        return (List<NotificationHandlerItem.NotificationPatterItem>) new Gson().fromJson(areaInfoStr, NotificationHandlerItem.NotificationPatterItem.class);
    }

    @TypeConverter
    public static String converter(List<NotificationHandlerItem.NotificationPatterItem> areaInfoStr) {
        // 使用Gson方法把List转成json格式的string，便于我们用的解析
        return new Gson().toJson(areaInfoStr);
    }
}

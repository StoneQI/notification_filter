package com.stone.notificationfilter.notificationhandler.databases;

import java.util.HashSet;

public class NotificationHandlerItem {
    public long ID =0;
    public  int orderID=0;
    public  String name="";
    public boolean isOff =true;
    public  HashSet<String> packageNames =new HashSet<>();

    public static class NotificationPatterItem{
        public String PatternItem="title";
        /**
         *匹配模式
         * 1. “contain”
         * 2. “nocontain”
         * 3. "regex"
         */
        public String PatternMode="contain";
        public String PatternRule="";
        public Boolean isRequire=false;

        public NotificationPatterItem(){}

        public NotificationPatterItem(String patternItem, String patternMode, String patternRule, Boolean isRequire) {
            PatternItem = patternItem;
            PatternMode = patternMode;
            PatternRule = patternRule;
            this.isRequire = isRequire;
        }
    }

    public HashSet<NotificationPatterItem> notificationPatterItems = new HashSet<>();

    public String titleFiliter="";
    public String titleFiliterReplace="";
    public String contextFiliter="";
    public String contextFiliterReplace="";
    public Boolean breakDown =false;
//        <string-array name="notification_actioner">
//        <item>悬浮通知</item>
//        <item>丢弃通知</item>
//        <item>复制到内容到剪贴板</item>
//        <item>执行点击事件</item>
//        <item>保存到日记记录</item>
//        <item>播放提示音</item>
//        <item>通知组件悬浮</item>
//        <item>弹幕悬浮</item>
//    </string-array>
//
//    <string-array name="notification_sceen_status_on">
//        <item>横竖屏同时启用</item>
//        <item>仅竖屏屏启用</item>
//        <item>仅横屏启用</item>
//    </string-array>
    public String actioner = "float_tile_notification";
    public int sceen_status_on = 0;

    public static int getActionerIndex(String value){
        switch (value){
            case "float_tile_notification":return 0;
            case "system_notification":return 1;
            case "drop_notification":return 2;
            case "copy_notification":return 3;
            case "click_notification":return 4;
            case "save_log_notification":return 5;
            case "sound_notification":return 6;
            case "float_notification":return 7;
            case "danmu_notification":return 8;
        };
        return -1;
    }
    public static String getActionerValue(int value){
        switch (value){
            case 0:return "float_tile_notification";
            case 1:return "system_notification";
            case 2:return "drop_notification";
            case 3:return "copy_notification";
            case 4:return "click_notification";
            case 5:return "save_log_notification";
            case 6:return "sound_notification";
            case 7:return "float_notification";
            case 8:return "danmu_notification";
        };
        return "-1";
    }

}

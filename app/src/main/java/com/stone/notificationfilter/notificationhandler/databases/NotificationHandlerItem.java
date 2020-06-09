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
//        <item>系统通知</item>
//        <item>丢弃通知</item>
//        <item>复制到内容到剪贴板</item>
//        <item>执行点击事件</item>
//        <item>保存到日记记录</item>
//        <item>播放提示音</item>
//        <item>通知组件悬浮</item>
//    </string-array>
//
//    <string-array name="notification_sceen_status_on">
//        <item>横竖屏同时启用</item>
//        <item>仅竖屏屏启用</item>
//        <item>仅横屏启用</item>
//    </string-array>
    public int actioner = 0;
    public int sceen_status_on = 0;

}

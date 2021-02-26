package com.stone.notificationfilter.notificationhandler.databases;

import java.util.HashSet;

public class NotificationRuleItem {
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
    public int sceen_status_on = 0;

}

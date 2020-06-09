package com.stone.notificationfilter.notificationhandler.databases;

import com.stone.notificationfilter.entitys.notificationfilter.NotificationFilterEntity;

import java.util.ArrayList;
import java.util.HashSet;

public class SystemBaseHandler {
    public  static ArrayList<NotificationHandlerItem> getSystemHandlerRule(){

        ArrayList<NotificationHandlerItem> notificationHandlerItems = new ArrayList<>();

        NotificationHandlerItem notificationHandlerItem = new NotificationHandlerItem();
        notificationHandlerItem.orderID =1;
        notificationHandlerItem.name ="禁止通知";
        HashSet< NotificationHandlerItem.NotificationPatterItem> notificationPatterItems = new HashSet<>();
        notificationPatterItems.add(new NotificationHandlerItem.NotificationPatterItem("title",
                "contain",
                "正在其他应用的上层显示内容",false));
        notificationPatterItems.add(new NotificationHandlerItem.NotificationPatterItem("content",
                "contain",
                "正在运行",false));
        notificationPatterItems.add(new NotificationHandlerItem.NotificationPatterItem("content",
                "contain",
                "下载",false));

        notificationHandlerItem.notificationPatterItems = notificationPatterItems;
        notificationHandlerItem.actioner = 2;
        notificationHandlerItem.breakDown =true;

        notificationHandlerItems.add(notificationHandlerItem);



        NotificationHandlerItem notificationHandlerItem2 = new NotificationHandlerItem();
        notificationHandlerItem2.orderID = 5;
        notificationHandlerItem2.name = "播放网易云";
//        notificationMatcher6.contextPatter="^(?!.*?个联系人给你发过来).*$";
        HashSet<String> strings = new HashSet<>();
        strings.add("com.netease.cloudmusic");
        strings.add("com.tencent.qqmusic");
        notificationHandlerItem2.packageNames =strings;
        notificationHandlerItem2.actioner = 7;
        notificationHandlerItem2.breakDown =true;
        notificationHandlerItems.add(notificationHandlerItem2);


        NotificationHandlerItem notificationHandlerItem3 = new NotificationHandlerItem();
        notificationHandlerItem3.orderID = 5;
        notificationHandlerItem3.name = "播放微信提示音";
//        notificationMatcher6.contextPatter="^(?!.*?个联系人给你发过来).*$";
        HashSet<String> strings2 = new HashSet<>();
        strings2.add("com.tencent.tim");
        strings2.add("com.tencent.mm");
        notificationHandlerItem3.packageNames =strings2;
        notificationHandlerItem3.actioner = 6;
        notificationHandlerItem3.breakDown =false;
        notificationHandlerItems.add(notificationHandlerItem3);


        NotificationHandlerItem notificationHandlerItem4 = new NotificationHandlerItem();
        notificationHandlerItem4.orderID = 5;
        notificationHandlerItem4.name = "日记记录";
//
        HashSet<String> strings4 = new HashSet<>();
        strings4.add("com.tencent.tim");
        strings4.add("com.tencent.mm");
        strings4.add("com.tencent.mobileqq");
        notificationHandlerItem4.packageNames =strings4;
        HashSet< NotificationHandlerItem.NotificationPatterItem> notificationPatterItems4 = new HashSet<>();
        notificationPatterItems4.add(new NotificationHandlerItem.NotificationPatterItem("content",
                "regex",
                "^(?!.*?个联系人给你发过来).*$",true));
        notificationHandlerItem4.notificationPatterItems = notificationPatterItems4;
        notificationHandlerItem4.actioner = 6;
        notificationHandlerItem4.breakDown =false;
        notificationHandlerItems.add(notificationHandlerItem4);



        NotificationHandlerItem notificationHandlerItem5 = new NotificationHandlerItem();
        notificationHandlerItem5.orderID = 5;
        notificationHandlerItem5.name = "默认悬浮通知";
        notificationHandlerItem5.actioner = 0;
        notificationHandlerItem5.breakDown =true;
        notificationHandlerItems.add(notificationHandlerItem5);


        return  notificationHandlerItems;
    }
}

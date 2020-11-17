package com.stone.notificationfilter.notificationhandler.databases;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.widget.RemoteViews;

public class NotificationInfo {


    public NotificationInfo(int ID, String key, long postTime) {
        this.ID = ID;
        this.key = key;
        this.postTime = postTime;
    }
    public int ID;
    public String key;
    public long postTime;
    public Boolean isClearable=true;
    public Boolean isOnGoing=false;
    public String packageName;
    public String Tag;
    public Icon smallIcon;
    public Icon largeIcon;
    public String title="";
    public String content="";
    public Boolean isInteractive =false;
    public PendingIntent intent =null;
    public RemoteViews remoteViews;
    public Notification notification=null;
    public int sceenStatus;
    public String ChannelID;
    public String ChannelGROUPID;
    public Boolean hasBigCustomView= false;
    public Boolean hasContentView = false;
    public Boolean hasHeadsUpContentView = false;
    public Boolean hasCustomView = false;
    public String template;


    public String getAttribute(String attributeName){
        switch (attributeName){
            case "title": return title;
            case "content":return content;
        }
        return "";
    }


//    public String

}

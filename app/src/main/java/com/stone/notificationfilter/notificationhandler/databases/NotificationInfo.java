package com.stone.notificationfilter.notificationhandler.databases;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.widget.RemoteViews;

public class NotificationInfo {
    public Boolean getClearable() {
        return isClearable;
    }

    public void setClearable(Boolean clearable) {
        isClearable = clearable;
    }

    public Boolean getOnGoing() {
        return isOnGoing;
    }

    public void setOnGoing(Boolean onGoing) {
        isOnGoing = onGoing;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public Icon getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(Icon smallIcon) {
        this.smallIcon = smallIcon;
    }

    public Icon getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(Icon largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PendingIntent getIntent() {
        return intent;
    }

    public void setIntent(PendingIntent intent) {
        this.intent = intent;
    }

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
    public String title;
    public String content;
    public Boolean isInteractive =false;
    public PendingIntent intent =null;
    public RemoteViews remoteViews;
    public int sceenStatus;

    public Boolean getInteractive() {
        return isInteractive;
    }

    public void setInteractive(Boolean interactive) {
        isInteractive = interactive;
    }

    public String getAttribute(String attributeName){
        switch (attributeName){
            case "title": return title;
            case "content":return content;
        }
        return "";
    }


//    public String

}

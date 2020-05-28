package com.lingc.notificationfilter.util;

import android.app.PendingIntent;
import android.graphics.drawable.Icon;

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
    public Boolean isClearable;
    public Boolean isOnGoing;
    public String packageName;
    public String Tag;
    public Icon smallIcon;
    public Icon largeIcon;
    public String title;
    public String content;
    public PendingIntent intent;
//    public String

}

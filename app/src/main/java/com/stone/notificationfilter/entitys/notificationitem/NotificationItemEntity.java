package com.stone.notificationfilter.entitys.notificationitem;

//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("post_time"),@Index("app_name"),@Index("package_name")})
public class NotificationItemEntity {

    @PrimaryKey(autoGenerate = true)
    public int ID;

    @ColumnInfo(name = "post_time")
    public long postTime;
    @ColumnInfo(name = "app_name")
    public String appName;

    public int dayOfYear;

    @ColumnInfo(name = "package_name")
    public String packageName;
    public String tag;
    public String intentUrl;
    public String title;
    public String content;



}

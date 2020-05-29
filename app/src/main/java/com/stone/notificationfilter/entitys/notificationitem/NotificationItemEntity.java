package com.stone.notificationfilter.entitys.notificationitem;

//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("post_time")})
public class NotificationItemEntity {

    @PrimaryKey(autoGenerate = true)
    public int ID;

    @ColumnInfo(name = "post_time")
    public long postTime;
    public String appName;
    public int dayOfYear;
    public String packageName;
    public String tag;
    public String title;
    public String content;



}

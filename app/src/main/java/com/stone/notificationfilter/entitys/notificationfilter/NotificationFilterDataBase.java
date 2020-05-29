package com.stone.notificationfilter.entitys.notificationfilter;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {NotificationFilterEntity.class},version = 2)
public abstract class NotificationFilterDataBase extends RoomDatabase {
    private static final String DB_NAME ="notificationfilter.db";
    private static volatile NotificationFilterDataBase instance;

    public static synchronized NotificationFilterDataBase getInstance(Context context){
        if(instance==null){
            instance=create(context);
        }
        return instance;
    }
    private static  NotificationFilterDataBase create(final Context context){
        return Room.databaseBuilder(context, NotificationFilterDataBase.class, DB_NAME)
                .build();
    }

    public abstract NotificationFilterDao NotificationFilterDao();
}

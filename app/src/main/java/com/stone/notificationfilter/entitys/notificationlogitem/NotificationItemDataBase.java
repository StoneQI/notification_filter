package com.stone.notificationfilter.entitys.notificationlogitem;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {NotificationItemEntity.class},version = 3)
public abstract class NotificationItemDataBase extends RoomDatabase {
    private static final String DB_NAME ="notificationitens.db";
    private static volatile NotificationItemDataBase instance;

    public static synchronized NotificationItemDataBase getInstance(Context context){
        if(instance==null){
            instance=create(context);
        }
        return instance;
    }
    private static NotificationItemDataBase create(final Context context){
        return Room.databaseBuilder(context, NotificationItemDataBase.class, DB_NAME)
                .build();
    }

    public abstract NotificationItemDao NotificationItemDao();
}

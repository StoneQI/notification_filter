package com.stone.notificationfilter.entitys.notificationfilter;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotificationFilterDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insertOne(NotificationFilterEntity notificationEntitie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public int updateOne(NotificationFilterEntity notificationEntitie);

    @Delete
    public int deleteOne(NotificationFilterEntity notificationEntitie);

    @Query("SELECT * FROM notificationfilterentity ORDER BY order_id")
    public List<NotificationFilterEntity> loadAll();

    @Query("SELECT * FROM notificationfilterentity ORDER BY order_id DESC")
    public List<NotificationFilterEntity> loadAllDESC();
    @Query("select * from notificationfilterentity where order_id= :id")
    public NotificationFilterEntity findById(int id);



}
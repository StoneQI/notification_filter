package com.stone.notificationfilter.entitys.notificationlogitem;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertOne(NotificationItemEntity notificationEntitie);

//    @Delete
//    public int deleteOne(NotificationItemEntity notificationEntitie);

    @Query("SELECT * FROM notificationitementity ORDER BY post_time")
    public List<NotificationItemEntity> loadAll();

    @Query("SELECT * FROM notificationitementity ORDER BY post_time DESC")
    public List<NotificationItemEntity> loadAllDESC();

    @Query("DELETE FROM notificationitementity WHERE id = :id ")
    public int deleteByID(long id);

    @Query("DELETE  FROM notificationitementity")
    public int deleteAll();

//    @Query("select * from notificationitementity where order_id= :id")
//    public NotificationItemEntity findById(int id);



}
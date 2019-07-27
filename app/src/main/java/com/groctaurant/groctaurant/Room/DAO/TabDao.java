package com.groctaurant.groctaurant.Room.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.groctaurant.groctaurant.Room.Entity.TabEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 09-11-2018.
 */

@Dao
public interface TabDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TabEntity tabEntity);

    @Update
    void update(TabEntity tabEntity);

    @Query("SELECT * FROM tab")
    List<TabEntity> load();

    @Query("UPDATE tab SET active_position= :activePosition WHERE order_id LIKE :orderId")
    void setActivePosition(String orderId, int activePosition);

    @Delete
    void delete(TabEntity tabEntity);

    @Query("SELECT order_number FROM tab WHERE order_id LIKE :orderId")
    String getOrderNumber(String orderId);
}

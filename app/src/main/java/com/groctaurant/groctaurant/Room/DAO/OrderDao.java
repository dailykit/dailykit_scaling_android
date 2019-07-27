package com.groctaurant.groctaurant.Room.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.groctaurant.groctaurant.Room.Entity.OrderEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 24-08-2018.
 */

@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(OrderEntity orderEntity);

    @Update
    void update(OrderEntity orderEntity);

    @Query("SELECT * FROM `order` WHERE order_number LIKE :orderId")
    OrderEntity loadOrder(String orderId);

    @Query("SELECT * FROM `order`")
    LiveData<List<OrderEntity>> loadAllOrder();

    @Query("SELECT * FROM `order`")
    List<OrderEntity> loadAllOrderList();

    @Query("SELECT COUNT(*) FROM `order`")
    int countOrder();
}

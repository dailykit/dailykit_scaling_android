package com.groctaurant.groctaurant.Room.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.groctaurant.groctaurant.Room.Entity.ItemEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 24-08-2018.
 */

@Dao
public interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ItemEntity itemEntity);

    @Update
    void update(ItemEntity itemEntity);

    @Query("SELECT * FROM item WHERE order_id LIKE :orderId")
    LiveData<List<ItemEntity>> loadAllItem(String orderId);

    @Query("SELECT * FROM item WHERE order_id LIKE :orderId")
    List<ItemEntity> loadItemsByOrderId(String orderId);

    @Query("SELECT * FROM item")
    List<ItemEntity> loadItems();

    @Query("UPDATE item SET selected_position= :selectedPosition WHERE item_order_id LIKE :orderId")
    void setSelectedItem(String orderId, int selectedPosition);

    @Query("SELECT * FROM item WHERE item_order_id LIKE :itemId")
    ItemEntity loadItem(String itemId);

    @Query("SELECT COUNT(*) FROM item")
    int countItemDao();

    @Query("SELECT recipe_name FROM item WHERE item_order_id LIKE :itemId")
    String getItemName(String itemId);

}

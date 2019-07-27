package com.groctaurant.groctaurant.Room.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;

import java.util.List;


/**
 * Created by Danish Rafique on 24-08-2018.
 */

@Dao
public interface IngredientDetailDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(IngredientDetailEntity ingredientDetailEntity);

    @Update
    void update(IngredientDetailEntity ingredientDetailEntity);

    @Query("SELECT * FROM ingredient_detail WHERE ingredient_id LIKE :ingredientId AND ingredient_is_deleted = 0")
    LiveData<List<IngredientDetailEntity>> loadAllIngredientDetail(String ingredientId);

    @Query("SELECT * FROM ingredient_detail ")
    List<IngredientDetailEntity> loadAll();

    @Query("SELECT * FROM ingredient_detail WHERE ingredient_id LIKE :ingredientId AND ingredient_is_deleted = 0")
    List<IngredientDetailEntity> loadIngredientDetailByIngredientId(String ingredientId);

    @Query("SELECT * FROM ingredient_detail")
    IngredientDetailEntity loadSingleIngredientEntity();

    @Query("UPDATE ingredient_detail SET ingredient_is_packed= :isPacked WHERE ingredient_detail_id LIKE :ingredientDetailId")
    void isPackedUpdate(String ingredientDetailId, boolean isPacked);

    @Query("UPDATE ingredient_detail SET ingredient_is_packed= :isPacked WHERE ingredient_id LIKE :ingredientId")
    void isPackedUpdateByIngredientId(String ingredientId, boolean isPacked);

    @Query("UPDATE ingredient_detail SET ingredient_is_deleted= :isDeleted WHERE ingredient_detail_id LIKE :ingredientDetailId")
    void isDeletedUpdate(String ingredientDetailId, boolean isDeleted);

    @Query("UPDATE ingredient_detail SET ingredient_pack_timestamp= :timestamp WHERE ingredient_detail_id LIKE :ingredientDetailId")
    void updateTimestamp(String ingredientDetailId, String timestamp);

    @Query("SELECT ingredient_is_packed FROM ingredient_detail WHERE ingredient_detail_id LIKE :ingredientDetailId")
    boolean isPacked(String ingredientDetailId);

    @Query("SELECT COUNT(*) FROM ingredient_detail")
    int countIngredientDetailDao();

    @Query("SELECT COUNT(*) FROM ingredient_detail WHERE ingredient_id LIKE :ingredientId")
    int countIngredientDetailPerIngredientId(String ingredientId);

    @Query("SELECT * FROM ingredient_detail WHERE ingredient_id LIKE :ingredientId AND ingredient_detail_position LIKE :position")
    IngredientDetailEntity getIngredientByPositionAndDetailId(String ingredientId,int position);

    @Query("SELECT * FROM ingredient_detail WHERE ingredient_detail_id LIKE :ingredientDetailId")
    IngredientDetailEntity getIngredientByIngredientDetailId(String ingredientDetailId);

    @Query("UPDATE ingredient_detail SET ingredient_measured_weight= :weight WHERE ingredient_detail_id LIKE :ingredientDetailId")
    void setMeasuredWeight(String ingredientDetailId, double weight);

    @Query("SELECT ingredient_quantity FROM ingredient_detail WHERE ingredient_id = :ingredientId")
    List<Double> getWeightByIngredientId(String ingredientId);

    @Query("SELECT * FROM ingredient_detail WHERE ingredient_name = :ingredientName")
    List<IngredientDetailEntity> getElementByIngredientName(String ingredientName);

    @Query("SELECT DISTINCT(ingredient_name) FROM ingredient_detail")
    List<String> getDistinctIngredientName();

}

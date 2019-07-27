package com.groctaurant.groctaurant.Room.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.groctaurant.groctaurant.Retrofit.Response.Ingredient;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 24-08-2018.
 */

@Dao
public interface IngredientDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(IngredientEntity ingredientEntity);

    @Update
    void update(IngredientEntity ingredientEntity);

    @Query("SELECT * FROM ingredient WHERE item_id LIKE :itemId AND ingredient_is_deleted = 0")
    LiveData<List<IngredientEntity>> loadAllIngredient(String itemId);

    @Query("SELECT * FROM ingredient WHERE item_id LIKE :itemId AND ingredient_is_deleted = 0")
    List<IngredientEntity> loadIngredientByItemId(String itemId);

    @Query("SELECT * FROM ingredient WHERE ingredient_is_deleted = 0")
    List<IngredientEntity> loadIngredients();

    @Query("SELECT COUNT(*) FROM ingredient")
    int countIngredientDao();

    @Query("UPDATE ingredient SET ingredient_is_packed_complete= :isPacked WHERE ingredient_id LIKE :ingredientId")
    void isPackedCompleteUpdate(String ingredientId, boolean isPacked);

    @Query("UPDATE ingredient SET ingredient_is_deleted= :isDeleted WHERE ingredient_id LIKE :ingredientId")
    void isDeletedUpdate(String ingredientId, boolean isDeleted);

    @Query("SELECT COUNT(*) FROM ingredient WHERE item_id LIKE :itemId AND ingredient_is_packed_complete LIKE :isPackedComplete")
    int countIngredientPackedDao(String itemId, boolean isPackedComplete);

    @Query("UPDATE ingredient SET selected_ingredient_position= :selectedIngredientPosition WHERE ingredient_id LIKE :ingredientId")
    void setSelectedItem(String ingredientId, int selectedIngredientPosition);

    @Query("SELECT * FROM ingredient WHERE ingredient_id LIKE :ingredientId")
    IngredientEntity getIngredientById(String ingredientId);

    @Query("UPDATE ingredient SET ingredient_measured_total_weight= :weight WHERE ingredient_id LIKE :ingredientId")
    void setMeasuredTotalWeight(String ingredientId, double weight);

    @Query("SELECT DISTINCT(slip_name) FROM ingredient")
    List<String> getDistinctSlipName();

    @Query("SELECT slip_name FROM ingredient WHERE ingredient_id = :ingredientId")
    String getSlipNameFromIngredientId(String ingredientId);

}

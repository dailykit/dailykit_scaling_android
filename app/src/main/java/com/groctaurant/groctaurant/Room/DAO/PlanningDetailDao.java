package com.groctaurant.groctaurant.Room.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.groctaurant.groctaurant.Room.Entity.IngredientPlanningEntity;
import com.groctaurant.groctaurant.Room.Entity.PlanningDetailEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 02-04-2019.
 */

@Dao
public interface PlanningDetailDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PlanningDetailEntity planningDetailEntity);

    @Update
    void update(PlanningDetailEntity planningDetailEntity);

    @Query("SELECT COUNT(*) FROM planning_detail")
    int countPlanningDetail();

    @Query("DELETE FROM planning_detail")
    void deleteAll();

    @Query("SELECT * FROM planning_detail WHERE planning_model_id IS :planningModelId AND ingredient_planning_id IS :ingredientPlanningId")
    List<PlanningDetailEntity> load(int planningModelId,int ingredientPlanningId);

    @Query("UPDATE planning_detail SET ingredient_is_packed= :isPacked WHERE ingredient_detail_id LIKE :ingredientDetailId")
    void isPackedUpdate(String ingredientDetailId, boolean isPacked);

}

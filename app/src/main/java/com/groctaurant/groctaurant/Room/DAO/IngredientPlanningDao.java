package com.groctaurant.groctaurant.Room.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.groctaurant.groctaurant.Room.Entity.IngredientPlanningEntity;
import com.groctaurant.groctaurant.Room.Entity.PlanningEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 31-03-2019.
 */

@Dao
public interface IngredientPlanningDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(IngredientPlanningEntity ingredientPlanningEntity);

    @Update
    void update(IngredientPlanningEntity ingredientPlanningEntity);

    @Query("SELECT COUNT(*) FROM ingredient_planning")
    int countIngredientPlanning();

    @Query("DELETE FROM ingredient_planning")
    void deleteAll();

    @Query("SELECT * FROM ingredient_planning WHERE planning_model_id IS :planningModelId")
    List<IngredientPlanningEntity> load(int planningModelId);

    @Query("UPDATE ingredient_planning SET ingredient_planning_selected_position= :position WHERE ingredient_planning_id LIKE :ingredientPlanningId")
    void setSelectedPosition(int ingredientPlanningId,int position);

}

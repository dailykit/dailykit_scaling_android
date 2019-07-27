package com.groctaurant.groctaurant.Room.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.groctaurant.groctaurant.Room.Entity.ItemEntity;
import com.groctaurant.groctaurant.Room.Entity.PlanningEntity;

import java.util.List;

/**
 * Created by Danish Rafique on 31-03-2019.
 */
@Dao
public interface PlanningDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PlanningEntity planningEntity);

    @Update
    void update(PlanningEntity planningEntity);

    @Query("SELECT COUNT(*) FROM `planning`")
    int countPlanning();

    @Query("DELETE FROM planning")
    void deleteAll();

    @Query("SELECT * FROM planning")
    List<PlanningEntity> load();

    @Query("SELECT * FROM planning WHERE planning_model_id IS :planningModelId ")
    PlanningEntity fetchByPlanningId(int planningModelId);

    @Query("UPDATE planning SET ingredient_selected_process_index= :position WHERE planning_model_id LIKE :planningModelId")
    void setSelectedProcessIndex(int planningModelId,int position);


}

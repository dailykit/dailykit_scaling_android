package com.groctaurant.groctaurant.Room.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.groctaurant.groctaurant.Model.PlanningModel;

/**
 * Created by Danish Rafique on 31-03-2019.
 */

@Entity(tableName = "planning")
public class PlanningEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "planning_model_id")
    public int planningModelId;

    @NonNull
    @ColumnInfo(name = "ingredient_name")
    public String ingredientName;

    @NonNull
    @ColumnInfo(name = "ingredient_total_weight")
    public double ingredientTotalWeight;

    @NonNull
    @ColumnInfo(name = "ingredient_selected_process_index")
    public int ingredientSelectedProcessIndex;


    public PlanningEntity(int planningModelId, @NonNull String ingredientName, double ingredientTotalWeight, int ingredientSelectedProcessIndex) {
        this.planningModelId = planningModelId;
        this.ingredientName = ingredientName;
        this.ingredientTotalWeight = ingredientTotalWeight;
        this.ingredientSelectedProcessIndex = ingredientSelectedProcessIndex;
    }

    public PlanningEntity(PlanningModel planningModel) {
        this.planningModelId = planningModel.getPlanningModelId();
        this.ingredientName = planningModel.getIngredientName();
        this.ingredientTotalWeight = planningModel.getIngredientTotalWeight();
        this.ingredientSelectedProcessIndex = planningModel.getIngredientSelectedProcessIndex();
    }

    public int getPlanningModelId() {
        return planningModelId;
    }

    public void setPlanningModelId(int planningModelId) {
        this.planningModelId = planningModelId;
    }

    @NonNull
    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(@NonNull String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getIngredientTotalWeight() {
        return ingredientTotalWeight;
    }

    public void setIngredientTotalWeight(double ingredientTotalWeight) {
        this.ingredientTotalWeight = ingredientTotalWeight;
    }

    public int getIngredientSelectedProcessIndex() {
        return ingredientSelectedProcessIndex;
    }

    public void setIngredientSelectedProcessIndex(int ingredientSelectedProcessIndex) {
        this.ingredientSelectedProcessIndex = ingredientSelectedProcessIndex;
    }

    @Override
    public String toString() {
        return "PlanningEntity{" +
                "planningModelId=" + planningModelId +
                ", ingredientName='" + ingredientName + '\'' +
                ", ingredientTotalWeight=" + ingredientTotalWeight +
                ", ingredientSelectedProcessIndex=" + ingredientSelectedProcessIndex +
                '}';
    }
}

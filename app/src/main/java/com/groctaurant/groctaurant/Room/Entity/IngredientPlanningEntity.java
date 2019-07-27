package com.groctaurant.groctaurant.Room.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.groctaurant.groctaurant.Model.IngredientPlanningModel;

/**
 * Created by Danish Rafique on 31-03-2019.
 */

@Entity(tableName = "ingredient_planning")
public class IngredientPlanningEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ingredient_planning_id")
    public int ingredientPlanningId;

    @NonNull
    @ColumnInfo(name = "planning_model_id")
    public int planningModelId;

    @NonNull
    @ColumnInfo(name = "ingredient_processing")
    public String ingredientProcessing;

    @NonNull
    @ColumnInfo(name = "ingredient_number_of_units")
    public int ingredientNumberOfUnits;

    @NonNull
    @ColumnInfo(name = "ingredient_number_of_units_packed")
    public int ingredientNumberOfUnitsPacked;

    @NonNull
    @ColumnInfo(name = "ingredient_consolidated_weight")
    public double ingredientConsolidatedWeight;

    @NonNull
    @ColumnInfo(name = "ingredient_planning_selected_position")
    public int ingredientPlanningSelectedPosition;

    public IngredientPlanningEntity(IngredientPlanningModel ingredientPlanningModel) {
        this.ingredientPlanningId = ingredientPlanningModel.getIngredientPlanningId();
        this.planningModelId = ingredientPlanningModel.getPlanningModelId();
        this.ingredientProcessing = ingredientPlanningModel.getIngredientProcessing();
        this.ingredientNumberOfUnits = ingredientPlanningModel.getIngredientNumberOfUnits();
        this.ingredientNumberOfUnitsPacked = ingredientPlanningModel.getIngredientNumberOfUnitsPacked();
        this.ingredientConsolidatedWeight = ingredientPlanningModel.getIngredientConsolidatedWeight();
        this.ingredientPlanningSelectedPosition = ingredientPlanningModel.getIngredientPlanningSelectedPosition();
    }

    public IngredientPlanningEntity(int ingredientPlanningId, int planningModelId, @NonNull String ingredientProcessing, int ingredientNumberOfUnits, int ingredientNumberOfUnitsPacked, double ingredientConsolidatedWeight, int ingredientPlanningSelectedPosition) {
        this.ingredientPlanningId = ingredientPlanningId;
        this.planningModelId = planningModelId;
        this.ingredientProcessing = ingredientProcessing;
        this.ingredientNumberOfUnits = ingredientNumberOfUnits;
        this.ingredientNumberOfUnitsPacked = ingredientNumberOfUnitsPacked;
        this.ingredientConsolidatedWeight = ingredientConsolidatedWeight;
        this.ingredientPlanningSelectedPosition = ingredientPlanningSelectedPosition;
    }

    public int getIngredientPlanningId() {
        return ingredientPlanningId;
    }

    public void setIngredientPlanningId(int ingredientPlanningId) {
        this.ingredientPlanningId = ingredientPlanningId;
    }

    public int getPlanningModelId() {
        return planningModelId;
    }

    public void setPlanningModelId(int planningModelId) {
        this.planningModelId = planningModelId;
    }

    @NonNull
    public String getIngredientProcessing() {
        return ingredientProcessing;
    }

    public void setIngredientProcessing(@NonNull String ingredientProcessing) {
        this.ingredientProcessing = ingredientProcessing;
    }

    public int getIngredientNumberOfUnits() {
        return ingredientNumberOfUnits;
    }

    public void setIngredientNumberOfUnits(int ingredientNumberOfUnits) {
        this.ingredientNumberOfUnits = ingredientNumberOfUnits;
    }

    public int getIngredientNumberOfUnitsPacked() {
        return ingredientNumberOfUnitsPacked;
    }

    public void setIngredientNumberOfUnitsPacked(int ingredientNumberOfUnitsPacked) {
        this.ingredientNumberOfUnitsPacked = ingredientNumberOfUnitsPacked;
    }

    public double getIngredientConsolidatedWeight() {
        return ingredientConsolidatedWeight;
    }

    public void setIngredientConsolidatedWeight(double ingredientConsolidatedWeight) {
        this.ingredientConsolidatedWeight = ingredientConsolidatedWeight;
    }

    public int getIngredientPlanningSelectedPosition() {
        return ingredientPlanningSelectedPosition;
    }

    public void setIngredientPlanningSelectedPosition(int ingredientPlanningSelectedPosition) {
        this.ingredientPlanningSelectedPosition = ingredientPlanningSelectedPosition;
    }

    @Override
    public String toString() {
        return "IngredientPlanningEntity{" +
                "ingredientPlanningId=" + ingredientPlanningId +
                ", planningModelId=" + planningModelId +
                ", ingredientProcessing='" + ingredientProcessing + '\'' +
                ", ingredientNumberOfUnits=" + ingredientNumberOfUnits +
                ", ingredientNumberOfUnitsPacked=" + ingredientNumberOfUnitsPacked +
                ", ingredientConsolidatedWeight=" + ingredientConsolidatedWeight +
                ", ingredientPlanningSelectedPosition=" + ingredientPlanningSelectedPosition +
                '}';
    }
}

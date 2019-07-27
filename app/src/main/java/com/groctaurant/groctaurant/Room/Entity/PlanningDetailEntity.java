package com.groctaurant.groctaurant.Room.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.groctaurant.groctaurant.Model.PlanningDetailModel;

/**
 * Created by Danish Rafique on 02-04-2019.
 */

@Entity(tableName = "planning_detail")
public class PlanningDetailEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "planning_detail_id")
    public int planningDetailId;

    @NonNull
    @ColumnInfo(name = "ingredient_planning_id")
    public int ingredientPlanningId;

    @NonNull
    @ColumnInfo(name = "planning_model_id")
    public int planningModelId;

    @NonNull
    @ColumnInfo(name = "ingredient_id")
    public String ingredientId;

    @NonNull
    @ColumnInfo(name = "ingredient_detail_id")
    public String ingredientDetailId;

    @NonNull
    @ColumnInfo(name = "slip_name")
    public String slipName;

    @NonNull
    @ColumnInfo(name = "item_name")
    public String itemName;

    @NonNull
    @ColumnInfo(name = "ingredient_weight")
    public double ingredientWeight;

    @NonNull
    @ColumnInfo(name = "ingredient_is_packed")
    public boolean ingredientIsPacked;

    public PlanningDetailEntity(int planningDetailId, int ingredientPlanningId, int planningModelId, @NonNull String ingredientId, @NonNull String ingredientDetailId, @NonNull String slipName, @NonNull String itemName, double ingredientWeight, boolean ingredientIsPacked) {
        this.planningDetailId = planningDetailId;
        this.ingredientPlanningId = ingredientPlanningId;
        this.planningModelId = planningModelId;
        this.ingredientId = ingredientId;
        this.ingredientDetailId = ingredientDetailId;
        this.slipName = slipName;
        this.itemName = itemName;
        this.ingredientWeight = ingredientWeight;
        this.ingredientIsPacked = ingredientIsPacked;
    }

    public PlanningDetailEntity(PlanningDetailModel planningDetailModel) {
        this.planningDetailId = planningDetailModel.getPlanningDetailId();
        this.ingredientPlanningId = planningDetailModel.getIngredientPlanningId();
        this.planningModelId = planningDetailModel.getPlanningModelId();
        this.ingredientId = planningDetailModel.getIngredientId();
        this.ingredientDetailId = planningDetailModel.getIngredientDetailId();
        this.slipName = planningDetailModel.getSlipName();
        this.itemName = planningDetailModel.getItemName();
        this.ingredientWeight = planningDetailModel.getIngredientWeight();
        this.ingredientIsPacked = planningDetailModel.isIngredientIsPacked();
    }

    public int getPlanningDetailId() {
        return planningDetailId;
    }

    public void setPlanningDetailId(int planningDetailId) {
        this.planningDetailId = planningDetailId;
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
    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(@NonNull String ingredientId) {
        this.ingredientId = ingredientId;
    }

    @NonNull
    public String getIngredientDetailId() {
        return ingredientDetailId;
    }

    public void setIngredientDetailId(@NonNull String ingredientDetailId) {
        this.ingredientDetailId = ingredientDetailId;
    }

    @NonNull
    public String getSlipName() {
        return slipName;
    }

    public void setSlipName(@NonNull String slipName) {
        this.slipName = slipName;
    }

    @NonNull
    public String getItemName() {
        return itemName;
    }

    public void setItemName(@NonNull String itemName) {
        this.itemName = itemName;
    }

    public double getIngredientWeight() {
        return ingredientWeight;
    }

    public void setIngredientWeight(double ingredientWeight) {
        this.ingredientWeight = ingredientWeight;
    }

    public boolean isIngredientIsPacked() {
        return ingredientIsPacked;
    }

    public void setIngredientIsPacked(boolean ingredientIsPacked) {
        this.ingredientIsPacked = ingredientIsPacked;
    }

    @Override
    public String toString() {
        return "PlanningDetailEntity{" +
                "planningDetailId=" + planningDetailId +
                ", ingredientPlanningId=" + ingredientPlanningId +
                ", planningModelId=" + planningModelId +
                ", ingredientId='" + ingredientId + '\'' +
                ", ingredientDetailId='" + ingredientDetailId + '\'' +
                ", slipName='" + slipName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", ingredientWeight=" + ingredientWeight +
                ", ingredientIsPacked=" + ingredientIsPacked +
                '}';
    }
}

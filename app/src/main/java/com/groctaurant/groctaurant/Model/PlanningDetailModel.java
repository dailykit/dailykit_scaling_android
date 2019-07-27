package com.groctaurant.groctaurant.Model;

import com.groctaurant.groctaurant.Room.Entity.PlanningDetailEntity;

import java.io.Serializable;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public class PlanningDetailModel implements Serializable {

    private int planningDetailId;
    private int ingredientPlanningId;
    private int planningModelId;
    private String ingredientId;
    private String ingredientDetailId;
    private String slipName;
    private String itemName;
    private double ingredientWeight;
    private boolean ingredientIsPacked;

    public PlanningDetailModel(){

    }

    public PlanningDetailModel(PlanningDetailEntity planningDetailEntity) {
        this.planningDetailId = planningDetailEntity.getPlanningDetailId();
        this.ingredientPlanningId = planningDetailEntity.getIngredientPlanningId();
        this.planningModelId = planningDetailEntity.getPlanningModelId();
        this.ingredientId = planningDetailEntity.getIngredientId();
        this.ingredientDetailId = planningDetailEntity.getIngredientDetailId();
        this.slipName = planningDetailEntity.getSlipName();
        this.itemName = planningDetailEntity.getItemName();
        this.ingredientWeight = planningDetailEntity.getIngredientWeight();
        this.ingredientIsPacked = planningDetailEntity.isIngredientIsPacked();
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

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientDetailId() {
        return ingredientDetailId;
    }

    public void setIngredientDetailId(String ingredientDetailId) {
        this.ingredientDetailId = ingredientDetailId;
    }

    public String getSlipName() {
        return slipName;
    }

    public void setSlipName(String slipName) {
        this.slipName = slipName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
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
        return "PlanningDetailModel{" +
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

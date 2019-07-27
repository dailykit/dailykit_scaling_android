package com.groctaurant.groctaurant.Model;

import com.groctaurant.groctaurant.Room.Entity.IngredientPlanningEntity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public class IngredientPlanningModel implements Serializable {

    private int ingredientPlanningId;
    private int planningModelId;
    private String ingredientProcessing;
    private int ingredientNumberOfUnits;
    private int ingredientNumberOfUnitsPacked;
    private double ingredientConsolidatedWeight;
    private int ingredientPlanningSelectedPosition;
    private ArrayList<PlanningDetailModel> planningDetailModelArrayList;

    public IngredientPlanningModel(){

    }

    public IngredientPlanningModel(IngredientPlanningEntity ingredientPlanningEntity) {
        this.ingredientPlanningId = ingredientPlanningEntity.getIngredientPlanningId();
        this.planningModelId = ingredientPlanningEntity.getPlanningModelId();
        this.ingredientProcessing = ingredientPlanningEntity.getIngredientProcessing();
        this.ingredientNumberOfUnits = ingredientPlanningEntity.getIngredientNumberOfUnits();
        this.ingredientNumberOfUnitsPacked = ingredientPlanningEntity.getIngredientNumberOfUnitsPacked();
        this.ingredientConsolidatedWeight = ingredientPlanningEntity.getIngredientConsolidatedWeight();
        this.ingredientPlanningSelectedPosition = ingredientPlanningEntity.getIngredientPlanningSelectedPosition();
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

    public String getIngredientProcessing() {
        return ingredientProcessing;
    }

    public void setIngredientProcessing(String ingredientProcessing) {
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

    public ArrayList<PlanningDetailModel> getPlanningDetailModelArrayList() {
        return planningDetailModelArrayList;
    }

    public void setPlanningDetailModelArrayList(ArrayList<PlanningDetailModel> planningDetailModelArrayList) {
        this.planningDetailModelArrayList = planningDetailModelArrayList;
    }

    @Override
    public String toString() {
        return "IngredientPlanningModel{" +
                "ingredientPlanningId=" + ingredientPlanningId +
                ", planningModelId=" + planningModelId +
                ", ingredientProcessing='" + ingredientProcessing + '\'' +
                ", ingredientNumberOfUnits=" + ingredientNumberOfUnits +
                ", ingredientNumberOfUnitsPacked=" + ingredientNumberOfUnitsPacked +
                ", ingredientConsolidatedWeight=" + ingredientConsolidatedWeight +
                ", ingredientPlanningSelectedPosition=" + ingredientPlanningSelectedPosition +
                ", planningDetailModelArrayList=" + planningDetailModelArrayList +
                '}';
    }
}

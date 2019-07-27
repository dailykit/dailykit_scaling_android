package com.groctaurant.groctaurant.Model;

import com.groctaurant.groctaurant.Room.Entity.PlanningEntity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public class PlanningModel implements Serializable {

    private int planningModelId;
    private String ingredientName;
    private double ingredientTotalWeight;
    private int ingredientSelectedProcessIndex;
    private ArrayList<IngredientPlanningModel> ingredientPlanningModels;

    public PlanningModel(){

    }

    public PlanningModel(PlanningEntity planningEntity) {
        this.planningModelId = planningEntity.getPlanningModelId();
        this.ingredientName = planningEntity.getIngredientName();
        this.ingredientTotalWeight = planningEntity.getIngredientTotalWeight();
        this.ingredientSelectedProcessIndex = planningEntity.getIngredientSelectedProcessIndex();
    }


    public int getPlanningModelId() {
        return planningModelId;
    }

    public void setPlanningModelId(int planningModelId) {
        this.planningModelId = planningModelId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
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

    public ArrayList<IngredientPlanningModel> getIngredientPlanningModels() {
        return ingredientPlanningModels;
    }

    public void setIngredientPlanningModels(ArrayList<IngredientPlanningModel> ingredientPlanningModels) {
        this.ingredientPlanningModels = ingredientPlanningModels;
    }

    @Override
    public String toString() {
        return "PlanningModel{" +
                "planningModelId=" + planningModelId +
                ", ingredientName='" + ingredientName + '\'' +
                ", ingredientTotalWeight=" + ingredientTotalWeight +
                ", ingredientSelectedProcessIndex=" + ingredientSelectedProcessIndex +
                ", ingredientPlanningModels=" + ingredientPlanningModels +
                '}';
    }
}

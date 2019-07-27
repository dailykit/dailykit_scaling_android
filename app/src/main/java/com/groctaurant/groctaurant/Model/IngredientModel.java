package com.groctaurant.groctaurant.Model;

import java.io.Serializable;

/**
 * Created by Danish Rafique on 26-07-2018.
 */
public class IngredientModel implements Serializable {

    private String ingredientId;
    private String ingredientName;
    private double ingredientQuantity;
    private String ingredientMsr;
    private String ingredientSection;
    private String ingredientProcess;
    private boolean isPacked;

    public boolean isPacked() {
        return isPacked;
    }

    public void setPacked(boolean packed) {
        isPacked = packed;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getIngredientQuantity() {
        return ingredientQuantity;
    }

    public void setIngredientQuantity(double ingredientQuantity) {
        this.ingredientQuantity = ingredientQuantity;
    }

    public String getIngredientMsr() {
        return ingredientMsr;
    }

    public void setIngredientMsr(String ingredientMsr) {
        this.ingredientMsr = ingredientMsr;
    }

    public String getIngredientSection() {
        return ingredientSection;
    }

    public void setIngredientSection(String ingredientSection) {
        this.ingredientSection = ingredientSection;
    }

    public String getIngredientProcess() {
        return ingredientProcess;
    }

    public void setIngredientProcess(String ingredientProcess) {
        this.ingredientProcess = ingredientProcess;
    }




}

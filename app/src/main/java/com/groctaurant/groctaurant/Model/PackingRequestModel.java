package com.groctaurant.groctaurant.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PackingRequestModel {

    @SerializedName("ingredient_id")
    @Expose
    private String ingredientId;

    @SerializedName("is_weighed")
    @Expose
    private boolean isWeighed;

    @SerializedName("is_packed")
    @Expose
    private boolean isPacked;

    @SerializedName("is_labelled")
    @Expose
    private boolean isLabelled;

    public PackingRequestModel(String ingredientId, boolean isWeighed, boolean isPacked, boolean isLabelled) {
        this.ingredientId = ingredientId;
        this.isWeighed = isWeighed;
        this.isPacked = isPacked;
        this.isLabelled = isLabelled;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public boolean isWeighed() {
        return isWeighed;
    }

    public void setWeighed(boolean weighed) {
        isWeighed = weighed;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public void setPacked(boolean packed) {
        isPacked = packed;
    }

    public boolean isLabelled() {
        return isLabelled;
    }

    public void setLabelled(boolean labelled) {
        isLabelled = labelled;
    }

    @Override
    public String toString() {
        return "PackingRequestModel{" +
                "ingredientId='" + ingredientId + '\'' +
                ", isWeighed=" + isWeighed +
                ", isPacked=" + isPacked +
                ", isLabelled=" + isLabelled +
                '}';
    }
}

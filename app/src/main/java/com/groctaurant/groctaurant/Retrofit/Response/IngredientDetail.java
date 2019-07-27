
package com.groctaurant.groctaurant.Retrofit.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class IngredientDetail implements Serializable {

    private final static long serialVersionUID = -8787627594304571614L;
    @SerializedName("ingredient_detail_id")
    @Expose
    private String ingredientDetailId;
    @SerializedName("ingredient_name")
    @Expose
    private String ingredientName;
    @SerializedName("ingredient_quantity")
    @Expose
    private String ingredientQuantity;
    @SerializedName("ingredient_measure")
    @Expose
    private String ingredientMeasure;
    @SerializedName("ingredient_section")
    @Expose
    private String ingredientSection;
    @SerializedName("ingredient_process")
    @Expose
    private String ingredientProcess;

    /**
     * No args constructor for use in serialization
     */
    public IngredientDetail() {
    }

    /**
     * @param ingredientSection
     * @param ingredientMeasure
     * @param ingredientName
     * @param ingredientQuantity
     * @param ingredientDetailId
     * @param ingredientProcess
     */
    public IngredientDetail(String ingredientDetailId, String ingredientName, String ingredientQuantity, String ingredientMeasure, String ingredientSection, String ingredientProcess) {
        super();
        this.ingredientDetailId = ingredientDetailId;
        this.ingredientName = ingredientName;
        this.ingredientQuantity = ingredientQuantity;
        this.ingredientMeasure = ingredientMeasure;
        this.ingredientSection = ingredientSection;
        this.ingredientProcess = ingredientProcess;
    }

    public String getIngredientDetailId() {
        return ingredientDetailId;
    }

    public void setIngredientDetailId(String ingredientDetailId) {
        this.ingredientDetailId = ingredientDetailId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientQuantity() {
        return ingredientQuantity;
    }

    public void setIngredientQuantity(String ingredientQuantity) {
        this.ingredientQuantity = ingredientQuantity;
    }

    public String getIngredientMeasure() {
        return ingredientMeasure;
    }

    public void setIngredientMeasure(String ingredientMeasure) {
        this.ingredientMeasure = ingredientMeasure;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("ingredientDetailId", ingredientDetailId).append("ingredientName", ingredientName).append("ingredientQuantity", ingredientQuantity).append("ingredientMeasure", ingredientMeasure).append("ingredientSection", ingredientSection).append("ingredientProcess", ingredientProcess).toString();
    }

}

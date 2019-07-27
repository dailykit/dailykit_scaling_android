
package com.groctaurant.groctaurant.Retrofit.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class Ingredient implements Serializable {

    private final static long serialVersionUID = -8695150093516410909L;
    @SerializedName("ingredient_id")
    @Expose
    private String ingredientId;
    @SerializedName("ingredient_index")
    @Expose
    private String ingredientIndex;
    @SerializedName("slip_name")
    @Expose
    private String slipName;
    @SerializedName("ingredient_detail")
    @Expose
    private List<IngredientDetail> ingredientDetail = null;

    /**
     * No args constructor for use in serialization
     */
    public Ingredient() {
    }

    /**
     * @param ingredientId
     * @param ingredientIndex
     * @param ingredientDetail
     * @param slipName
     */
    public Ingredient(String ingredientId, String ingredientIndex, String slipName, List<IngredientDetail> ingredientDetail) {
        super();
        this.ingredientId = ingredientId;
        this.ingredientIndex = ingredientIndex;
        this.slipName = slipName;
        this.ingredientDetail = ingredientDetail;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientIndex() {
        return ingredientIndex;
    }

    public void setIngredientIndex(String ingredientIndex) {
        this.ingredientIndex = ingredientIndex;
    }

    public String getSlipName() {
        return slipName;
    }

    public void setSlipName(String slipName) {
        this.slipName = slipName;
    }

    public List<IngredientDetail> getIngredientDetail() {
        return ingredientDetail;
    }

    public void setIngredientDetail(List<IngredientDetail> ingredientDetail) {
        this.ingredientDetail = ingredientDetail;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("ingredientId", ingredientId).append("ingredientIndex", ingredientIndex).append("slipName", slipName).append("ingredientDetail", ingredientDetail).toString();
    }

}

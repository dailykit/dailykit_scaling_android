package com.groctaurant.groctaurant.Room.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.groctaurant.groctaurant.Room.Converter.IngredientDetailConverter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Danish Rafique on 23-08-2018.
 */

@Entity(tableName = "ingredient")
public class IngredientEntity implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ingredient_id")
    public String ingredientId;

    @ColumnInfo(name = "item_id")
    public String ingredientItemId;

    @ColumnInfo(name = "ingredient_index")
    public String ingredientIndex;

    @ColumnInfo(name = "slip_name")
    public String slipName;

    @ColumnInfo(name = "ingredient_is_packed_complete")
    public boolean isPackedComplete;

    @ColumnInfo(name = "ingredient_is_deleted")
    public boolean isDeleted;

    @ColumnInfo(name = "ingredient_is_labeled")
    public boolean isLabeled;

    @ColumnInfo(name = "selected_ingredient_position")
    public int selectedIngredientPosition;

    @ColumnInfo(name = "ingredient_measured_total_weight")
    public double ingredientMeasuredTotalWeight;

    @ColumnInfo(name = "ingredient_detail")
    @TypeConverters(IngredientDetailConverter.class)
    public ArrayList<IngredientDetailEntity> ingredientEntity;

    @NonNull
    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(@NonNull String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientItemId() {
        return ingredientItemId;
    }

    public void setIngredientItemId(String ingredientItemId) {
        this.ingredientItemId = ingredientItemId;
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

    public boolean isPackedComplete() {
        return isPackedComplete;
    }

    public void setPackedComplete(boolean packedComplete) {
        isPackedComplete = packedComplete;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isLabeled() {
        return isLabeled;
    }

    public void setLabeled(boolean labeled) {
        isLabeled = labeled;
    }

    public int getSelectedIngredientPosition() {
        return selectedIngredientPosition;
    }

    public void setSelectedIngredientPosition(int selectedIngredientPosition) {
        this.selectedIngredientPosition = selectedIngredientPosition;
    }

    public double getIngredientMeasuredTotalWeight() {
        return ingredientMeasuredTotalWeight;
    }

    public void setIngredientMeasuredTotalWeight(double ingredientMeasuredTotalWeight) {
        this.ingredientMeasuredTotalWeight = ingredientMeasuredTotalWeight;
    }

    public ArrayList<IngredientDetailEntity> getIngredientEntity() {
        return ingredientEntity;
    }

    public void setIngredientEntity(ArrayList<IngredientDetailEntity> ingredientEntity) {
        this.ingredientEntity = ingredientEntity;
    }

    @Override
    public String toString() {
        return "IngredientEntity{" +
                "ingredientId='" + ingredientId + '\'' +
                ", ingredientItemId='" + ingredientItemId + '\'' +
                ", ingredientIndex='" + ingredientIndex + '\'' +
                ", slipName='" + slipName + '\'' +
                ", isPackedComplete=" + isPackedComplete +
                ", isDeleted=" + isDeleted +
                ", isLabeled=" + isLabeled +
                ", selectedIngredientPosition=" + selectedIngredientPosition +
                ", ingredientMeasuredTotalWeight=" + ingredientMeasuredTotalWeight +
                ", ingredientEntity=" + ingredientEntity +
                '}';
    }
}

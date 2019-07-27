package com.groctaurant.groctaurant.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Danish Rafique on 26-07-2018.
 */
public class AllIngredientModel implements Serializable {

    private ArrayList<IngredientModel> ingredientModelArrayList;
    private String ingredientSlipName;

    public ArrayList<IngredientModel> getIngredientModelArrayList() {
        return ingredientModelArrayList;
    }

    public void setIngredientModelArrayList(ArrayList<IngredientModel> ingredientModelArrayList) {
        this.ingredientModelArrayList = ingredientModelArrayList;
    }

    public String getIngredientSlipName() {
        return ingredientSlipName;
    }

    public void setIngredientSlipName(String ingredientSlipName) {
        this.ingredientSlipName = ingredientSlipName;
    }

}

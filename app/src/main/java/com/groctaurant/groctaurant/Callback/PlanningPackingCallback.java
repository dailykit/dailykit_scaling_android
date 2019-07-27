package com.groctaurant.groctaurant.Callback;

import com.groctaurant.groctaurant.Model.IngredientPlanningModel;
import com.groctaurant.groctaurant.Model.PlanningDetailModel;

import java.util.ArrayList;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public interface PlanningPackingCallback {

    void updateTabList();

    void updatePackingList(IngredientPlanningModel ingredientPlanningModel);

    void changeTareDisplay();

    void setScreenDetail(PlanningDetailModel planningDetailModel);

}

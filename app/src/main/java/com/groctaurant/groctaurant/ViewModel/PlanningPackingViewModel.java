package com.groctaurant.groctaurant.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.groctaurant.groctaurant.Callback.PlanningPackingCallback;
import com.groctaurant.groctaurant.Model.IngredientPlanningModel;
import com.groctaurant.groctaurant.Model.PlanningDetailModel;
import com.groctaurant.groctaurant.Model.PlanningModel;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientPlanningEntity;
import com.groctaurant.groctaurant.Room.Entity.PlanningDetailEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danish Rafique on 10-03-2019.
 */
public class PlanningPackingViewModel extends AndroidViewModel {

    private static final String TAG = "PlanningPackingVM";
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private GroctaurantDatabase groctaurantDatabase;
    private GroctaurantExecutor groctaurantExecutor;
    private Type type;
    PlanningDetailModel planningDetailModel;
    boolean gotNextIndex=false;

    public PlanningPackingViewModel(@NonNull Application application) {
        super(application);
        sharedpreferences = AppUtil.getAppPreferences(application);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(application, GroctaurantDatabase.class, "Development").allowMainThreadQueries().build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
    }

    public PlanningModel getSelectedPlanningModel(){

        int selectedPlanningModelId= sharedpreferences.getInt(Constants.SELECTED_PLANNING_MODEL_ID, -1);
        PlanningModel planningModel=new PlanningModel(groctaurantDatabase.planningDao().fetchByPlanningId(selectedPlanningModelId));
        List<IngredientPlanningEntity> ingredientPlanningEntityList=groctaurantDatabase.ingredientPlanningDao().load(selectedPlanningModelId);
        ArrayList<IngredientPlanningModel> ingredientPlanningModelArrayList=new ArrayList<>();
        for(IngredientPlanningEntity ingredientPlanningEntity:ingredientPlanningEntityList){
            IngredientPlanningModel ingredientPlanningModel=new IngredientPlanningModel(ingredientPlanningEntity);
            List<PlanningDetailEntity> planningDetailEntityList=groctaurantDatabase.planningDetailDao().load(ingredientPlanningEntity.getPlanningModelId(),ingredientPlanningEntity.getIngredientPlanningId());
            ArrayList<PlanningDetailModel> planningDetailModelArrayList=new ArrayList<>();
            for(PlanningDetailEntity planningDetailEntity:planningDetailEntityList){
                planningDetailModelArrayList.add(new PlanningDetailModel(planningDetailEntity));
            }
            ingredientPlanningModel.setPlanningDetailModelArrayList(planningDetailModelArrayList);
            ingredientPlanningModelArrayList.add(ingredientPlanningModel);
        }
        planningModel.setIngredientPlanningModels(ingredientPlanningModelArrayList);
        Log.e(TAG,planningModel.toString());

        return planningModel;
    }

    public void setSelectedPlanningPosition(PlanningPackingCallback planningPackingCallback,int position){
        planningPackingCallback.updatePackingList(getSelectedPlanningModel().getIngredientPlanningModels().get(getSelectedPlanningModel().getIngredientSelectedProcessIndex()));
    }

    public void setNextSelectIndex(PlanningPackingCallback planningPackingCallback){
        PlanningModel planningModel=getSelectedPlanningModel();
        int selectedIndex=-1;
        gotNextIndex=false;
        IngredientPlanningModel ingredientPlanningModel=planningModel.getIngredientPlanningModels().get(planningModel.getIngredientSelectedProcessIndex());
        int currentIndex = ingredientPlanningModel.getIngredientPlanningSelectedPosition();
        for(int i=currentIndex;i<ingredientPlanningModel.getPlanningDetailModelArrayList().size();i++){
            planningDetailModel=ingredientPlanningModel.getPlanningDetailModelArrayList().get(i);
            if(!planningDetailModel.isIngredientIsPacked()){
                groctaurantDatabase.ingredientPlanningDao().setSelectedPosition(planningDetailModel.getIngredientPlanningId(),i);
                gotNextIndex=true;
                selectedIndex=i;
                break;
            }
        }
        if(!gotNextIndex){
            for(int i=0;i<currentIndex;i++){
                planningDetailModel=ingredientPlanningModel.getPlanningDetailModelArrayList().get(i);
                if(!planningDetailModel.isIngredientIsPacked()){
                    groctaurantDatabase.ingredientPlanningDao().setSelectedPosition(planningDetailModel.getIngredientPlanningId(),i);
                    gotNextIndex=true;
                    selectedIndex=i;
                    break;
                }
            }
        }

        if(!gotNextIndex){
            groctaurantDatabase.ingredientPlanningDao().setSelectedPosition(planningDetailModel.getIngredientPlanningId(),-2);
        }
        else {
            editor.putString(Constants.SELECTED_PLANNING_INGREDIENT_MODEL, new Gson().toJson(ingredientPlanningModel.getPlanningDetailModelArrayList().get(selectedIndex)));
            editor.commit();
            planningPackingCallback.setScreenDetail(ingredientPlanningModel.getPlanningDetailModelArrayList().get(selectedIndex));
        }
        planningPackingCallback.updatePackingList(getSelectedPlanningModel().getIngredientPlanningModels().get(getSelectedPlanningModel().getIngredientSelectedProcessIndex()));
    }

    public PlanningDetailModel getSelectedPlanningDetail(){
        Type type = new TypeToken<PlanningDetailModel>() {}.getType();
        return new Gson().fromJson(sharedpreferences.getString(Constants.SELECTED_PLANNING_INGREDIENT_MODEL, ""), type);
    }

}

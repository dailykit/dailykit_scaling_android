package com.groctaurant.groctaurant.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.groctaurant.groctaurant.Callback.PlanningCallback;
import com.groctaurant.groctaurant.Model.IngredientPlanningModel;
import com.groctaurant.groctaurant.Model.OrderDetailModel;
import com.groctaurant.groctaurant.Model.PlanningDetailModel;
import com.groctaurant.groctaurant.Model.PlanningModel;
import com.groctaurant.groctaurant.Room.DAO.PlanningDetailDao;
import com.groctaurant.groctaurant.Room.Database.GroctaurantDatabase;
import com.groctaurant.groctaurant.Room.Database.GroctaurantExecutor;
import com.groctaurant.groctaurant.Room.Entity.IngredientDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.IngredientPlanningEntity;
import com.groctaurant.groctaurant.Room.Entity.PlanningDetailEntity;
import com.groctaurant.groctaurant.Room.Entity.PlanningEntity;
import com.groctaurant.groctaurant.Utils.AppUtil;
import com.groctaurant.groctaurant.Utils.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danish Rafique on 09-03-2019.
 */

public class PlanningViewModel extends AndroidViewModel {

    private static final String TAG = "PlanningViewModel";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GroctaurantDatabase groctaurantDatabase;
    GroctaurantExecutor groctaurantExecutor;
    Type type;
    int planningModelId=0,ingredientPlanningId=0,planningDetailId=0;

    public PlanningViewModel(@NonNull Application application) {
        super(application);
        sharedpreferences = AppUtil.getAppPreferences(application);
        editor = sharedpreferences.edit();
        groctaurantDatabase = Room.databaseBuilder(application, GroctaurantDatabase.class, "Development").allowMainThreadQueries().build();
        groctaurantExecutor = GroctaurantExecutor.getInstance();
    }

    public void getStats(){
        ArrayList<PlanningModel> planningModelArrayList=new ArrayList<>();
        groctaurantDatabase.planningDao().deleteAll();
        groctaurantDatabase.ingredientPlanningDao().deleteAll();
        groctaurantDatabase.planningDetailDao().deleteAll();
        //Log.e(TAG,"Number of Order :" + groctaurantDatabase.orderDao().countOrder());
        //Log.e(TAG,"Number of Item :" + groctaurantDatabase.itemDao().countItemDao());
        //Log.e(TAG,"Number of Ingredient :"+groctaurantDatabase.ingredientDao().countIngredientDao());
        //Log.e(TAG,"Number of Ingredient Detail :"+groctaurantDatabase.ingredientDetailDao().countIngredientDetailDao());
        List<String> uniqueIngredientNameList=groctaurantDatabase.ingredientDetailDao().getDistinctIngredientName();
        //Log.e(TAG,"Number of Distinct Slip Name :"+uniqueIngredientNameList.size());
        //Log.e(TAG,"------------------------------------------------------");
        for(String uniqueIngredientName:uniqueIngredientNameList){
            PlanningModel planningModel = new PlanningModel();
            List<Double> totalWeightList=new ArrayList<>();
            List<String> processingList=new ArrayList<>();
            //Log.e(TAG,uniqueIngredientName);
            planningModel.setIngredientName(uniqueIngredientName);
            planningModel.setIngredientSelectedProcessIndex(0);
            List<IngredientDetailEntity> ingredientDetailList=groctaurantDatabase.ingredientDetailDao().getElementByIngredientName(uniqueIngredientName);
            //Log.e(TAG,"Number of Ingredient Ids :"+ingredientDetailList.size());
            //Log.e(TAG,"------------------------------------------------------");
            Double totalWeight = getTotalWeightByEntity(ingredientDetailList);
            planningModel.setIngredientTotalWeight(totalWeight);
            totalWeightList.add(totalWeight);
            ArrayList<IngredientPlanningModel> ingredientPlanningModelArrayList=new ArrayList<>();
            for(IngredientDetailEntity ingredientDetailEntity:ingredientDetailList){
                String ingredientId=ingredientDetailEntity.getIngredientId();
                String itemName=groctaurantDatabase.itemDao().getItemName(ingredientId.substring(0,ingredientId.lastIndexOf("_")));
                String slipName=groctaurantDatabase.ingredientDao().getSlipNameFromIngredientId(ingredientId);
                //Log.e(TAG,ingredientId+" "+slipName);
                //Log.e(TAG,itemName);
                IngredientPlanningModel ingredientPlanningModel=new IngredientPlanningModel();
                ingredientPlanningModel.setIngredientPlanningSelectedPosition(-1);
                if(!processingList.contains(ingredientDetailEntity.getIngredientProcess())){
                    processingList.add(ingredientDetailEntity.getIngredientProcess());
                    ingredientPlanningModel.setIngredientNumberOfUnits(1);
                    ingredientPlanningModel.setIngredientPlanningId(ingredientPlanningId++);
                    ingredientPlanningModel.setIngredientProcessing(ingredientDetailEntity.getIngredientProcess());
                    ingredientPlanningModel.setIngredientConsolidatedWeight(ingredientDetailEntity.getIngredientQuantity());
                    if(ingredientDetailEntity.isPacked()){
                        ingredientPlanningModel.setIngredientNumberOfUnitsPacked(1);
                    }
                    else{
                        ingredientPlanningModel.setIngredientNumberOfUnitsPacked(0);
                    }
                    PlanningDetailModel planningDetailModel=new PlanningDetailModel();
                    planningDetailModel.setIngredientDetailId(ingredientDetailEntity.getIngredientDetailId());
                    planningDetailModel.setIngredientId(ingredientDetailEntity.getIngredientId());
                    planningDetailModel.setIngredientWeight(ingredientDetailEntity.getIngredientQuantity());
                    planningDetailModel.setSlipName(slipName);
                    planningDetailModel.setItemName(itemName);
                    planningDetailModel.setIngredientIsPacked(ingredientDetailEntity.isPacked());
                    planningDetailModel.setPlanningDetailId(planningDetailId++);
                    planningDetailModel.setIngredientPlanningId(ingredientPlanningId-1);
                    planningDetailModel.setPlanningModelId(planningModelId);
                    ArrayList<PlanningDetailModel> planningDetailModelArrayList=new ArrayList<>();
                    planningDetailModelArrayList.add(planningDetailModel);
                    ingredientPlanningModel.setPlanningDetailModelArrayList(planningDetailModelArrayList);

                }
                else{
                    for(IngredientPlanningModel i: new ArrayList<IngredientPlanningModel>(ingredientPlanningModelArrayList)){
                        if(i.getIngredientProcessing().equals(ingredientDetailEntity.getIngredientProcess())){
                            ingredientPlanningModel=i;
                            ingredientPlanningModelArrayList.remove(i);
                        }
                    }
                    ingredientPlanningModel.setIngredientNumberOfUnits(ingredientPlanningModel.getIngredientNumberOfUnits()+1);
                    ingredientPlanningModel.setIngredientConsolidatedWeight(ingredientPlanningModel.getIngredientConsolidatedWeight()+ingredientDetailEntity.getIngredientQuantity());
                    if(ingredientDetailEntity.isPacked()){
                        ingredientPlanningModel.setIngredientNumberOfUnitsPacked(ingredientPlanningModel.getIngredientNumberOfUnitsPacked()+1);
                    }
                    PlanningDetailModel planningDetailModel=new PlanningDetailModel();
                    planningDetailModel.setIngredientDetailId(ingredientDetailEntity.getIngredientDetailId());
                    planningDetailModel.setIngredientId(ingredientDetailEntity.getIngredientId());
                    planningDetailModel.setIngredientWeight(ingredientDetailEntity.getIngredientQuantity());
                    planningDetailModel.setSlipName(slipName);
                    planningDetailModel.setItemName(itemName);
                    planningDetailModel.setIngredientIsPacked(ingredientDetailEntity.isPacked());
                    planningDetailModel.setPlanningDetailId(planningDetailId++);
                    planningDetailModel.setIngredientPlanningId(ingredientPlanningId-1);
                    planningDetailModel.setPlanningModelId(planningModelId);
                    ingredientPlanningModel.getPlanningDetailModelArrayList().add(planningDetailModel);

                }
                ingredientPlanningModel.setPlanningModelId(planningModelId);
                ingredientPlanningModelArrayList.add(ingredientPlanningModel);
                //Log.e(TAG,ingredientDetailEntity.getIngredientName()+" "+ingredientDetailEntity.getIngredientQuantity()+" "+ingredientDetailEntity.getIngredientProcess()+" "+ingredientDetailEntity.isPacked());
                //Log.e(TAG,".");
            }
            //Log.e(TAG,"------------------------------------------------------");
            //Log.e(TAG,"Total Weight : "+getTotalWeight(totalWeightList));
            //Log.e(TAG,"Number of Processing Types : "+processingList.size());
            //Log.e(TAG,"------------------------------------------------------");
            planningModel.setIngredientPlanningModels(ingredientPlanningModelArrayList);
            planningModel.setPlanningModelId(planningModelId++);
            planningModelArrayList.add(planningModel);
        }


        for(PlanningModel planningModel:planningModelArrayList){
            //Log.e(TAG,planningModel.toString());
            groctaurantDatabase.planningDao().insert(new PlanningEntity(planningModel));
            for(IngredientPlanningModel ingredientPlanningModel: planningModel.getIngredientPlanningModels()){
                groctaurantDatabase.ingredientPlanningDao().insert(new IngredientPlanningEntity(ingredientPlanningModel));
                for(PlanningDetailModel planningDetailModel:ingredientPlanningModel.getPlanningDetailModelArrayList()){
                    groctaurantDatabase.planningDetailDao().insert(new PlanningDetailEntity(planningDetailModel));
                }
            }
        }
        editor.putString(Constants.PLANNING_MODEL_LIST, new Gson().toJson(planningModelArrayList));
        editor.commit();

        //Log.e(TAG,"Count of Planning Entity : "+groctaurantDatabase.planningDao().countPlanning());
        //Log.e(TAG,"Count of Ingredient Planning Entity : "+groctaurantDatabase.ingredientPlanningDao().countIngredientPlanning());
        //Log.e(TAG,"Count of Planning Detail Entity : "+groctaurantDatabase.planningDetailDao().countPlanningDetail());

    }

    public Double getTotalWeight(List<Double> weightList){
        Double totalWeight=0.0;
        for(Double weight:weightList){
            totalWeight=totalWeight+weight;
        }
        return totalWeight;
    }

    public Double getTotalWeightByEntity(List<IngredientDetailEntity> ingredientDetailEntities){
        Double totalWeight=0.0;
        for(IngredientDetailEntity ingredientDetailEntity:ingredientDetailEntities){
            totalWeight=totalWeight+ingredientDetailEntity.getIngredientQuantity();
        }
        return totalWeight;
    }

    public ArrayList<PlanningModel> getPlanningModelList(){

        type = new TypeToken<ArrayList<PlanningModel>>() {}.getType();

        ArrayList<PlanningModel> planningModelArrayList=new ArrayList<>();

        List<PlanningEntity> planningEntityList=groctaurantDatabase.planningDao().load();

        for(PlanningEntity planningEntity:planningEntityList){
            PlanningModel planningModel=new PlanningModel(planningEntity);
            List<IngredientPlanningEntity> ingredientPlanningEntityList=groctaurantDatabase.ingredientPlanningDao().load(planningModel.getPlanningModelId());
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
            planningModelArrayList.add(planningModel);
        }

        return planningModelArrayList;
        //return new Gson().fromJson(sharedpreferences.getString(Constants.PLANNING_MODEL_LIST, ""), type);
    }

}
